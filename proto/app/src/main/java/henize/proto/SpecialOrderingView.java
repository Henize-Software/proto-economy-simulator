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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import henize.proto.lib.traders.ComType;
import henize.proto.lib.traders.Trader;

import static henize.proto.lib.traders.ComType.COAL;
import static henize.proto.lib.traders.ComType.FOOD;
import static henize.proto.lib.traders.ComType.IRON;
import static henize.proto.lib.traders.ComType.TOOL;

/**
 * Created by ACR411 on 12/22/2019.
 */

public class SpecialOrderingView extends RelativeLayout {
    Spinner comSelect;
    ComType type;
    EditText quant, list;
    Button order, cancel;
    private Trader selectedTrader;

    public SpecialOrderingView(Context context) {
        super(context);
        inflate(getContext(), R.layout.special_order_panel, this);

        comSelect = findViewById(R.id.spinnerComSelect);
        quant = findViewById(R.id.editTextQuant);
        list = findViewById(R.id.editTextOrderList);
        order = findViewById(R.id.buttonOrder);
        cancel = findViewById(R.id.buttonBreakPrioirty);

        List<String> comOptions = new ArrayList<String>();

        comOptions.add(getResources().getString(R.string.food));
        comOptions.add(getResources().getString(R.string.tools));
        comOptions.add(getResources().getString(R.string.coal));
        comOptions.add(getResources().getString(R.string.iron));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, comOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        comSelect.setAdapter(adapter);

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                App.sim.breakPriority();
                App.vibrate();
            }
        });
        order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            selectCom(comSelect);
            int quantity;
            try {
                quantity = Integer.parseInt(quant.getText().toString());
                selectedTrader.buySpecialPriority(type, quantity);
                App.vibrate();
                list.setText(list.getText() + selectedTrader.id + " order for " + Integer.toString(quantity)
                        + " " + type.toString() + "\n");
            } catch (Exception e) {
                App.showBasicDialogue("Invalid", "Enter a valid number");
            }
            }

        });
    }    private void selectCom(Spinner select) {

        int comSelect = select.getSelectedItemPosition();
        switch(comSelect) {
            case 0: {
                selectedTrader = App.sim.access.master.foodTrader;
                type = FOOD;
                break;
            }
            case 1: {
                selectedTrader = App.sim.access.master.toolTrader;
                type = TOOL;
                break;
            }
            case 2: {
                selectedTrader = App.sim.access.master.coalTrader;
                type = COAL;
                break;
            }
            case 3: {
                selectedTrader = App.sim.access.master.ironTrader;
                type = IRON;
                break;
            }
        }
    }
}
