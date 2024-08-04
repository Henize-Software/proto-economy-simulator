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

import android.support.v4.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import henize.proto.lib.commodities.Labor;
import henize.proto.lib.traders.LaborTrader;
import henize.proto.lib.traders.SpecialOrder;
import henize.proto.lib.traders.Trader;
import henize.proto.lib.traders.ComType;
import henize.proto.lib.traders.TransportationService;

/**
 * Created by ACR411 on 12/13/2017.
 */


public class Directory implements Serializable {
    private DirectoryList labor, food, tool, coal, iron, transportation;
    private List<PriceDecSignalListener> pdl_list = new ArrayList<PriceDecSignalListener>();
    private Trader[] fullList;
    public List<Trader> firstPriority = new ArrayList<Trader>();

    public Stack<SpecialOrder> laborOrderForTrade = new Stack<>();

    private boolean hasChanged;

    public Directory(){
        labor = new DirectoryList();
        food = new DirectoryList();
        tool = new DirectoryList();
        coal = new DirectoryList();
        iron = new DirectoryList();
        transportation = new DirectoryList();
        hasChanged = true;
    }


    public void addListener(PriceDecSignalListener listener){
        pdl_list.add(listener);
    }
    public void removeListener(PriceDecSignalListener listener){
        pdl_list.remove(listener);
    }
    public void sendPriceDecSignal(Trader t){
        for(PriceDecSignalListener l : pdl_list) l.priceDecSignalNotification(t);
    }
    public Trader getCheapestTraderOf(ComType t){
        return getCheapestTraderOf(t, true);
    }

    public void setMaxPriority(Trader t, boolean b) {
        if(b) {
            if(firstPriority.contains(t))
                return;
            else
                firstPriority.add(t);
            t.max_priority = true;
        } else {
            firstPriority.remove(t);
            t.max_priority = false;
        }
    }
    public void add(ComType type, Trader t) {
        hasChanged = true;
        DirectoryList list = getListFromType(type);
        list.add(t);
    }
    public void remove(ComType type, Trader t) {
        hasChanged = true;
        DirectoryList list = getListFromType(type);
        list.remove(t);
    }
    public int getCount(ComType t) {
        DirectoryList dl = getListFromType(t);
        return dl.getCount();
    }
    public int getCount(){
        int count = 0;
        count += labor.getCount();
        count += food.getCount();
        count += tool.getCount();
        count += coal.getCount();
        count += iron.getCount();
        count += transportation.getCount();
        return count;
    }
    public Pair<Trader[], Pair<Trader[], Trader[]>> getPriorityList() {
        PriorityList max_list = new PriorityList();
        PriorityList ind_list = new PriorityList();
        PriorityList lab_list = new PriorityList();
        for(Trader t : getAll()) {
            if(t instanceof TransportationService)
                continue;
            if(t instanceof LaborTrader) {
                lab_list.add(t);
            } else {
                if(!t.max_priority) {
                    ind_list.add(t);
                }
            }
        }
        for(Trader t : firstPriority) {
            max_list.add(t);
        }
        return new Pair<>(max_list.getPriorityList(), new Pair<>(ind_list.getPriorityList(), lab_list.getPriorityList()));
    }
    public Trader getAtaAddress(int x, int y) {
        Trader[] t = getAll();
        for(int i = 0; i < t.length; i++) {
            if(t[i].address.x == x && t[i].address.y == y) return t[i];
        }
        return null;
    }
    public Trader[] getAll(){
        if(!hasChanged) return fullList;
        int count = getCount();
        fullList = new Trader[count];

        int j = 0, k = 0;
        DirectoryList dl = getListFromType(ComType.fromInt(k));
        for(int i = 0; i < count; i++){
            if(j < dl.getCount()){
                fullList[i] = dl.getAtIndex(j);
                j++;
            }
            else {
                if(k <= 5) {
                    k++;
                    dl = getListFromType(ComType.fromInt(k));
                    j = 0;
                    i--;
                }
            }
        }

        return fullList;
    }
    public Trader getTraderByID(String ID) {
        Trader[] traders = getAll();
        for(int i = 0; i < traders.length; i++) {
            //if(traders[i].id.equals(ID)) return traders[i];
            if(traders[i].id ==(ID)) return traders[i];
        }
        return null;
    }
    public Trader getCheapestTraderOf(ComType t, boolean inclMaster){
        DirectoryList list = getListFromType(t);
        for(Trader q : list.getList()){
            if(q.isStocked() && q.isWaiting() == false){
                if(inclMaster) return q;
                if(!q.id.equals("MASTER")) return q;
            }
        }
        return null;
    }
    public long getLowestPriceOf(ComType t){
        DirectoryList list = getListFromType(t);
        return list.getAtIndex(0).getPrice();
    }
    public long getHighestPriceOf(ComType t) {
        DirectoryList list = getListFromType(t);
        return list.getAtIndex(list.getCount() - 1).getPrice();
    }
    public long getAveragePriceOf(ComType t) {
        DirectoryList list = getListFromType(t);
        long sum = 0;
        int count = list.getCount();
        int sub = 0;
        for(int i = 0; i < count; i++){
            if(!list.getAtIndex(i).id.equals("MASTER"))
                sum += list.getAtIndex(i).getPrice();
            else
                sub++;
        }
        return sum / (count - sub);
    }
    public DirectoryList getListFromType(ComType t){
        switch (t){
            case LABOR:
                return labor;
            case FOOD:
                return food;
            case TOOL:
                return tool;
            case COAL:
                return coal;
            case IRON:
                return iron;
            case TRANSPORTATION:
                return transportation;
        }
        return null;
    }
}
