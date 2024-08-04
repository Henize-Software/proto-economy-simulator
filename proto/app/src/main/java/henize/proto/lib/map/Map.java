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
package henize.proto.lib.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import henize.proto.lib.commodities.Transportation;
import henize.proto.lib.sim.Access;
import henize.proto.pathfinder.Coordinate;

import static henize.proto.lib.map.ArrivalPoint.START;

/**
 * Created by ACR411 on 12/14/2017.
 */

public class Map implements ArrivalNotficationListener, Serializable {
    byte[][] map;
    public boolean[][] update;
    Access access;
    //public int[][] offset;
    public int scale, diag;
    public static int frameWidth, frameHeight, setX, setY;
    public int frameStartX, frameStartY;


    public int width, height;

    //Timer step_timer;
    Random rnd;

    public List<MapDataPair> stationary_list; //for visual purposes only
    public List<Traveler> traveler_list;
    public List<MapDataPair> unwalkable;
    List<Traveler> wait_list;
    List<Traveler> garbage;

    private Map()
    {
        stationary_list = new ArrayList<MapDataPair>();
        traveler_list = new ArrayList<Traveler>();
        garbage = new ArrayList<Traveler>();
        wait_list = new ArrayList<Traveler>();
        unwalkable = new ArrayList<MapDataPair>();

        //step_timer = new Timer();
        //step_timer.Interval = 1;
        rnd = new Random();
        //step_timer.Tick += new EventHandler(delegate (object sender, EventArgs e) { Step(); });

    }

    public Map(Access access, byte[][] map, int scale, String unwalk) {
        this();
        this.map = map;
        this.scale = scale;
        this.access = access;
        width = map.length;
        height = map[0].length;
        frameWidth = access.config.DEF_FRAME_X;
        frameHeight = access.config.DEF_FRAME_Y;
        update = new boolean[width][height];
        loadMap(unwalk);

    }
    private void setMaxFrameSize() {
        if(height < frameHeight) {
            frameHeight = height;
        }
    }
    public void setFullScreen(boolean b) {
        if(b) {
            frameWidth = setX;
            frameHeight = setY;
            setMaxFrameSize();
        } else {
            frameWidth = access.config.DEF_FRAME_X;
            frameHeight = access.config.DEF_FRAME_Y;
        }
        moveFrame(0, 0);
    }
    public void setFrameSize(int x, int y) {
        setX = x;
        setY = y;
        setFullScreen(access.config.FULL_SCREEN);
        frameStartX = 0;
        frameStartY = 0;
        updateAll();
    }
    public boolean moveFrame(int incx, int incy) {
        frameStartX += incx;
        frameStartY += incy;
        boolean reset = false;
        if(frameStartX < 0) {frameStartX = 0; reset = true;}
        if(frameStartY < 0) {frameStartY = 0; reset = true;}
        if(frameStartX + frameWidth > width) { frameStartX = width - frameWidth; reset = true;}
        if(frameStartY + frameHeight > height) {frameStartY = height - frameHeight; reset = true;}
        updateAll();
        return reset;
    }
    public boolean insideFrame(int x, int y) {
        boolean xOk = x >= frameStartX - 1  && x < frameStartX + frameWidth + 1 ;
        boolean yOk = y >= frameStartY - 1 && y < frameStartY + frameHeight + 1 ;
        if(xOk && yOk)
            return true;
        else
            return false;
    }
    public int translateX(int x) {
        return x - frameStartX;
    }
    public int translateY(int y) {
        return y - frameStartY;
    }

