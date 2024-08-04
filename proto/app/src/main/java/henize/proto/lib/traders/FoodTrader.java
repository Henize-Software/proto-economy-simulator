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
import henize.proto.lib.commodities.Tool;
import henize.proto.lib.sim.Access;

import static henize.proto.App.sim;
import static henize.proto.lib.traders.ComType.LABOR;
import static henize.proto.lib.traders.ComType.TOOL;
import static henize.proto.lib.traders.ComType.TRANSPORTATION;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class FoodTrader extends Trader implements Serializable {
    private Stack<Tool> tools = new Stack<Tool>();
    private Stack<Labor> labor = new Stack<>();
    private int orderedLabor;
    private int orderedTools;


    public FoodTrader(Access access, String id){
        super(access);
        this.address = access.sim.map.GenerateAddress(2, access.config.Fxreg, access.config.Fyreg, access.config.F_xreg , access.config.F_yreg);
        this.id = id;
        account.setTaxCode(Account.TaxCode.FOOD);
        traderType = "food";
    }
    @Override
    public long getPrice() {
        if(!access.config.collectTax) return super.getPrice();
        return super.getPrice() + (long)(super.getPrice() * (access.config.universalTaxRate / 100) + super.getPrice() * (access.config.foodTaxRate / 100) );
    }
    @Override
    public boolean act() {
        boolean retval = super.act();
        if(sim.specialPriority.size() > 0) return true;

        if(com.size() < 5) access.dir.setMaxPriority(this, true);

        if(targetOffset == 0 && tools.isEmpty()) {
            if(buy(TOOL)) {
                orderedTools++;
            }
        }
        return retval;
    }
    @Override
    protected void reset() {
        super.reset();
        orderedLabor = 0;
        orderedTools = 0;
    }
    @Override
    protected boolean stockSpecialPriority(int quant) {
        int k = access.config.TOOL_FOOD;
        int j = quant / k;
        int l = j + (quant - (k * j));
        if(j >= 1) {
            buySpecialPriority(TOOL, j);
            orderedTools += j;
        }
        buySpecialPriority(LABOR, l);
        orderedLabor += l;
        return true;
    }
    @Override
    protected boolean stock() {

        if(orderedLabor < 1) {
            if (buy(LABOR)) {
                orderedLabor++;
                if (orderedLabor + com.size() >= 5) access.dir.setMaxPriority(this, false);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    @Override
    protected long bidMax(){
        return account.getBalance() - access.dir.getAveragePriceOf(TOOL) - access.dir.getLowestPriceOf(TRANSPORTATION);
    }


    @Override
    protected boolean authorizeSale() {
        if(quantity >= access.config.MIN_FOOD_SELL || !processingSpecialOrders.isEmpty()) return true;
          return false;
       // return true;
    }

    @Override
    protected void receive(Commodity c)
    {
        super.receive(c);
        if (c instanceof Tool) {
            tools.push((Tool) c);
            if(orderedTools > 0) orderedTools--;
        }
        if (c instanceof Labor) {
            labor.push((Labor)c);
            if(orderedLabor > 0) orderedLabor--;
        }



    }
    @Override
    protected void generateCom() {
        if(orderedTools == 0) {
            while(labor.size() > 0) {
                Tool t = null;
                if (tools.size() > 0) {
                    t = tools.pop();
                }
                for (Commodity cm : Food.Generate(access.comStats, labor.pop(), t)) {
                    com.push(cm);
                }
                isStocked = true;
            }
        }
    }
}

