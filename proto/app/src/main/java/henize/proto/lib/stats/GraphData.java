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
package henize.proto.lib.stats;

import java.io.Serializable;

/**
 * Created by ACR411 on 12/17/2017.
 */

public class GraphData implements Serializable {
    public long labor, food, tool, coal, iron, transport;
    public GraphData(long l, long f, long t, long c, long i, long tr)
    {
        labor = l; food = f; tool = t; coal = c; iron = i; transport = tr;
    }
}
