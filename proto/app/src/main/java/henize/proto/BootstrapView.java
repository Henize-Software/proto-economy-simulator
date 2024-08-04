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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by ACR411 on 9/26/2018.
 */

public class BootstrapView extends RelativeLayout {

    Thread t, s;

    EditText log;
    ScrollView scv;
    Button terminate;

    BootstrapView _this;

    public BootstrapView(Context context) {
        super(context);
        inflate(context, R.layout.bootstrap_panel, this);
        _this = this;
        log = findViewById(R.id.bootstrapLog);
        scv = findViewById(R.id.bootLogScrollView);
        terminate = findViewById(R.id.buttonTerminate);
        terminate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.vibrate();
                App.showBigRedDialogue();
                App.deleteStateAndTerminate();
            }
        });
    }
    public void initApp() {
                App.activity.setContentView(_this);

                log.setText(log.getText().append("Proto bootstrap\ninitializing user interface...\n").toString());

        App.activity.initUI();
    }

    public void log(String text) {
        log.setText(log.getText().append(text += "\n").toString());
    }

    public void initSim() {
        initSim(null);
    }
    public void initSim(final String state) {
        App.activity.setContentView(_this);
        if(App.loading) return;
        if(s != null && s.isAlive()) {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String s = "";
        if(state != null) s = " " + state;
        App.loading = true;
                log.setText(log.getText().append("loading sim state" + s + "...").toString());

        scv.fullScroll(View.FOCUS_DOWN);

        t = new Thread(new Runnable() {
            @Override
            public void run() {

                    App.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(state == null) {
                                App.activity.deserialize();
                            } else {
                                App.activity.deserialize(state);
                            }
                            App.loading = false;
                            App.activity.pref.setView(R.id.map_item);
                            App.activity._resume();
                            log.setText(log.getText().append("DONE\n").toString());

                        }
                    });
                }

        });
        t.start();

    }

    public void saveSim() {
        saveSim(null);
    }
    public void saveSim(final String state) {
        App.activity.setContentView(_this);
        if(App.loading || App.saving) return;
        App.saving = true;
        s = new Thread(new Runnable() {
            @Override
            public void run() {
                if(state == null) {
                    App.activity.serialize();
                } else {
                    App.activity.serialize(state);
                }
                App.activity.stateLoaderView.refresh();
                App.saving = false;
            }
        });
        s.start();
        String s = "";
        if(state != null) s = " " + state;
        log.setText(log.getText().append(s + " saved...\n").toString());
    }
}
