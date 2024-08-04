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
import java.util.Random;
import java.util.Stack;

import henize.proto.lib.bank.Account;
import henize.proto.lib.commodities.Commodity;
import henize.proto.lib.commodities.Transportation;
import henize.proto.lib.directory.DirectoryList;
import henize.proto.lib.directory.PriceDecSignalListener;
import henize.proto.lib.map.ArrivalNotficationListener;
import henize.proto.lib.map.ArrivalPoint;
import henize.proto.lib.map.Traveler;
import henize.proto.lib.sim.Access;
import henize.proto.pathfinder.Coordinate;

import static henize.proto.lib.map.ArrivalPoint.END;
import static henize.proto.lib.traders.ComType.LABOR;
import static henize.proto.lib.traders.ComType.TRANSPORTATION;

/**
 * Created by ACR411 on 12/13/2017.
 */


public class Trader implements PriceDecSignalListener, ArrivalNotficationListener, Serializable {
    public int directory_index;
    public int priority_index;
    protected long price;
    protected boolean isStocked;
    public boolean isWaiting;
    protected Access access;
    public Coordinate address;
    public String id;
    public boolean isDead;
    public Account account;
    public boolean sale;
    protected long noSaleInc;
    protected int quantity;
    public long sale_count;
    public long targetOffset;
    public long priceOffset;
    protected boolean fixPrice;
    protected boolean active;
    public Trader activeTrade;
    public boolean max_priority;
    public boolean reserve;
    public long reserve_price;
    public String traderType;
    protected boolean unableToBuyTransportaion;
    public Trader sellerOfBid;
    public long bidPriceOffset;
    public boolean allowPriceDecrease;
    private List<PriceChangeListener> pcl_list = new ArrayList<PriceChangeListener>();

    public Stack<Commodity> com = new Stack<Commodity>();
    public Stack<Commodity> com_sold = new Stack<Commodity>();

    public List<SpecialOrder> specialOrders = new ArrayList();
    public List<SpecialOrder> processingSpecialOrders = new ArrayList<>();
    public boolean execSpecial;

    private ComType _specBuyType;
    private int _specOrderQuant;



    protected Trader(Access access) {
        this.access = access;
        this.account = new Account(access, 0/*access.config.INIT_CASH*/);
        this.price = access.config.MIN_PRICE;
        //this.stock = access.config.MIN_STOCK;
        fixPrice = true;
        this.id = "UNNAMED";
        this.traderType = "NULL";
        access.dir.addListener(this);
    }

    public void addListener(PriceChangeListener listener){
        pcl_list.add(listener);
    }
    public void removeListener(PriceChangeListener listener){
        pcl_list.remove(listener);
    }

    private void priceChanged(){
        for(PriceChangeListener l : pcl_list) l.priceChangedNotification(this);
    }

    public long getNonBidPrice() {
        long shippingOffset = 0;
        if(unableToBuyTransportaion) {
            shippingOffset = access.dir.getCheapestTraderOf(TRANSPORTATION).getPrice();
        }
        return reserve ? reserve_price : price + priceOffset + shippingOffset;
    }
    public long getPrice () { return getNonBidPrice(); }
    protected void setPrice(long price) {
        if(price >= access.config.MIN_PRICE)
            this.price = price;
        else
            this.price = access.config.MIN_PRICE;

        priceChanged();
    }

    public void increaseBid() {
        bidPriceOffset+=10;
        priceChanged();
    }


