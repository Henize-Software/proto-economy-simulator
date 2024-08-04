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
import henize.proto.lib.commodities.Coal;
import henize.proto.lib.commodities.Commodity;
import henize.proto.lib.commodities.Labor;
import henize.proto.lib.commodities.Tool;
import henize.proto.lib.sim.Access;

import static henize.proto.lib.traders.ComType.LABOR;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class CoalTrader extends Trader  implements Serializable {
    Stack<Tool> tools = new Stack<Tool>();

    public CoalTrader(Access access) {
        super(access);
        traderType = "coal";
        account.setTaxCode(Account.TaxCode.COAL);
        this.address = access.sim.map.GenerateAddress(4, access.config.Fxreg, access.config.Fyreg, access.config.F_xreg , access.config.F_yreg); }

    public CoalTrader(Access access, String id) {
        this(access);
        this.id = id;
        traderType = "coal";
    }
    @Override
    public long getPrice() {
        if(!access.config.collectTax) return super.getPrice();
        return super.getPrice() + (long)(super.getPrice() * (access.config.universalTaxRate / 100) + super.getPrice() * (access.config.coalTaxRate / 100) );
    }
    @Override
    public boolean act() {
        return super.act();
    }

    @Override
    protected boolean stock() {
        //return bid(LABOR, bidMax()  - bidMax() / 2, 1);
        if(com.size() < 25) {
            if(buy(LABOR)) {
                access.dir.setMaxPriority(this, false);
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    @Override
    protected boolean stockSpecialPriority(int quant) {
        return buySpecialPriority(LABOR, quant);
    }
    @Override
    protected long bidMax(){
        return account.getBalance();
    }
    @Override
    protected void receive(Commodity c) {
        super.receive(c);
        if (c instanceof Labor) {
            Tool t = null;
            if (tools.size() > 0) {
                t = tools.pop();
            }
            for (Commodity cl : Coal.Generate(access.comStats, (Labor) c, t)) {
                com.push(cl);
            }
            isStocked = true;

        } else if (c instanceof Tool) {
            tools.push((Tool) c);
        }
    }
}
