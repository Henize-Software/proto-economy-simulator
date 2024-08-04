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
package henize.proto.lib.directory;

import java.io.Serializable;
import java.util.Arrays;


import henize.proto.lib.traders.Trader;

/**
 * Created by ACR411 on 9/13/2018.
 */

public class PriorityList implements Serializable  {

    /**
     * Created by ACR411 on 12/13/2017.
     */

        private int count;

        private Trader[] priority;

        public PriorityList(){
            count = 0;

            priority = new Trader[1000];
        }

        public int getCount() {
            return count;
        }

        public Trader[] getPriorityList() { return Arrays.copyOf(priority, count); }

        public void add(Trader t){
            ++count;
            t.priority_index = count - 1;

            priority[count - 1] = t;

            sortPriority(t);

        }

        public void remove(Trader t){

            int p = t.priority_index;

            for(int j = p; j < count - 1; j++){
                swapPriority(j, j+1);
            }
            priority[--count] = null;
        }




        private void sortPriority(Trader t) {
            int index = t.priority_index;
            byte direction;
            for(;;) {
                if(index != 0 && t.com.size() < priority[index - 1].com.size())
                    direction = -1;
                else if (index != count - 1 && t.com.size() > priority[index + 1].com.size())
                    direction = 1;
                else
                    return;
                swapPriority(index, index + direction);
                index += direction;
            }
        }

        private void swapPriority(int a, int b) {
            int i = priority[a].priority_index;
            priority[a].priority_index = priority[b].priority_index;
            priority[b].priority_index = i;
            Trader t = priority[a];
            priority[a] = priority[b];
            priority[b] = t;
        }







    }


