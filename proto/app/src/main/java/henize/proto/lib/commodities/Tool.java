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

public class Tool extends Commodity implements Serializable {
    protected Tool(ComStats cs) {
        super(cs);
        cs.toolCount++;
        cs.t_toolCount++;
    }

    @Override
    public void use() throws RuntimeException {
        super.use();
        comStat.toolCount--;
    }

    public static Tool Generate(ComStats stat, Labor[] labor, Coal c, Iron i) throws RuntimeException {
        for (Labor l : labor) {
            if (l.isUsed())
                throw new RuntimeException("Com used");
        }
        if (c.isUsed() || i.isUsed()) {
            throw new RuntimeException("Com used");
        }
        else {
            c.use(); i.use(); for (Labor l : labor ) l.use();
        }
        return new Tool(stat);
    }
}
