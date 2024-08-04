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

/**
 * Created by ACR411 on 12/28/2017.
 */


    public class Heap
    {
        public Node a[];

        public int NNodes;

        public Heap( int size )
        {
            a = new Node[size+1];
            NNodes = 0;

        }



        /* ============================================================
           put(x): insert x into the heap

               (we must make sure the heap properties are preserved !
           ============================================================ */
        void put( Node x )
        {
            a[NNodes+1] = x;        // Insert x in the "fartest left location"
            // This preserves the "complete" bin tree prop

            NNodes++;		      // We have 1 more node
            x.index = NNodes;
            HeapFilterUp( NNodes ); // Filter the inserted node up
            // This preserves the "min. value at root" prop

        }

        /* ===============================================================
           HeapFilterUp(k): Filter the node a[k] to its proper position
                            in the heap
           =============================================================== */
        void HeapFilterUp( int k )
        {
            int parent;                 /* parent = parent */
            Node help;

            while ( k != 1 )    /* k has a parent node */
            { /* Parent is not the root */

                parent = k/2;

                if ( a[k].getTotalCost() < a[parent].getTotalCost() )
                {
                    help = a[parent];
                    a[parent] = a[k];
                    a[parent].index = parent;
                    a[k] = help;
                    a[k].index = k;

            /* ===============================
	       Continue filter up one level
	       =============================== */
                    k = parent;          // k moved up one level
                }
                else
                {
                    break;
                }

            }
        }




        public Node remove(int k) {
            int parent;
            Node r;             // Variable to hold deleted value

            r = a[k];             // Save return value

            a[k] = a[NNodes];     // Replace deleted node with the right most leaf
            // This fixes the "complete bin. tree" property
            NNodes--;

            parent = k / 2;

            if (k == 1 /* k is root */ || a[parent].getTotalCost() < a[k].getTotalCost()) {
                 //System.out.println("\nHeap before filter DOWN:");
               // printHeap();
                HeapFilterDown(k);  // Move the node a[k] DOWN the tree
            } else {
               // System.out.println("\nHeap before filter UP:");
              //  printHeap();
                HeapFilterUp(k);    // Move the node a[k] UP the tree
            }

            // a[r.index] = null;

//            r.index = -1;
            return r;
        }

        void HeapFilterDown( int k )
        {
            int child1, child2;
            Node help;


            while ( 2*k <= NNodes )
            {
                child1 = 2*k;                 // Child1 = left  child of k
                child2 = 2*k+1;               // Child2 = right child of k

                if ( child2 <= NNodes )
                {
            /* ========================================
	       Node k has 2 children nodes....
	       Find the min. of 3 nodes !!!
	       ======================================== */
                    if ( a[k].getTotalCost() < a[child1].getTotalCost() && a[k].getTotalCost() < a[child2].getTotalCost() )
                    {
               /* -------------------------------------------------------
		  Node k is in correct location... It's a heap. Stop...
                  ------------------------------------------------------- */
                        break;
                    }
                    else
                    {
	        /* =========================================
		   Replace a[k] with the smaller child node
		   ========================================= */
                        if ( a[child1].getTotalCost() < a[child2].getTotalCost() )
                        {
                  /* -------------------------------------------------
		     Child1 is smaller: swap a[k] with a[child1]
                     ------------------------------------------------- */
                            help = a[k];
                            a[k] = a[child1];
                            a[k].index = k;
                            a[child1] = help;
                            a[child1].index = child1;

                            k = child1;         // Replacement node is now a[child1]
                        }
                        else
                        {
                  /* -------------------------------------------------
		     Child2 is smaller: swap a[k] with a[child2]
                     ------------------------------------------------- */
                            help = a[k];
                            a[k] = a[child2];
                            a[k].index = k;
                            a[child2] = help;
                            a[child2].index = child2;
                            k = child2;        // Replacement node is now a[child2]
                        }
                    }
                }
                else
                {
            /* ========================================
	       Node k only has a left child node
	       ======================================== */
                    if ( a[k].getTotalCost() < a[child1].getTotalCost() )
                    {
               /* -------------------------------------------------------
		  Node k is in correct location... It's a heap. Stop...
                  ------------------------------------------------------- */
                        break;
                    }
                    else
                    {
               /* -------------------------------------------------------
		  Child1 is smaller: swap a[k] with a[child1]
                  ------------------------------------------------------- */
                        help = a[k];
                        a[k] = a[child1];
                        a[k].index = k;
                        a[child1] = help;
                        a[child1].index = child1;
                        k = child1;         // Replacement node is now a[child1]
                    }
                }
            }
        }




   /* ======================================================= */

        public void printnode(int n, int h)
        {
            for (int i = 0; i < h; i++)
                System.out.print("        ");

            System.out.println("[" + a[n].getTotalCost() + "]");
        }

        void printHeap()
        {
            if ( NNodes == 0 )
            {
                System.out.println("*** heap is empty");
                System.out.println("================================");
                return;
            }

            showR( 1, 0 );
            System.out.println("================================");
        }

        public void showR(int n, int h)
        {
            if (n > NNodes)
                return;

            showR(2*n+1, h+1);
            printnode(n, h);
            showR(2*n, h+1);
        }


    }

