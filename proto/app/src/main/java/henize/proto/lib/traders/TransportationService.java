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
import java.util.ArrayList;
import java.util.List;

import henize.proto.lib.bank.Account;
import henize.proto.lib.commodities.Commodity;
import henize.proto.lib.commodities.Labor;
import henize.proto.lib.commodities.ReceiveDeliveryAddressListener;
import henize.proto.lib.commodities.Transportation;
import henize.proto.lib.map.ArrivalNotficationListener;
import henize.proto.lib.map.ArrivalPoint;
import henize.proto.lib.map.Traveler;
import henize.proto.lib.sim.Access;
import henize.proto.pathfinder.Coordinate;

import static henize.proto.lib.map.ArrivalPoint.END;
import static henize.proto.lib.map.ArrivalPoint.START;
import static henize.proto.lib.traders.ComType.LABOR;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class TransportationService extends Trader implements ReceiveDeliveryAddressListener, ArrivalNotficationListener, Serializable{
    Labor labor;
    List<TSPair> wait_list = new ArrayList<TSPair>();
    List<TSPair> active_list = new ArrayList<TSPair>();
    TSPair current_tranport;
    Transportation tr;



    public TransportationService(Access access)
    {
        super(access);
        traderType = "shipping";
        account.setTaxCode(Account.TaxCode.TRANSPORT);
        InitCom();
        this.address = access.sim.map.GenerateAddress(6, access.config.trAdd);
    }
    @Override
    public boolean act()
    {
       super.act();

        if (access.sim.wait_for_t)
        {
            if (wait_list.size() <= access.config.TRANSPORT_OVERLOAD_RESET)
                access.sim.wait_for_t = false;
        }
        if (wait_list.size() > 0 || access.sim.wait_for_t)
        {
            if (wait_list.size() > access.config.TRANSPORT_OVERLOAD)
            {
                access.sim.wait_for_t = true;
            }

           // for (TSPair t : wait_list.toArray(new TSPair[0]))
           // {
                TSPair t = wait_list.get(0);
                active_list.add(t);

                if (!buy(LABOR))
                {
                    active_list.remove(t);
               //     break;
                }
                else
                {
                    wait_list.remove(t);
                }
           // }
        }
        return false;
    }


    @Override
    protected void increasePrice(){
      return;
   }

    @Override
    protected void sellSuccess()
    {
      return;
   }

    @Override
    protected void fixPriceToLabor() {
        if(fixPrice) {
            setPrice(access.dir.getAveragePriceOf(LABOR) / 2);
        }
    }
    @Override
    protected void setTargetPriceOffset() {
        useStandardOffsetModel();
    }
    @Override
    public long getPrice() {
        if(!access.config.collectTax) return super.getPrice();
        return super.getPrice() + (long)(super.getPrice() * (access.config.universalTaxRate / 100) + super.getPrice() * (access.config.transportTaxRate / 100) );
    }
    @Override
    protected boolean initTransport()
    {
        TSPair ts = new TSPair(activeTrade, activeTrade.activeTrade, activeTrade.quantity);
        ts.reserve_price = access.dir.getLowestPriceOf(LABOR);
        wait_list.add(ts);
        InitCom();
        return true;
    }

    private void InitCom()
    {
        com.push(new Transportation(access.comStats, new Coordinate(0, 0), new Coordinate(0, 0), null));
        isStocked = true;
        access.dir.setMaxPriority(this, false);
    }
    @Override
    protected long calculateTotals(QTP q)
    {
        q.quant = 1; q.tp = 0;
        return getPrice();
    }
    @Override
    protected void receive(Commodity c)
    {
        if (c instanceof Labor)
        {
            current_tranport = active_list.remove(0);
            activeTrade = current_tranport.active;
            labor = (Labor)c;
            labor.creator.pauseTraveler(true);
            tr = (Transportation)com_sold.pop();
            tr.use();
            tr = Transportation.Generate(access.comStats, labor, address, activeTrade.address);
            tr.addListener(this);
            tr.receiveDeliveryAddress(current_tranport.active_active.address);
            com_sold.push(tr);
            arrivalNotification(null, END); // send the Transportation comm so that additional information can passed to this

        }
    }
    @Override
    public void receiveDeliveryAddressNotification(Coordinate[] order) {

        access.sim.map.Transport(tr);
        tr.traveler.tr_color = 9;
        tr.traveler.addListener(this);
        tr.traveler.order_for = current_tranport.active_active;
        tr.traveler.quant = current_tranport.quant;


    }
    @Override
    public void arrivalNotification(Traveler sender, ArrivalPoint p){
        if (p == START)
        {
            sender.transportation.labor.creator.pauseTraveler(false);
            sender.transportation.use();
            return;
        }
        if (sender == null) //true if an operation is underway
        {
            super.arrivalNotification(sender, p); // so that buyer receives tranport comm so that more infatiom is passed
        }
    }

}
