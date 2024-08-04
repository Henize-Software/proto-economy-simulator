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
import henize.proto.lib.traders.LaborTrader;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class Labor extends Commodity implements Serializable {
    public LaborTrader creator;

    public Labor(ComStats stat, LaborTrader creator) {
        super(stat);
        this.creator = creator;
        stat.laborCount++;
        stat.t_laborCount++;
    }
    public static Labor[] Generate(ComStats stat, Food f, LaborTrader creator) throws RuntimeException {
        if (f.isUsed()) {
            throw new RuntimeException("Com used");
        }
        else {
            f.use();
            Labor[] l = new Labor[1];
            for (int i = 0; i < 1; i++)
                l[i] = new Labor(stat, creator);
            return l;
        }
    }
    public void use() {
        super.use();
        comStat.laborCount--;
    }
}
