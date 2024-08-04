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
package henize.proto.lib.sim;

import android.support.v4.util.Pair;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import henize.proto.lib.bank.Account;
import henize.proto.lib.bank.Bank;
import henize.proto.lib.bank.TransactionListener;
import henize.proto.lib.directory.Directory;
import henize.proto.lib.map.Map;
import henize.proto.lib.stats.AdvTextStats;
import henize.proto.lib.stats.BankStats;
import henize.proto.lib.stats.ComStats;
import henize.proto.lib.stats.Graph;
import henize.proto.lib.stats.GraphData;
import henize.proto.lib.stats.TextStats;
import henize.proto.lib.stats.TraderStats;
import henize.proto.lib.traders.CoalTrader;
import henize.proto.lib.traders.ComType;
import henize.proto.lib.traders.FoodTrader;
import henize.proto.lib.traders.IronTrader;
import henize.proto.lib.traders.LaborTrader;
import henize.proto.lib.traders.MasterTrader;
import henize.proto.lib.traders.ToolTrader;
import henize.proto.lib.traders.Trader;
import henize.proto.lib.traders.TransportationService;

import static henize.proto.lib.traders.ComType.COAL;
import static henize.proto.lib.traders.ComType.FOOD;
import static henize.proto.lib.traders.ComType.IRON;
import static henize.proto.lib.traders.ComType.LABOR;
import static henize.proto.lib.traders.ComType.TOOL;
import static henize.proto.lib.traders.ComType.TRANSPORTATION;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Simulator implements TransactionListener, Serializable {
    public Map map;
    public Access access;
    public boolean wait_for_t;
    public Stack<Boolean> specialPriority;
    public Graph graph;
    public Config cfg;
    public transient TextStats textStats;
    public transient AdvTextStats advTextStats;
    int scale; String loadMap;

    List<Trader> primary;
    List<Trader> inactive;
    private boolean update;
    private boolean breakPriority;

    public Simulator(int scale, String loadMap, Config cfg){
        primary = new ArrayList<Trader>();
        inactive = new ArrayList<Trader>();
        specialPriority = new Stack<>();
        initSimulator(scale, loadMap, cfg);
    }
    public void run(){
        while(true) { Next();}
    }

    public void reset() {
        primary.clear();
        inactive.clear();
        specialPriority.clear();
        graph.deleteStateFile();
        initSimulator(scale, loadMap, null);
    }
    public void initSimulator(int scale, String loadMap, Config config){
        this.scale = scale;
        this.loadMap = loadMap;
        access = new Access();
        if(config == null){
            if(cfg == null) {
                access.config = new Config();
            } else {
                access.config = (Config)cfg.clone();
            }
        } else {
            access.config = config;
        }
        cfg = (Config)access.config.clone();


        map = new Map(access, GenerateMap(), scale, loadMap);
        access.bankStats = new BankStats();
        access.bank = new Bank(access);
        access.bank.initBanksAccount(access.config.INIT_CASH);
        access.dir = new Directory();

        access.comStats = new ComStats();
        access.comStats.access = access;
        access.traderStats = new TraderStats();
        access.sim = this;
        access.taxAccount = new Account(access, 0);
        access.taxAccount.setTaxCode(Account.TaxCode.MASTER);
        //access.auction = new AuctionSystem(access);
        access.dir = new Directory();
        access.master = new MasterTrader(access);

        graph = new Graph();
        initNonSerializable();
        initDirectory();
    }

    public void initNonSerializable() {
        //access.config.fresh = true;
        textStats = new TextStats(this);
        advTextStats = new AdvTextStats(this);
        map.initNonSerializable();

    }

    public void initDirectory(){
            for(int i = 0; i < access.config.INIT_LABOR_POP; i++) {
                access.dir.add(LABOR, new LaborTrader(access, "LAB" + Integer.toString(i)));
            }

        access.dir.add(LABOR, new LaborTrader(access, "TRIGGER"));

        for(int i = 0; i < access.config.FOOD_POP; i++) {
            access.dir.add(FOOD, new FoodTrader(access, "FOOD" + Integer.toString(i)));
        }
        for(int i = 0; i < access.config.TOOL_POP; i++) {
            access.dir.add(TOOL, new ToolTrader(access, "TOOL" + Integer.toString(i)));
        }
        for(int i = 0; i < access.config.COAL_POP; i++) {
            access.dir.add(COAL, new CoalTrader(access, "COAL" + Integer.toString(i)));
        }
        for(int i = 0; i < access.config.IRON_POP; i++) {
            access.dir.add(IRON, new IronTrader(access, "IRON" + Integer.toString(i)));
        }

        access.dir.add(TRANSPORTATION,  new TransportationService(access));

    }
    public void setScale(int scale) {

        if(scale != 0) setScaleLeap(scale, false);
        map.scale = scale;
        this.scale = scale;
    }

    private void setScaleLeap(int scale, boolean diag) {
        int[] den = findDenominators(scale);
        int diff = Integer.MAX_VALUE;
        int set = access.config.SET_SCALE_LEAP;
        for(int diviser : den) {
            if(diviser == 0) break;
            if(Math.abs(set-diviser) < diff) {
                diff = Math.abs(set-diviser);
                if(diag) {
                    access.config.SCALE_LEAP_DIAG = diviser;

                } else {
                    access.config.SCALE_LEAP = diviser;
                }
            }
        }
        if(diag) return;

        setScaleLeap(calDis(0, 0, scale, scale), true);
    }

    private int[] findDenominators(int scale) {
        int[] den = new int[120];
        int j = 0;
        for (int i = 2; i <= scale / 2; i++) {
            if (scale % i == 0) {
                den[j++] = i;
            }
        }
        if(den[0] == 0) {
            return findDenominators(scale + 1);
        }
        return den;
    }

    static int calDis(int x1,int y1,int x2,int y2)
    {
        return (int)(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
    }

    private byte[][] GenerateMap()
    {
            byte[][] _md = new byte[access.config.MAP_SIZE][access.config.MAP_SIZE];

        for (byte y = 0; y < access.config.MAP_SIZE; y++)
        {
            for (byte x = 0; x < access.config.MAP_SIZE; x++)
            {
                _md[x][y] = 0;
            }
        }
        return _md;
    }
    public long bankSum(ComType t) {
        Trader[] ta = access.dir.getListFromType(t).getList();
        long sum = 0;
        for(Trader tt : ta){
            if(tt.id.equals("MASTER")) continue;
            sum += tt.account.getBalance();
        }
        return sum;
    }
    public long bankSum() {

        Trader[] ta = access.dir.getAll();
        long sum = 0;
        for (Trader t : ta) {

            if (t.id.equals("MASTER")) continue;
            sum += t.account.getBalance();
        }return sum; // + access.bank.account.getBalance();
    }

    public int stockSum(ComType t) {
        Trader[] ta = access.dir.getListFromType(t).getList();
        int sum = 0;
        for(Trader tt : ta){
            if(tt.id.equals("MASTER")) continue;
            sum += tt.com.size();
        }
        return sum;
    }
    public String CtD(long cents) {
        double d = (double)cents / 100;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df.format(d);
    }

    public long DtoC(String dollars) {
        double d = Double.parseDouble(dollars);

        return (long)(d * 100);
    }
    public boolean isWaiting() {
        for(Trader t : access.dir.getListFromType(LABOR).getList()){
            if(!t.isWaiting()) return false;
        }
        return true;
    }
    public void Next(){
                if(breakPriority) {
                    specialPriority.clear();
                    breakPriority = false;
                }
                access.bankStats.adj_totalCurrency = bankSum();
                access.bank.act();
                access.master.act();
                access.dir.getListFromType(TRANSPORTATION).getAtIndex(0).act();
                if(specialPriority.size() > 0) {
                    for(Trader t : access.dir.getAll()) {
                        t.act();
                    }
                }else {
                    if (wait_for_t) {
                        return;
                    } else {

                        Pair<Trader[], Pair<Trader[], Trader[]>> lists = access.dir.getPriorityList();

                        for (Trader t : lists.first) {
                            t.act();
                        }
                        for (Trader t : lists.second.first) {
                            t.act();
                        }
                        for (Trader t : lists.second.second) {
                            t.act();
                        }


                    }
                }

       /*         for (Trader t : access.dir.getAll()) {

                    primary.add(t);
                }

                if (wait_for_t) {
                    access.dir.getListFromType(TRANSPORTATION).getAtIndex(0).act();
                    return;
                }

                for (Trader n : inactive.toArray(new Trader[0])) {
                    access.master.act();
                    if (n.act()) {
                        inactive.remove(n);
                        primary.remove(n);
                    }
                }

                for (Trader n : primary.toArray(new Trader[0])) {

                    access.master.act();
                    if(inactive.contains(n)) continue;
                    if (!n.act()) {
                        inactive.add(n);
                    }
                    //master.Act();
                }
                primary.clear();*/

    }
    @Override
    public void transactionNotification(boolean refund) {
       if(!refund)
        {
            graph.addPoint(new GraphData(access.dir.getAveragePriceOf(LABOR), access.dir.getAveragePriceOf(FOOD), access.dir.getAveragePriceOf(TOOL)
                    , access.dir.getAveragePriceOf(COAL), access.dir.getAveragePriceOf(IRON), access.dir.getAveragePriceOf(TRANSPORTATION)));

        }
        else {
            graph.removePoint();
        }
    }

    public void interrupt() {
        map.interrupt();
    }

    public void breakPriority() {
        breakPriority = true;
    }
}

