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
import java.util.List;



import henize.proto.lib.commodities.Transportation;
import henize.proto.lib.sim.Access;
import henize.proto.lib.traders.Trader;
import henize.proto.pathfinder.Coordinate;
import henize.proto.pathfinder.PathFinder;

import static henize.proto.lib.map.ArrivalPoint.START;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Traveler implements Runnable, Serializable {
    public String id;
    public byte tr_color = 8;
    volatile boolean initPF;
    Map map;
    Access access;
    public Coordinate loc;

    volatile transient PathFinder path_finder;
    Coordinate[] path;
    int curr_step;
    byte curr_torder;

    public short currXOffset, currYOffset, rotation;
    private short _rotation;

    volatile boolean waitStateEnabled;

    Coordinate[] transport_order;
    public Transportation transportation;
    public Trader order_for;
    public int quant;

    transient Thread thread;



    private List<ArrivalNotficationListener> tl_list = new ArrayList<ArrivalNotficationListener>();


    Traveler(Access access, Map map, Coordinate currLoc)
    {
        this.map = map;
        this.access = access;
        curr_step = 0;
        curr_torder = 0;
        loc = currLoc;
    }

    public Traveler(Access access, Map map, Coordinate start, Coordinate finish)
    {
        this(access, map, start);
        transport_order = new Coordinate[] { start, finish, start };
    }
    public Traveler(Access access, Map map, Coordinate start, Coordinate mid, Coordinate finish)
    {
        this(access, map, start);
        transport_order = new Coordinate[] { start, mid, finish, start };
    }

    //DONT USE THIS
    public void setWaitStateEnabled(boolean value) {waitStateEnabled = value;}

    public void Tick()
    {
        if(access.config.warp){
            int p = 0;
            for(Coordinate c : transport_order) {

                if(++p == 3) p = 0;
                                    sendNotficationSignal(this, ArrivalPoint.fromInt(p));

            }
            return;
        }
        if(!waitStateEnabled) Next(Route());
    }

    Coordinate[] GetStationaryCoordinates(List<MapDataPair> stationary_list) {
        List<Coordinate> stationary_coords = new ArrayList<Coordinate>();

        for (MapDataPair k : stationary_list)
            stationary_coords.add(k.address);
        for (MapDataPair k : map.unwalkable)
            stationary_coords.add(k.address);
        return stationary_coords.toArray(new Coordinate[0]);
    }

    Coordinate[] DestinationAdjustedStationaries(Coordinate plus)
    {

        Coordinate[] unwalkable = GetStationaryCoordinates(map.stationary_list);
        List<Coordinate> newUnwalkable = new ArrayList<Coordinate>();

        boolean add;
        for (int i = 0; i < unwalkable.length; i++)
        {
            add = true;
            for(Coordinate c : transport_order){
                if(c.x == unwalkable[i].x && c.y == unwalkable[i].y){
                    add = false;
                }
        }
          if(add)  newUnwalkable.add(unwalkable[i]);
        }
        if(plus != null)
            newUnwalkable.add(plus);
        return newUnwalkable.toArray(new Coordinate[0]);

    }

    //switch to next route in tranport order list if true
    boolean Next(boolean next)
    {
        if (next && ++curr_torder < transport_order.length - 1)
        {
            path_finder = new PathFinder(map.width, map.height,
                    transport_order[curr_torder],
                    transport_order[curr_torder + 1], DestinationAdjustedStationaries(null));
            curr_step = 0;
            initPF = true;
            return false;
        }
        return true;
    }

    //step through current route in tranport order and update map; returns true when complete
    boolean Route()
    {
        if (InitPathFinder())
        {
            return false;
        }
        else
        {
            return Step();
        }
    }

    private boolean InitPathFinder()
    {
        if (path_finder == null && curr_torder < transport_order.length - 1)
        {
            path_finder = new PathFinder(map.width, map.height,
                    transport_order[curr_torder],
                    transport_order[curr_torder + 1], DestinationAdjustedStationaries(null));
            initPF = true;
        }

        if (initPF) {
            waitStateEnabled = true;

            thread = new Thread(this);
            thread.start();

            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean Step()
    {
        if(path == null) {
            initPF = true;
            return false;
        }
        if (curr_step < path.length - 1) {

            if (stepOffset()) {
                resetOffset();

                map.updateAreaAround(path[curr_step].x, path[curr_step].y);
                loc = path[++curr_step];
                map.updateAreaAround(path[curr_step].x, path[curr_step].y);
            }
            return false;
        }
        else
        {
            NotifyArrival();
            return true;
        }
    }

    private boolean setRotation() {
        int xp = path[curr_step].x;
        int yp = path[curr_step].y;
        int x = path[curr_step + 1].x;
        int y = path[curr_step + 1].y;

        if(x == xp && y < yp) {
            _rotation = 0; }
        if(x > xp && y < yp) {
            _rotation = 45;  }
        if(x > xp && y == yp) {
            _rotation = 90; }
        if(x > xp && y > yp) {
            _rotation = 135; }
        if(x == xp && y > yp) {
            _rotation = 180; }
        if(x < xp && y > yp) {
            _rotation =  225; }
        if(x < xp && y == yp) {
            _rotation = 270; }
        if(x < xp && y < yp) {
            _rotation = 315; }


        if(map.access.config.speed) {
            rotation = _rotation;
        }else if(_rotation != rotation) {
            int inc = 0; int _inc = 10;
            if(Math.abs(rotation - _rotation) < _inc) {
                _inc = 1;
            }
            if(_rotation < rotation) {
                if((359 - rotation) + _rotation < rotation - _rotation) {
                    inc = _inc;
                } else {
                    inc = -_inc;
                }
            }
            else if(_rotation > rotation) {
                if((359 - _rotation) + rotation < _rotation - rotation) {
                    inc = -_inc;
                } else {
                    inc = _inc;
                }
            }

            rotation += inc;

            if(rotation > 359) {
                rotation = (short)(rotation - 359);
            } else if(rotation < 0) {
                rotation = (short)(359 + rotation);
            }

            //return false;
        }
        return true;
    }

    private void resetOffset() {

        currXOffset = 0; currYOffset = 0;
        /*if(currXOffset < 0) currXOffset = (short)(currXOffset + map.scale);
        if(currXOffset > 0) currXOffset = (short)(currXOffset - map.scale);
        if(currYOffset < 0) currYOffset = (short)(currYOffset + map.scale);
        if(currYOffset > 0) currYOffset = (short)(currYOffset - map.scale); */
    }

    private boolean stepOffset() {
        if(!setRotation()) return false;
        int o = getIncrements();

        if(o == 0){
            return true;
        }
        short y = (short)o;
        short x = (short)(o >> 16);
        int scaleLeap = access.config.SCALE_LEAP;
        if(x != 0 && y != 0) {
            scaleLeap = access.config.SCALE_LEAP_DIAG;
        }
        scaleLeap = scaleLeap == 0 ? access.config.SET_SCALE_LEAP : scaleLeap;
        currXOffset += x * (map.scale / scaleLeap);
        currYOffset += y *(map.scale / scaleLeap);

        if(Math.abs(currXOffset) >= map.scale || Math.abs(currYOffset) >= map.scale) {
            return true;
        }
        else {
            int _x = path[curr_step].x;
            int _y = path[curr_step].y;
            map.updateAreaAround(_x, _y);
            return false;
        }
    }



    private int getIncrements() {
        short x= 0, y = 0;
        if(curr_step < path.length - 1) {
            if (path[curr_step].x < path[curr_step + 1].x) {
                x = 1;
            }
            if (path[curr_step].x == path[curr_step + 1].x) {
                x = 0;
            }
            if (path[curr_step].x > path[curr_step + 1].x) {
                x = -1;
            }
            if (path[curr_step].y < path[curr_step + 1].y) {
                y = 1;
            }
            if (path[curr_step].y == path[curr_step + 1].y) {
                y = 0;
            }
            if (path[curr_step].y > path[curr_step + 1].y) {
                y = -1;
            }

        }
        return (x << 16) +  (y & 0xFFFF);
    }

    private void NotifyArrival()
    {
            ArrivalPoint point;
            if (curr_torder == transport_order.length - 1)
            {
                point = START;
            }
            else
            {
                byte offset = 1;
                if (transport_order.length == 3)
                {
                    offset = 2;
                }
                point = ArrivalPoint.fromInt(curr_torder + offset);
            }
            sendNotficationSignal(this, point);
        }


        //USE THIS INSTEAD OF OTHER
    void setWaitState(boolean s){
        synchronized (this){
            waitStateEnabled = s;
            access.config.computing = false;
        }


}
public void addListener(ArrivalNotficationListener listener){
    tl_list.add(listener);
}
    public void removeListener(ArrivalNotficationListener listener){
        tl_list.remove(listener);
    }
    private void sendNotficationSignal(Traveler sender, ArrivalPoint p){
        for(ArrivalNotficationListener l : tl_list) l.arrivalNotification(sender, p);
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(2);
        initPF = false; //serilaztion bug because path finder interupted
        path = path_finder.getPath();
        setWaitState(false);
    }

    public void setQuant(int quant) {
        if(this.quant > 0) throw new RuntimeException("VALKUE ALREADY SET");
        this.quant = quant;
    }

    public void interrupt() {
        try {
            if(thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
