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
import java.util.List;

import henize.proto.lib.traders.PriceChangeListener;
import henize.proto.lib.traders.Trader;

/**
 * Created by ACR411 on 12/13/2017.
 */

public class DirectoryList implements PriceChangeListener, Serializable {
    private int count;
    private Trader[] list;


    public DirectoryList(){
        count = 0;
        list = new Trader[1000];

    }

    public int getCount() {
        return count;
    }

    public Trader[] getList() {
        return Arrays.copyOf(list, count);
    }


    public void add(Trader t){
        ++count;
        t.directory_index = count - 1;
        list[count - 1] = t;

        sort(t);

        t.addListener(this);
    }

    public void remove(Trader t){
        int i = t.directory_index;
        list[i].removeListener(this);
        for(int k = i; k < count - 1; k++){
            swap(k, k+1);
        }

        list[--count] = null;

    }

    public Trader getAtIndex(int i){
        return list[i];
    }


    private void sort(Trader t){
        try {
            int index = t.directory_index;
            byte direction;
            for (; ; ) {
                if (index != 0 && t.getPrice() < list[index - 1].getPrice())
                    direction = -1;
                else if (index < count - 1 && t.getPrice() > list[index + 1].getPrice())
                    direction = 1;
                else
                    return;
                swap(index, index + direction);
                index += direction;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swap(int a, int b){
        int i = list[a].directory_index;
        list[a].directory_index = list[b].directory_index;
        list[b].directory_index = i;
        Trader t = list[a];
        list[a] = list[b];
        list[b] = t;
    }



    @Override
    public void priceChangedNotification(Trader t) {
        sort(t);
    }


}
