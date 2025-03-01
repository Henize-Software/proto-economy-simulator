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
/*
package henize.proto.lib.sim;

*/
/**
 * Created by ACR411 on 9/14/2018.
 *//*



import henize.proto.pathfinder.Coordinate;


        import java.io.Serializable;


*/
/**
 * Created by ACR411 on 12/13/2017.
 *//*



public class Config_BigMap extends Config implements Serializable, Cloneable {

    public boolean collectTax;
    public double universalTaxRate, laborTaxRate, foodTaxRate, toolTaxRate, coalTaxRate, ironTaxRate, transportTaxRate;
    public boolean warp, speed;
    public boolean computing = true;
    public boolean fresh = true;
    public static String stateFile = "henize.proto.state31";

    public boolean supplyAndDemandEnabled = true;

    public boolean USE_TEST_ADS = false;

    private double INIT_CASH_DOLLAR = 500.00; //default

    public int MAP_SIZE = 40;
    public int DEF_FRAME_X = 20;
    public int DEF_FRAME_Y = 20;
    public boolean FULL_SCREEN = false;

    public int TEXT_SIZE_DIV = 25;
    public int TEXT_SIZE_DIV_LIMITED_HEIGHT = 11;
    public int TEXT_SIZE_DIV_LIMITED_HEIGHT_FULL_SCREEN = 32;

    //all npc
    public long INIT_CASH = (long)INIT_CASH_DOLLAR * 100;
    public int STOCK_INC = 4;
    public long PRICE_INC = 10;
    public long MIN_PRICE = 1;
    public int MIN_STOCK = 10;
    public int TRANSPORT_MAX = 10;
    public int OFFSET_DIV = 10;
    public int FIX_PRICE_SUPPLYDEMAND = 5;
    public int FIX_PRICE = 5;

    //labor npc
    public int INIT_LABOR_POP = 80;
    public int INIT_LABOR = 100;
    public int LAB_PRICE_HOLD = 150; //150
    public int LAB_FOOD_USE = 2000;
    public int LAB_SPAWN_RATE = 100;
    public boolean LAB_CAN_DIE = true;
    public boolean LAB_CAN_SPAWN = true;
    public int MIN_LABOR = 1;
    public int MAX_LABOR = 168;
    public boolean FLAG_NO_FOOD = false;
    public int Lxreg = 0;
    public int Lyreg = 0;
    public int L_xreg = 39;
    public int L_yreg = 39;
    public boolean sequencial = false;

    //food npc
    public byte FOOD_POP = 4;
    public byte TOOL_FOOD = 20;
    public byte LABOR_FOOD = 1;
    public byte MIN_FOOD_SELL = 5;
    public boolean FLAG_NO_LABOR = false;
    public int Fxreg = 0;
    public int Fyreg = 0;
    public int F_xreg = 39;
    public int F_yreg = 39;

    //tool npc
    public int TOOL_POP = 3;
    public int REQ_LAB_FOR_TOOL = 1;

    //coal npc
    public int COAL_POP = 2;
    public long COAL_PRICE_INC = 10;
    public byte TOOL_COAL = 8;
    public byte LABOR_COAL = 1;

    //iron npc
    public int IRON_POP = 2;
    public long IRON_PRICE_INC = 10;
    public byte TOOL_IRON = 8;
    public byte LABOR_IRON = 1;

    //transporter
    public int TRANSPORT_OVERLOAD = 168;
    public int TRANSPORT_OVERLOAD_RESET = 167;
    public Coordinate trAdd = new Coordinate(31, 13);

    //master
    public Coordinate msAdd = new Coordinate(31, 24);

    //travelers
    public int SET_SCALE_LEAP = 8;
    public int SCALE_LEAP;
    public int SCALE_LEAP_DIAG;

    //graph
    public int GRAPH_REFRESH = 1000;

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return null;
        }
    }
}
*/
