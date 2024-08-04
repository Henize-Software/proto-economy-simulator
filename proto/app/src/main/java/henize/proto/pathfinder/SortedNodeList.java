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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ACR411 on 12/14/2017.
 */

public class SortedNodeList implements Serializable {
    List<Node> list = new ArrayList<Node>();
    Comparator<Node> nodeComparator = new Comparator<Node>()
    {
        public int compare(Node a, Node b)
        {
            return a.getTotalCost() - b.getTotalCost();
        }
    };

    public int Count() {
        return list.size() ;
    }

    public Node NodeAt(int i) {
        return list.get(i);
    }

    public void RemoveAt(int i) {
        list.remove(i);
         }

    public int IndexOf(Node n) {
        /*for(int i = 0; i < list.size(); i++){
            Node nn = list.get(i);
            if(nn.getTotalCost() == n.getTotalCost()){return i;}
        }*/
        return list.indexOf(n);
    }

    public int Add(Node n) {
        int k = Collections.binarySearch(list, n, nodeComparator);

        if (k == -1) // no element
        {
            list.add(0, n);
        }
        else if (k < 0) // find location by complement
        {
            k = ~k;
            list.add(k, n);
        }
        else if (k >= 0) {
            list.add(k, n);
        }


        return k;
    }

    public Node RemoveFirst() {
        Node n = list.remove(0);
        return n;

    }

}

