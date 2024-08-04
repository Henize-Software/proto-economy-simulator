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
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ACR411 on 9/10/2018.
 */

public class ControlView extends RelativeLayout {

    Button classic, big, big2, boot;
    TextView text;

    public ControlView(Context context) {
        super(context);
        inflate(context, R.layout.control_panel, this);
        classic = findViewById(R.id.buttonClassic);
        big = findViewById(R.id.buttonBig);
        big2 = findViewById(R.id.buttonBig2);
        boot = findViewById(R.id.buttonViewBoot);
        text = findViewById(R.id.textView22);

        classic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Loading...this could take a second.");
                    }
                });
                App.vibrate();
                App.initSimWithFile(R.raw.classic);
                App.activity.initUIFromState();
                text.setText("Classic map is loaded. Press play to start.");
            }
        });
        big.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Loading...this could take a second.");
                    }
                });
                App.vibrate();
                App.initSimWithFile(R.raw.big);
                App.activity.initUIFromState();
                text.setText("Big Economy map is loaded. It starts off with a high labor population and units are on random locations. Press play to start.");
            }
        });
        big2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Loading...this could take a second.");
                    }
                });
                App.vibrate();
                App.initSimWithFile(R.raw.big2);
                App.activity.initUIFromState();
                text.setText("Big Economy II map is loaded. This map is segregated and starts off with a smaller labor population.  Press play to start.");
            }
        });
        boot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.activity.setContentView(App.activity.bootView);
            }
        });

    }
}
