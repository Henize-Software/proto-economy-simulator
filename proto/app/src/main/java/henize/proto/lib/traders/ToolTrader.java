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
import henize.proto.lib.commodities.Iron;
import henize.proto.lib.commodities.Labor;
import henize.proto.lib.commodities.Tool;
import henize.proto.lib.sim.Access;

import static henize.proto.lib.traders.ComType.COAL;
import static henize.proto.lib.traders.ComType.IRON;
import static henize.proto.lib.traders.ComType.LABOR;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class ToolTrader extends Trader implements Serializable {
    Stack<Labor> labor = new Stack<Labor>();
    Stack<Coal> coal = new Stack<Coal>();
    Stack<Iron> ir = new Stack<Iron>();

    boolean sc, sir;

    int orderedLabor, orderedCoal, orderedIron;

    public ToolTrader(Access access) {
        super(access);
        traderType = "tool";
        account.setTaxCode(Account.TaxCode.TOOL);
        this.address = access.sim.map.GenerateAddress(3, access.config.Fxreg, access.config.Fyreg, access.config.F_xreg, access.config.F_yreg);
    }

    public ToolTrader(Access access, String id) {
        this(access);
        this.id = id;
    }

    @Override
    public long getPrice() {
        if (!access.config.collectTax) return super.getPrice();
        return super.getPrice() + (long) (super.getPrice() * (access.config.universalTaxRate / 100) + super.getPrice() * (access.config.toolTaxRate / 100));
    }

    private void checkStock() {
        if (orderedLabor >= access.config.REQ_LAB_FOR_TOOL && orderedCoal > 0 && orderedIron > 0) {
            access.dir.setMaxPriority(this, false);
        }

    }

    @Override
    public boolean act() {

        return super.act();


    }

    @Override
    protected void reset() {
        super.reset();
        orderedLabor = 0;
        orderedIron = 0;
        orderedCoal = 0;
    }

    @Override
    protected void fixPriceToLabor() {
        if (fixPrice) setPrice(priceFix());
    }

    @Override
    protected boolean stockSpecialPriority(int quant) {
        int l = access.config.REQ_LAB_FOR_TOOL * quant;
        buySpecialPriority(LABOR, l);
        buySpecialPriority(COAL, quant);
        buySpecialPriority(IRON, quant);
        orderedLabor += l;
        orderedIron += quant;
        orderedCoal += quant;
        return true;
    }

    @Override
    protected boolean stock() {
        super.stock();
        //if(orderedFood > 10 || orderedCoal > 10 || orderedIron > 10)
        // return true;
        boolean success = false;
        if (orderedLabor < access.config.REQ_LAB_FOR_TOOL && labor.size() < access.config.REQ_LAB_FOR_TOOL) {
            if (buy(LABOR)) {
                orderedLabor++;
                success = true;
            }
        }
        if (orderedCoal < 1 && coal.size() < 1) {
            if (buy(COAL)) {
                orderedCoal++;
                success = true;
            }

        }
        if (orderedIron < 1 && ir.size() < 1) {
            if (buy(IRON)) {
                orderedIron++;
                success = true;
            }
        }

        checkStock();

        return success;
    }

    private long priceFix() {
        // if(access.config.supplyAndDemandEnabled) {
        //     return (access.dir.getHighestPriceOf(LABOR) * access.config.REQ_LAB_FOR_TOOL) + (access.dir.getHighestPriceOf(IRON) + access.dir.getHighestPriceOf(COAL)) * 2;
        //   } else {
        return (access.dir.getAveragePriceOf(LABOR) * access.config.REQ_LAB_FOR_TOOL) + (access.dir.getAveragePriceOf(IRON) + access.dir.getAveragePriceOf(COAL)) / access.config.FIX_PRICE;
        //}

    }

    @Override
    protected void receive(Commodity c) {
        super.receive(c);
        if (c instanceof Labor) {
            labor.push((Labor) c);
            if (orderedLabor > 0) orderedLabor--;
        }
        if (c instanceof Coal) {
            coal.push((Coal) c);
            sc = false;
            if (orderedCoal > 0) orderedCoal--;
        }
        if (c instanceof Iron) {
            sir = false;
            ir.push((Iron) c);
            if (orderedIron > 0) orderedIron--;
        }

    }

    @Override
    protected void generateCom() {
        while (labor.size() >= access.config.REQ_LAB_FOR_TOOL && coal.size() > 0 && ir.size() > 0) {
            Labor[] templab = new Labor[access.config.REQ_LAB_FOR_TOOL];
            for (int i = 0; i < access.config.REQ_LAB_FOR_TOOL; i++)
                templab[i] = labor.pop();
            com.push(Tool.Generate(access.comStats, templab, coal.pop(), ir.pop()));
            isStocked = true;
        }

    }
}
