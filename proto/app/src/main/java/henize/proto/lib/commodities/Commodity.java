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

public class Commodity implements Serializable {
    private boolean isUsed;
    protected ComStats comStat;

    protected Commodity(ComStats cs){
        comStat = cs;
    }

    public boolean isUsed(){
        return isUsed;
    }

    public void use() throws RuntimeException{
        if(isUsed) throw new RuntimeException("Com used");
        isUsed = true;
    }

    protected static <T> T[] ToolGen(T[] array, ComStats stat, Labor l, Tool t, byte ratio, byte def) throws RuntimeException {
        if (l.isUsed()) {
            throw new RuntimeException("Com used");
        }
        else
        {
            byte size = def;
            if (t != null && !t.isUsed()) {
                t.use();
                size = ratio;
            }
            array = (T[])new Commodity[size];
            for (byte i = 0; i < size; i++) {
                array[i] = (T)new Commodity(stat);
            }
            l.use();
            return array;
        }
    }
}
