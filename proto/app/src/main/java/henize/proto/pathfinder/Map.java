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
package henize.proto.pathfinder;

import java.io.Serializable;

/**
 * Created by ACR411 on 12/14/2017.
 */

public class Map implements Serializable {
    boolean[][] map;
    public Node[][] n_map;

    public Map(int x, int y) {
        map = new boolean[x][y];
        n_map = new Node[x][y];
    }

    public Map(int x, int y, Coordinate[] unwalkableNodes)
    {
        this(x, y);
        if (unwalkableNodes == null)
            return;
        for (Coordinate c : unwalkableNodes) {
            map[c.x][c.y] = true;
        }
    }

    public boolean getAtIndexes(int x, int y) {

        return (!(x >= 0 && x < map[0].length && y >= 0 && y < map.length) || map[x][y]);

    }
    public void setAtIndexes(int x, int y, boolean value){
        map[x][y] = value;
}
}
