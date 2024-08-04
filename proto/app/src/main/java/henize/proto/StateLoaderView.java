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
import android.content.res.AssetManager;
import android.view.View;
import android.widget.*;
import android.widget.ListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ACR411 on 2/19/2020.
 */

public class StateLoaderView extends RelativeLayout {
    ListView stateList;
    Button save, load;

    String selected;
    public StateLoaderView(final Context context) {
        super(context);
        inflate(context, R.layout.state_loader_panel, this);

        stateList = findViewById(R.id.stateList);
        save = findViewById(R.id.saveStateButton);
        load = findViewById(R.id.loadStateButton);

        refresh();

        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = (String)stateList.getItemAtPosition(i);
                App.vibrate();
            }
        });

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                selected = dateFormat.format(date);
                App.vibrate();
                App.activity.bootView.saveSim(selected);
                }
        });

        load.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected != null) {
                    App.vibrate();
                    App.activity.bootView.initSim(selected);
                }
            }
        });

    }

    public void refresh() {
        String[] list = App.activity.getFilesDir().list();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
        stateList.setAdapter(adapter);
    }
}
