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

/**
 * Created by ACR411 on 12/17/2017.
 */

public class TSPair implements Serializable {
    public Trader active;
    public Trader active_active;
    public int quant;
    public long reserve_price;

    public TSPair(Trader a, Trader aa, int q){
        active = a;
        active_active = aa;
        quant = q;
    }
}
