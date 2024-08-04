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
package henize.proto.lib.commodities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import henize.proto.lib.map.Traveler;
import henize.proto.lib.stats.ComStats;
import henize.proto.pathfinder.Coordinate;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Transportation extends Commodity implements Serializable {
    public Coordinate[] transportOrder;
    public Traveler traveler;
    public Labor labor;

    private List<ReceiveDeliveryAddressListener> rdl_list = new ArrayList<ReceiveDeliveryAddressListener>();

    public Transportation(ComStats stat, Coordinate start, Coordinate mid, Labor lt)
    {super(stat);
        transportOrder = new Coordinate[] { start, mid, null};
        labor = lt;
        stat.trCount++;
        stat.t_trCount++;

    }

    @Override
    public void use() throws RuntimeException {
        super.use();
        comStat.trCount--;
    }

    public static Transportation Generate(ComStats stat, Labor l, Coordinate start, Coordinate mid) throws RuntimeException
    {
        if (l.isUsed()) {
            throw new RuntimeException("Com used");
        }
        else {
            l.use();
            return new Transportation(stat, start, mid, l);
        }
    }
    public void receiveDeliveryAddress(Coordinate c) {
        transportOrder[2] = c;
        recievedDeliveryAddressNotification();
    }

    public void addListener(ReceiveDeliveryAddressListener listener){
        rdl_list.add(listener);
    }
    public void removeListener(ReceiveDeliveryAddressListener listener){
        rdl_list.remove(listener);
    }

    private void recievedDeliveryAddressNotification(){
        for(ReceiveDeliveryAddressListener l : rdl_list) l.receiveDeliveryAddressNotification(transportOrder);
    }
}
