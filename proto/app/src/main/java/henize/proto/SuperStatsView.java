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
package henize.proto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import henize.proto.lib.traders.Trader;


public class SuperStatsView extends RelativeLayout {
    ListView traderList;
    ArrayList<Trader> orderedList;
    Timer timer2;
    public SuperStatsView(Context context) {
        super(context);
        inflate(context, R.layout.super_stats_panel, this);
        orderedList = new ArrayList<>();
        traderList = findViewById(R.id.traderList);

        refresh();


        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                App.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        }
                    });
                };

        }, 0, 1000);

    }

    public void refresh() {
             int scroll = traderList.getFirstVisiblePosition();
            View v = traderList.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - traderList.getPaddingTop());
            Trader[] traders = App.sim.access.dir.getAll();
            ArrayList<String> numberID = new ArrayList<String>();
            for (int i = 0; i < traders.length; i++) {
                numberID.add(Integer.toString(i));
                if(orderedList.contains(traders[i])) {
                    continue;
                }else {
                    orderedList.add(traders[i]);
                }
            }
            List<Trader> goodList = Arrays.asList(traders);
            Stack<Trader> toRemove = new Stack<>();
            for(Trader t : orderedList) {
                if(!goodList.contains(t)) {
                    toRemove.push(t);
                }
            }
            while(!toRemove.isEmpty()) {
                orderedList.remove(toRemove.pop());
            }

            ListAdapter adapter = new ListAdapter(App.activity, orderedList, numberID);
            traderList.setAdapter(adapter);
            traderList.setSelectionFromTop(scroll, top);

    }
}