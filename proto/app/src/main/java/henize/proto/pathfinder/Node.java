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
import java.util.Stack;

/**
 * Created by ACR411 on 12/14/2017.
 */

public class Node implements Serializable {
    public int gCost;
    public int hCost;
    public int x;
    public int y;
    public int index;
    public boolean onList;

    Node parentNode;
    Node goalNode;
    Map ownerMap;



    public int getTotalCost() {
        return hCost + gCost;
    }

    public Node(Node parentNode, Node goalNode, Map ownerMap, int x, int y) {
        this.parentNode = parentNode;
        this.goalNode = goalNode;
        this.x = x;
        this.y = y;
        this.index = -1;
        this.ownerMap = ownerMap;
        CalculateCost();
    }
    public Node(Node parentNode, Node goalNode, Map ownerMap, int x, int y, boolean onList) {
        this(parentNode, goalNode, ownerMap, x, y);
        this.onList = onList;
    }


    public boolean IsDiagonalTo(Node other)
    {
      return ((other.x < this.x && other.y < this.y) || //upper left
              (other.x > this.x && other.y < this.y) || //upper right
              (other.x < this.x && other.y > this.y) || //lower left
              (other.x > this.x && other.y > this.y));  //lower right
    }


    void CalculateCost() {
        this.gCost = 10;

        if (parentNode != null)
        {
            if (IsDiagonalTo(parentNode)) //not really needed but may become usefull
              this.gCost = 14;
        }
        else
        {
            this.gCost = 0;
        }

        this.gCost = (parentNode != null) ? parentNode.gCost + this.gCost : this.gCost;
        this.hCost = (goalNode != null) ?
                Math.abs(goalNode.x - this.x) + Math.abs(goalNode.y - this.y) : 0;

    }

    public Node[] GetSuccessors() {
        Stack<Node> successors = new Stack<Node>();

        for (byte iy = -1; iy <= 1; iy++) {
            for (byte ix = -1; ix <= 1; ix++) {
                if (ownerMap.getAtIndexes(x + ix, y + iy) != true) {
                    Node n;
                    if(ownerMap.n_map[x + ix][y + iy] == null) {

                        n = new Node(this, this.goalNode, ownerMap, x + ix, y + iy);
                    }
                    else {
                        n = new Node(this, this.goalNode, ownerMap, x + ix, y + iy, ownerMap.n_map[x + ix][y + iy].onList);
                    }

                    ownerMap.n_map[x + ix][y + iy] = n;
                    if (!n.equals(this.parentNode) && !n.equals(this)) {
                        successors.push(n);
                }
            }
            }
        }
        return successors.toArray(new Node[0]);
    }

    public Node getParentNode() {
        return parentNode;
    }
    public void setParentNode(Node value) {
        parentNode = value;
    }

    public int compareTo(Node other) {
        return this.getTotalCost() - other.getTotalCost();
    }

    public boolean equals(Node other) {
        return (other != null && this.x == other.x && this.y == other.y);
    }

      //region "Operator Overloads"

    public static boolean isLessThan(Node a, Node b) {
        return a.compareTo(b) < 0;
    }

    public  boolean isLessThanOrEqual(Node b) {
        return this.compareTo(b) >= 0;
    }

    public static boolean isGreaterThan(Node a, Node b) {
        return a.compareTo(b) > 0;
    }

    public static boolean isGreaterThanOrEqual(Node a, Node b) {
        return a.compareTo(b) >= 0;
    }
    @Override
    public boolean equals(Object obj)
    {
        Node n = (Node)((obj instanceof Node) ? obj : null);
        return n != null && this.equals(n);
    }
      //endregion
}
