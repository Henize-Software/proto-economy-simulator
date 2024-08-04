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
 * Created by ACR411 on 12/28/2017.
 */

public class NodeHeap implements Serializable {

    Heap heap = new Heap(2500);
    int count;

    public int Count() {
        return count;
    }

/*    public Node NodeAt(int i) {
       return heap.a[i];

    }

    public void RemoveAt(int i) {

        heap.remove(i + 1);

    }

    public int IndexOf(Node n) {
        for(int i = 1; i <= heap.NNodes; i++){
            if(heap.a[i] != null && heap.a[i].x == n.x && heap.a[i].y == n.y) {
                return i;
            }
        }
        return -1;
    }*/

    public int Add(Node n) {

        heap.put(n);
        n.onList = true;
        count++;
        return n.index;


    }

    public void remove(Node n) {
        n.onList = false;
        count--;
    }

    public Node RemoveFirst() {
        Node n = heap.remove(1);
        n.onList = false;
        count--;
        return n;
    }




}
