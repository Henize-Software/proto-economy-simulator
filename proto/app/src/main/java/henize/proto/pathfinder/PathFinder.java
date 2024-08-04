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

public class PathFinder implements Serializable {
    Map nodeMap;

    Node startNode;
    Node endNode;

   Stack<Node> finalPath = new Stack<Node>();
   Coordinate[] path;

    boolean isPathCalculated;

    public boolean getAtIndexes(int x, int y) {
        return nodeMap.getAtIndexes(x, y);
    }
    public void setAtIndexes(int x, int y, boolean value) {
            nodeMap.setAtIndexes(x, y, value);
            isPathCalculated = false;
        }

    public Coordinate getStartNodePosition() {
        return new Coordinate(startNode.x, startNode.y);
    }
    public void setStartNodePosition(int x, int y) {

            startNode.x = x;
            startNode.y = y;
            isPathCalculated = false;
        }

    public Coordinate getEndNodePosition() {
        return new Coordinate(endNode.x, endNode.y); }

    public void setEndNodePosition(int x, int y) {
            endNode.x = x;
            endNode.y = y;
            isPathCalculated = false;
       }

    public Coordinate[] getPath()
    {

        if (!isPathCalculated)
                FindPath();
            if(path != null)
                return path;
            if (finalPath.size() == 0)
                return null;
            int size = finalPath.size();
            path = new Coordinate[size];
            for(int i = 0; i< size; i++) {
                Node n = finalPath.pop();
                path[i] = new Coordinate(n.x, n.y);
            }

            return path;
           // Stack<Coordinate> coordinateStack = new Stack<Coordinate>();
           // for (Node pathNode : finalPath) {
             //   coordinateStack.push(new Coordinate(pathNode.x, pathNode.y));
           // }
            //Coordinate[] array = new Coordinate[coordinateStack.size()];
            //return coordinateStack.toArray(array);
        }


    public PathFinder(int width, int height, Coordinate startNodePos,
                            Coordinate endNodePos, Coordinate[] unwalkableNodes)
    {
        nodeMap = new Map(width, height, unwalkableNodes);
        endNode = new Node(null, null, nodeMap, endNodePos.x, endNodePos.y);
        startNode = new Node(null, endNode, nodeMap, startNodePos.x, startNodePos.y);
    }

    void FindPath()
    {
       // SortedNodeList Open = new SortedNodeList();
       NodeHeap Open = new NodeHeap();

        Open.Add(startNode);

        while (Open.Count() > 0)
        {
            Node currentNode = Open.RemoveFirst();

            if (currentNode.equals(endNode))
            {
                endNode.setParentNode(currentNode.getParentNode());
                break;
            }

            Node[] successors = currentNode.GetSuccessors();

            for (Node successorNode : successors) {
                //int oFound = Open.IndexOf(successorNode); //index stored in node
                if(successorNode.onList) {
                    if (successorNode.isLessThanOrEqual(currentNode)) {
                        continue;
                    }
                    Open.remove(successorNode);
                    continue;
                }
                Open.Add(successorNode);
 /*               if(oFound > 0)
                {
                    if (successorNode.isLessThanOrEqual(currentNode))
                    {
                        continue;
                    }
                }

                if (oFound >= 0)
                {
                    Open.RemoveAt(oFound);
                    continue;
                }
                Open.Add(successorNode);*/
            }
        }

        Node p = endNode;

        //loop through and contruct the final path by following the parents
        //of each node starting from the end node
        while(true)
        {
            finalPath.push(p);
            p = p.getParentNode();

            if (p == null)
                break;
            //if (p.ParentNode != null && p.ParentNode.ParentNode != null && p.ParentNode.ParentNode.ParentNode != null)
            //{
            //  //cleans and shortens the path by removing unecessary moves
            //  if (p.ParentNode.ParentNode.IsAjacentTo(p))
            //    p.ParentNode = p.ParentNode.ParentNode;
            //  //removes single node zigzags on x diminsion
            //  if (p.IsDiagonalTo(p.ParentNode) && p.ParentNode.IsDiagonalTo(p.ParentNode.ParentNode) &&
            //     p.X == p.ParentNode.ParentNode.X)
            //  {
            //    p.ParentNode.X = p.X;
            //  }
            //  //removes single node zigzags on y diminsion
            //  if (p.IsDiagonalTo(p.ParentNode) && p.ParentNode.IsDiagonalTo(p.ParentNode.ParentNode) &&
            //      p.Y == p.ParentNode.ParentNode.Y)
            //  {
            //    p.ParentNode.Y = p.Y;
            //  }

            //}
        }

        //if this is true then the there is no path or the start node is ontop of the end node.
        if (finalPath.size() == 1)
            finalPath.clear();

        isPathCalculated = true;
    }
}

