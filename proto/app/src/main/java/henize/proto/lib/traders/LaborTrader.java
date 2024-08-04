/* Proto - Economy Simulator
Copyright (C) 2024 Joshua Henize

This program is free software: you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.
If not, see https://www.gnu.org/licenses/. */
package henize.proto.lib.traders;

import java.io.Serializable;
import java.util.Stack;

import henize.proto.lib.bank.Account;
import henize.proto.lib.commodities.Commodity;
import henize.proto.lib.commodities.Food;
import henize.proto.lib.commodities.Labor;
import henize.proto.lib.map.ArrivalPoint;
import henize.proto.lib.map.Traveler;
import henize.proto.lib.sim.Access;

import static henize.proto.lib.map.ArrivalPoint.START;
import static henize.proto.lib.traders.ComType.FOOD;
import static henize.proto.lib.traders.ComType.LABOR;
import static henize.proto.lib.traders.ComType.TRANSPORTATION;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class LaborTrader extends Trader implements Serializable {
    Stack<Food> food = new Stack<Food>();

    Traveler traveler;
    int orderedFood;

    public LaborTrader(Access access, String id) {
        super(access);
        this.address = access.sim.map.GenerateAddress(1,  access.config.Lxreg, access.config.Lyreg, access.config.L_xreg, access.config.L_yreg, access.config.sequencial);
        this.id = id;
        traderType = "labor";
        account.setTaxCode(Account.TaxCode.LABOR);
        if(id == "TRIGGER") { //add one more to create spawn
            com.push(new Labor(access.comStats, this));
        }
        for (int i = 0; i < access.config.INIT_LABOR; i++) {
            com.push(new Labor(access.comStats, this));
        }
        isStocked = true;
        access.traderStats.laborTraderCount++;
        access.traderStats.laborTraderRunningTotalPopCount++;
    }

    public LaborTrader(Access access, Labor[] l) {
        super(access);
        traderType = "labor";
        account.cancel(); //moved above address generation to correct bug of $100 being created.
        account.setTaxCode(Account.TaxCode.LABOR);

        //may throw exception, account canceled before that can happen.
        this.address = access.sim.map.GenerateAddress(1, access.config.Lxreg, access.config.Lyreg, access.config.L_xreg, access.config.L_yreg, access.config.sequencial);    //throws exception of overpopulated

        for (Labor c : l) {
            c.creator = this;
            com.push(c);
        }

        access.traderStats.laborTraderCount++;
        access.traderStats.laborTraderRunningTotalPopCount++;
        this.id = "LAB" + Integer.toString(access.traderStats.laborTraderRunningTotalPopCount);

        isStocked = true;
    }
    @Override
    public long getPrice() {
        if(!access.config.collectTax) return super.getPrice();
        return super.getPrice() + (long)(super.getPrice() * (access.config.universalTaxRate / 100) + super.getPrice() * (access.config.laborTaxRate / 100) );
    }
    @Override
    public void priceDecSignalNotification(Trader t) {
        if(com.size() > access.config.LAB_SPAWN_RATE / 2)
          super.priceDecSignalNotification(t);
    }


    @Override
    protected long calculateTotals(QTP q) {
        q.quant = 1;
        q.tp = 0;
        return getPrice();
    }
    @Override
    public boolean act() {
        execSpecialPrioritySystem();
        //if(access.sim.specialPriority.size() > 0) return true;
        active = false;
        if (isDead) return false;
        useStandardOffsetModel();
        //setTargetPriceOffset();
        adjustPriceOffset();

        if (com.size() > access.config.LAB_SPAWN_RATE && access.traderStats.laborTraderCount < access.config.MAX_LABOR) {
            spawn();
        }

        if (deathCondition()) {
            die();
            for (SpecialOrder o : processingSpecialOrders) {
                access.dir.laborOrderForTrade.push(o);
                access.sim.specialPriority.pop();
            }
            return false;
        } else {
            //           if (isWaiting)
            //               return false;
//            else {
            if (!sale)
                allowPriceDecrease = true; //allowPriceDecrease(false);
            if (!execSpecial)
                active = stock();
//            }
        }
        sale = false;
        if (processingSpecialOrders.size() == 0) {
            if (access.dir.laborOrderForTrade.size() != 0) {
                specialOrders.add(access.dir.laborOrderForTrade.pop());
            }
        } else if (processingSpecialOrders.size() > 1) {
                access.dir.laborOrderForTrade.push(processingSpecialOrders.remove(1));
            }
        return active;
    }
  //  @Override
  //  protected  long setTargetPriceOffset() {
   //     return priceOffset = 0;
    //}
@Override
protected boolean sellSpecialPriority(Trader t, int quant) {
    for (int i = 0; i < quant; i++) {
        super.sellSpecialPriority(t, 1);
    }
    return true;
}
    private void spawn() {
        if (!access.config.LAB_CAN_SPAWN) return;
        Labor[] l = new Labor[access.config.LAB_SPAWN_RATE / 2];
        for (int i = 0; i < access.config.LAB_SPAWN_RATE / 2; i++) {
            l[i] = (Labor)com.pop();
        }

        try {
            Trader n = new LaborTrader(access, l);
            access.dir.add(LABOR, n);
            n.price = this.price;
            n.id += "(spawn)";
        }catch (Throwable e) {
            for(Labor n : l) {
                com.push(n);  //return labor back to com since failed.
            }
        }
    }

    public void pauseTraveler(boolean pause)
    {
        traveler.setWaitStateEnabled(pause);
    }

    protected void die()
    {
        isDead = true;
        access.dir.remove(LABOR, this);
        access.traderStats.laborTraderCount--;
        access.sim.map.RemoveAddress(address, 1);
        account.distribute(LABOR, account.getBalance(), this);
        account.close();
        super.die();
    }

    private boolean deathCondition() {
        return access.config.LAB_CAN_DIE && com.size() == 0 && access.dir.getCount(LABOR) > access.config.MIN_LABOR;
    }
    @Override
    protected  boolean stock() {
        if(orderedFood == 0) {
            if (buy(FOOD, Integer.MAX_VALUE)) {
                orderedFood++;
                return true;
            } else {
                allowPriceDecrease = false;
                //  increasePrice();
                return false;
            }
        }
        return true;


    }
    @Override
    protected long bidMax() {
        return account.getBalance() - access.dir.getLowestPriceOf(TRANSPORTATION);
    }
    @Override
    protected  void receive(Commodity c)
    {
        orderedFood = 0;
        for(Labor l : Labor.Generate(access.comStats, (Food)c, this))
            com.push(l);
        isStocked = true;
    }
    @Override
    protected  boolean  initTransport()
    {
        isWaiting = true;

        traveler = access.sim.map.Transport(address, activeTrade.address);
        traveler.addListener(this);
        traveler.order_for = activeTrade;
        traveler.setQuant(quantity);
        traveler.id = "CAR";
        return true;

    }

    @Override
    public void arrivalNotification(Traveler sender, ArrivalPoint p) {
        super.arrivalNotification(sender, p);
        if (p == START) {
            isWaiting = false;
        }
    }


}
