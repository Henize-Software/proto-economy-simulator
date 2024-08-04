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
package henize.proto.lib.directory;

import java.util.ArrayList;
import java.util.List;

import henize.proto.lib.sim.Access;
import henize.proto.lib.traders.ComType;
import henize.proto.lib.traders.Trader;

/**
 * Created by ACR411 on 6/14/2018.
 */

public class AuctionSystem {
    Access access;
    boolean won;
    List<Trader> laborAuction = new ArrayList<>();
    List<Trader> foodAuction = new ArrayList<>();
    List<Trader> toolAuction = new ArrayList<>();
    List<Trader> coalAuction = new ArrayList<>();
    List<Trader> ironAuction = new ArrayList<>();

    List<Trader> claborAuction = new ArrayList<>();
    List<Trader> cfoodAuction = new ArrayList<>();
    List<Trader> ctoolAuction = new ArrayList<>();
    List<Trader> ccoalAuction = new ArrayList<>();
    List<Trader> cironAuction = new ArrayList<>();

    public AuctionSystem(Access access) {
        this.access = access;
    }

    public boolean canBuy(ComType com, long max) {
        Trader t = access.dir.getCheapestTraderOf(com);
        return t != null && t.getPrice() <= max;
    }
    public Trader bid(Trader buyer, ComType com, boolean withdraw) {
        List<Trader> bidding = getListFromType(com, false);
        List<Trader> withdrawn = getListFromType(com, true);
        Trader seller = access.dir.getCheapestTraderOf(com);
        if(seller == null) return null;

        if(withdrawn.contains(buyer))
        {
            if(bidding.size() == 0)
                withdrawn.clear();
            return null;
        }
        if(!bidding.contains(buyer)){
            bidding.add(buyer);

        } else if(bidding.size() == 1) {
            won = true;
            bidding.clear();
            withdrawn.clear();

            return seller;
        }
        if(withdraw) {
            bidding.remove(buyer);
            withdrawn.add(buyer);
            return null;
        }


        seller.increaseBid();
        return seller;
    }

    public void reset(ComType com) {
        for (Trader t : access.dir.getListFromType(com).getList()) {
            t.bidPriceOffset = 0;
        }
    }

    public boolean isWon(Trader buyer) {
        boolean iswon = won;
        won = false;
        return iswon;
    }
    public List<Trader> getListFromType(ComType t, boolean closed){
        switch (t){
            case LABOR:
                return closed ? claborAuction : laborAuction;
            case FOOD:
                return closed ? cfoodAuction : foodAuction;
            case TOOL:
                return closed ? ctoolAuction : toolAuction;
            case COAL:
                return closed ? ccoalAuction : coalAuction;
            case IRON:
                return closed ? cironAuction : ironAuction;
        }
        return null;
    }


}