    public Coordinate getTranslatedAddress(int x, int y) {
        return new Coordinate(x + frameStartX, y + frameStartY);
    }
    public void updateAll() {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                update[x][y] = true;
            }
        }
    }
    public void updateAreaAround(int _x, int _y) {
        int __x, __y;
        for(int y = -1; y <= 1; y++) {
            for(int x = -1; x <= 1; x++) {
                __x = _x + x;
                __y = _y + y;
                if(isOutOfBounds(__x, __y)) {
                    continue;
                }
                update[__x][__y] = true;
            }
        }
    }

    public boolean isOutOfBounds(int __x, int __y) {
        return __x < 0 || __y < 0 || __x > width - 1 || __y > height - 1;
    }

    public void initNonSerializable() {
        /*for(Traveler t : traveler_list) {
            t.initPF = true;
            t.waitStateEnabled = false;
        }*/
    }
    private void loadMap(String unwalk) {
        if(unwalk == null) return;
        int i = 0;

               for(int y = 0; y < height; y++){
                   for(int x = 0; x < width; x++) {
                       if(unwalk.charAt(i) == ' ') ++i;
                       byte _p = Byte.parseByte("" + unwalk.charAt(i++));
                       if(_p > 0) {
                           _p += 10;
                           MapDataPair p = new MapDataPair(new Coordinate(x, y), _p);
                           unwalkable.add(p);
                           setAtCoordinate(p.address, p.data);
                       }
                   }
               }

    }

    public byte getAtAddress(int x, int y) {

        return map[x][y];
    }

    public void setAtAddress(int x, int y, byte value) { map[x][y] = value; }


    public byte getAtCoordinate(Coordinate a) {
        return map[a.x][a.y];
    }
    public void setAtCoordinate(Coordinate a, int value) {
        map[a.x][a.y] = (byte)value;
        update[a.x][a.y] = true;
    }

    public Coordinate GenerateAddress(int color) {
        return GenerateAddress(color, 0, 0, width, height, false);
    }
    public Coordinate GenerateAddress(int color, Coordinate c) {
        setAtCoordinate(c, color);
        stationary_list.add(new MapDataPair(c, color));
        return c;
    }
    public Coordinate GenerateAddress(int color, int xreg, int yreg, int _xreg, int _yreg) {
        return GenerateAddress(color, xreg, yreg, _xreg, _yreg, false);
    }
    public Coordinate GenerateAddress(int color, int xreg, int yreg, int _xreg, int _yreg, boolean seq)
    {
        int x = 0, y = 0;
        Coordinate c;
        do
        {
            c = new Coordinate(rnd.nextInt(width), rnd.nextInt(height));

            if(x++ == width - 1) {
                x = 0; y++;
            }
            if(seq && color == 1) {
                c.x = x; c.y = y;
            }

        if(     (c.x >= xreg && c.x <= _xreg) &&
                (c.y >= yreg && c.y <= _yreg) &&
                getAtCoordinate(c) == 0 &&
                onlyAdjacent(c, color == 1)) break;
        } while (true);
        GenerateAddress(color, c);
        return c;
    }
    private boolean onlyAdjacent(Coordinate c, boolean allowDiag) {
        for(byte y = -1; y <= 1; y++) {
            for(byte x = -1; x <=1; x++) {
               if(!allowDiag || ((x == 0 && y != 0) || (x != 0 && y == 0))) {
                    for(MapDataPair m : stationary_list) {
                        if(m.address.x == c.x + x && m.address.y == c.y + y) return false;
                    }

                }
                for(MapDataPair m : unwalkable) {
                    if(m.address.x == c.x + x && m.address.y == c.y + y) return false;
                }
            }
        }
        return true;
    }
    public void RemoveAddress(Coordinate c, int key)
    {
        MapDataPair r = null;
        for(MapDataPair j : stationary_list){
            if(j.address == c && j.data == key) {
                r = j;
                break;
            }
        }
        stationary_list.remove(r);

    }

    public void Tick() {
        Tick(true);
    }
    public void Tick(boolean travel) {
        for (int i = 0; i < map.length; i++) {
            Arrays.fill(map[i], (byte) 0);
        }
        if (travel) {
            for (Traveler t : garbage) {
                traveler_list.remove(t);
            }
            garbage.clear();

            for (Traveler t : wait_list) {
                InitTransport(t);
            }
            wait_list.clear();
            for (Traveler t : traveler_list) {
                t.Tick();
            }
        } else {
            updateAll();
        }

        for (MapDataPair d : stationary_list) {
            setAtCoordinate(d.address, d.data);
        }
        for (MapDataPair d : unwalkable) {
            setAtCoordinate(d.address, d.data);
        }
    }

    Traveler InitTransport(Traveler t)
    {
        t.addListener(this);
        traveler_list.add(t);
        return t;
    }


    public Traveler Transport(Coordinate start, Coordinate end)
    {
        Traveler t = new Traveler(access, this, start, end);
        wait_list.add(t);
        return t;
    }

    public Traveler Transport(Coordinate start, Coordinate mid, Coordinate end)
    {
        Traveler t = new Traveler(access, this, start, mid, end);
        wait_list.add(t);
        return t;
    }

    public Traveler Transport(Transportation transportation)
    {
        Traveler t;
        if (transportation.transportOrder.length == 2)
        {
            t = Transport(transportation.transportOrder[0], transportation.transportOrder[1]);
        }
        else
        {
            t = Transport(transportation.transportOrder[0], transportation.transportOrder[1], transportation.transportOrder[2]);
        }
        t.transportation = transportation;
        transportation.traveler = t;
        return t;
    }

    @Override
    public void arrivalNotification(Traveler sender, ArrivalPoint p) {
        if (p == START)
        {
            Traveler t = (Traveler)sender;
            t.removeListener(this);
            garbage.add(t);
        }
    }

    public void interrupt() {
        for(Traveler t : traveler_list) {
            t.interrupt();
        }
    }
}