    public boolean isStocked() {
        return isStocked;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setBuyCond(String buyCond) {}
    public void setSellCond(String sellCond) {}
    public void setBuyValues(int quant, long price) {}
    public void setSellValues(int quant, long price) {}
    public void buyEnabled(boolean enabled) {}
    public void sellEnabled(boolean enabled) {}
    public boolean act() {

        generateCom();

        execSpecialPrioritySystem();

        if(access.sim.specialPriority.size() > 0) return true;

        if (isWaiting || isDead) return true;

        if (!sale)
        {
            if(com.size() > 0 && access.config.supplyAndDemandEnabled)
                allowPriceDecrease = true;
        }
        fixPriceToLabor();
        setTargetPriceOffset();
        adjustPriceOffset();


        if(execSpecial) return true;

        if (com.size() <= access.config.MIN_STOCK || priceOffset == 0) {
            active = stock();
        }

        sale = false;
        return active;
    }

    public void execSpecialPrioritySystem() {
        checkCancel();
        if (_specBuyType != null) {
            buySpecialPriority(_specBuyType, _specOrderQuant);
            _specBuyType = null;
        }
        while (specialOrders.size() > 0) {
            execSpecial = true;
            SpecialOrder o = specialOrders.get(0);

                stockSpecialPriority(o.quant);

            processingSpecialOrders.add(specialOrders.remove(0));
        }
        if(execSpecial == true) {
            List<SpecialOrder> remove = new ArrayList<>();
            int readyCount = 0;
            for (SpecialOrder o : processingSpecialOrders) {
                if (com.size() >= o.quant) {
                    readyCount++;
                    execSpecial = false;
                    if(sellTo(o.trader, o.trader.account, o.quant) ) {
                        o.quant -= quantity;
                        if(o.quant == 0) {
                            remove.add(o);
                            access.sim.specialPriority.pop();
                        }
                    }
                    execSpecial = true;
                }
            }
            for (SpecialOrder o : remove) {
                processingSpecialOrders.remove(o);
            }
            if (processingSpecialOrders.size() == 0) {
                execSpecial = false;
            }
        }
    }

    protected void die(){
        access.dir.removeListener(this);
    }

    protected void adjustPriceOffset() {
            if (targetOffset < priceOffset) {
                priceOffset--;
            }
            if (targetOffset > priceOffset) {
                priceOffset++;
            }
            priceChanged();

    }
    protected void fixPriceToLabor() {
        if(fixPrice) {
            if(access.config.supplyAndDemandEnabled) {
                setPrice(access.dir.getAveragePriceOf(LABOR) / access.config.FIX_PRICE_SUPPLYDEMAND);
            }else {
                setPrice(access.dir.getAveragePriceOf(LABOR) / access.config.FIX_PRICE);
            }
        }

    }
    protected void decreasePrice(boolean sendSignal)
    {
        if(access.config.supplyAndDemandEnabled) {
            if(isWaiting()) return;
            if(access.sim.isWaiting()) return;
            if (allowPriceDecrease){
                priceOffset -= access.config.PRICE_INC;
                if(priceOffset < access.config.MIN_PRICE) priceOffset = access.config.MIN_PRICE;
            }

        }else {
           /* if (price < access.config.MIN_PRICE && fixPrice ? price >= access.dir.getAveragePriceOf(LABOR) / 2 : true)
                setPrice(fixPrice ? access.dir.getAveragePriceOf(LABOR) / 2 : access.config.MIN_PRICE);
            else
                setPrice(price - access.config.PRICE_INC);*/

        }
        if(sendSignal)
            access.dir.sendPriceDecSignal(this);

        allowPriceDecrease = false;
        priceChanged();
    }
     protected void setTargetPriceOffset() {

        long level = access.bankStats.adj_totalCurrency / access.dir.getCount();
        long target = Math.abs(level - account.getBalance());

        long div = com.size() == 0 ? 1 : com.size();
        if (access.config.supplyAndDemandEnabled) {
            if (account.getBalance() < level) {
                targetOffset = (target / div);
            } else {
                targetOffset -= ((target / div) / div);
                if(targetOffset < 0) targetOffset = 0;
            }
        } else {
            useStandardOffsetModel();

    }
    }

    protected void useStandardOffsetModel() {
        long level = access.bankStats.adj_totalCurrency / access.dir.getCount();
        //try removing this
/*
        if (!sale) {
            return;
        }///////////////
*/


        if (account.getBalance() < level) {
            targetOffset = Math.abs(level - account.getBalance()) / access.config.OFFSET_DIV;
        } else if (account.getBalance() > level && targetOffset > 0) {
            targetOffset = 0;
        }
    }

    protected void increasePrice() {
        priceOffset += access.config.PRICE_INC;
    }

    protected boolean stock(){ return false; }
    protected boolean stockSpecialPriority(int quant) { return false; }
    protected long bidMax() {return 0;}
 /*   protected boolean bid(ComType com, long max, int quant) {
        if(!access.auction.canBuy(com, max)) {
            access.auction.bid(this, com, true);
        }
        Trader seller = access.auction.bid(this, com, false);
        if(seller == null) {
            return false; //withdrawn
        }else {
            if(access.auction.isWon(this)) {
                sellerOfBid = seller;
                boolean success = buy(com, quant);
                access.auction.reset(com);
                return success;
            }
        }
        return false;
    }*/
 protected boolean buyAtReserve(ComType type, long res_price) {
     _sellAtReservePrice(res_price);
     boolean success = buy(type);
     _sellAtCurrentPrice();
     return success;

 }


    protected boolean buy(ComType type) {
        return buy(type, 1);
    }
    protected boolean buy(ComType type, int quant) {
        boolean success = false;
        //Trader t = access.dir.getCheapestTraderOf(type);
        for (Trader t : access.dir.getListFromType(type).getList()){
            if(!(t == null || !t.sellTo(this, account, quant))) {
                success = true;
                quant -= t.quantity;
                if(quant == 0) break;
                continue;
            }
        }
        return success;
    }
    public void _outside_thread_order(ComType type, int quant) {
     _specBuyType = type;
     _specOrderQuant = quant;
    }
    public boolean buySpecialPriority(ComType type, int quant){
     DirectoryList l = access.dir.getListFromType(type);
     List<Trader> list = new ArrayList<Trader>();
     for(int i = 0; i < l.getCount(); i++) {
         if(!l.getAtIndex(i).id.equals("MASTER")) {
             list.add(l.getAtIndex(i));
         }
     }
     int n = list.size();
     int j = quant / n;
     if(type == LABOR || j == 0) {
         j = 1;
     }
     int k = 0;
     int listIndex = 0;
     while (k < quant) {
         if(quant - k < j) {
             j = quant - k;
         }
         if(listIndex < list.size()) {
             list.get(listIndex).sellSpecialPriority(this, j);
             k += j;
             listIndex++;
         } else {
             listIndex = 0;
         }
     }
/*
     if(n >= quant) {
         for(int i = 0; i < quant; i++) { //fix list issue
             list.get(i).sellSpecialPriority(this, 1);
         }
     } else {
         while(k < quant) {
             if(quant - k < j) {
                 list.get(0).sellSpecialPriority(this, quant - k);
                 break;
             }
             for(int i = 0; i < n; i++) {
                 list.get(i).sellSpecialPriority(this, j);
                 k += j;
             }
         }
     }
*/
     return true;
    }
    protected boolean sellSpecialPriority(Trader t, int quant) {
        specialOrders.add(new SpecialOrder(t, quant));
        access.sim.specialPriority.push(true);
        return true;
    }

    public boolean sellTo(Trader trader, Account acct)
    {
        return sellTo(trader, acct, 1);
    }
    //ref tp needed for labor trader override
    protected long calculateTotals(QTP q)
    {
        if (q.quant == Integer.MAX_VALUE)
            q.quant = com.size();

        q.tp = access.dir.getAveragePriceOf(TRANSPORTATION);
        return getPrice() * q.quant + q.tp;
    }
    protected void _sellAtReservePrice(long _reserve) {
        reserve_price = _reserve;
        reserve = true;
    }
    protected void _sellAtCurrentPrice() {
     reserve_price = 0;
     reserve = false;
    }
    protected void reset() {
        execSpecial = false;
    }

    public int getQuantityOfLastTransaction() {
     return quantity;
    }
    public boolean sellTo(Trader trader, Account acct, int quant)
    {

        QTP q = new QTP(quant, 0);
        long total = calculateTotals(q);
        if (execSpecial || isWaiting || !isStocked || com.size() < q.quant) return false;
        if (total > acct.getBalance()) {
            long loanReq = total - acct.getBalance();
            acct.getLoan(loanReq);
            if (acct.getBalance() < getPrice() + q.tp) {
                q.quant = 0;
            }
            if (q.quant > 1) {
                q.quant = (int)((acct.getBalance() - q.tp) / getPrice());
            }
            if (q.quant <= 0) {
                decreasePrice(true);
                return false;
            }
            else {
                if (q.quant > access.config.TRANSPORT_MAX) {
                 //   q.quant = access.config.TRANSPORT_MAX;
                }
                total = getPrice() * q.quant + q.tp;
            }
        }

        quantity = q.quant;
        activeTrade = trader;

        if(!authorizeSale()) return false;

        if(!acct.send(this.account, total)) {
            return false;
        }

        if (initTransport()) {
            for (int i = 0; i < quantity; i++)
                com_sold.push(com.pop());

            sale_count += quantity;
            sale = true;

            sellSuccess();

            if (com.size() == 0) {
                ranOutOfStock();
            }
            unableToBuyTransportaion = false;
            return true;
        }
        else {
            if(account.getBalance() < access.dir.getCheapestTraderOf(TRANSPORTATION).getPrice() ) {
                unableToBuyTransportaion = true;
            }
            account.refund(acct, total);
        }
        return false;
    }

    protected void checkCancel() {
        if(access.sim.specialPriority.isEmpty()) {
               reset(); //should only exec if orders canceled mid fill.
        }
    }

    protected void ranOutOfStock() {
        isStocked = false;
        allowPriceDecrease = false;
        access.dir.setMaxPriority(this, true);
    }

    protected boolean authorizeSale() {
        return true;
    }

    protected void sellSuccess()
    {
        if(!id.equals("MASTER") && access.config.supplyAndDemandEnabled)
            increasePrice();

        noSaleInc = 0;
    }
    protected boolean initTransport(){
       return buy(TRANSPORTATION);
    }

    protected void receive(Commodity c) {
        if (c instanceof Transportation) {
            Transportation tr = (Transportation)c;
           // tr.receiveDeliveryAddress(activeTrade.address);
            tr.traveler.addListener(this);
            //tr.traveler.setQuant(quantity);
        }
    }

    protected void generateCom() { }

    @Override
    public void priceDecSignalNotification(Trader t) {
        if (isDead || id.equals("MASTER")) return;

        if (t.getClass() == this.getClass()) {
            if (t == this) return;
            decreasePrice(false);
        }
    }

    @Override
    public void arrivalNotification(Traveler sender, ArrivalPoint p) {
        if (p == END) {
            if (sender == null) {
                activeTrade.receive(com_sold.pop());
                return;
            }
            else {

                for (short i = 0; i < sender.quant; i++) {
                    sender.order_for.receive(com_sold.pop());
                }
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}
