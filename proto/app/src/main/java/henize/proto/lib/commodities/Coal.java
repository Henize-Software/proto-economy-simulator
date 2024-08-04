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

import henize.proto.lib.stats.ComStats;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Coal extends Commodity implements Serializable {
    public Coal(ComStats cs) {
        super(cs);
    }

    public static Coal[] Generate(ComStats stat, Labor l, Tool t) {

        Coal[] ir = null;
        if (l.isUsed()) {
            throw new RuntimeException("Com used");
        }
        else
        {
            byte size = stat.access.config.LABOR_COAL;
            if (t != null && !t.isUsed()) {
                t.use();
                size = stat.access.config.TOOL_COAL;
            }
            ir = new Coal[size];
            for (byte i = 0; i < size; i++) {
                ir[i] = new Coal(stat);
            }
            l.use();

        }
        stat.coalCount += ir.length;
        stat.t_coalCount += ir.length;
        return ir;
    }

    public void use() {
        super.use();
        comStat.coalCount--;

    }
}


