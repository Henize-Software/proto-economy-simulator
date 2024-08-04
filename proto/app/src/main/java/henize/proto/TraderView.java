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
import android.widget.TabHost;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import henize.proto.lib.traders.MasterTrader;
import henize.proto.lib.traders.Trader;

/**
 * Created by ACR411 on 5/11/2018.
 */

public class TraderView extends RelativeLayout {

    Spinner selectBuyCom, selectBuyCond, selectSellCom, selectSellCond;
    Button btnOrder, btnCancelBuy, btnSell, btnCancelSell;
    EditText editBuyQuant, editBuyPrice, editSellQuant, editSellPrice, sellOrderDisplay, buyOrderDisplay;

    Trader selectedTrader;

    public TraderView(Context context) {
        super(context);
        inflate(getContext(), R.layout.trader_panel_v2, this);

        initTabs();

        initSpinners();

        initButtons();

        editBuyQuant = findViewById(R.id.editBuyQuant);
        editBuyPrice = findViewById(R.id.editBuySetPrice);
        editSellQuant = findViewById(R.id.editSellQuant);
        editSellPrice = findViewById(R.id.editSellSetPrice);

        //AdServer.instance.displayBanner(findViewById(R.id.adViewTrader));

    }
    private void initDisplay() {

       sellOrderDisplay = findViewById(R.id.editTextSellOrders);
       buyOrderDisplay = findViewById(R.id.editTextBuyOrders);

    }
    private void initButtons() {
        btnOrder = findViewById(R.id.buttonOrder);
        btnCancelBuy = findViewById(R.id.buttonCancelBuy);
        btnSell = findViewById(R.id.buttonSell);
        btnCancelSell = findViewById(R.id.buttonCancelSell);

        orderButtonEvent();
        buyCancelEvent();
        sellButtonEvent();
        cancelSellEvent();
    }

    private void cancelSellEvent() {
        btnCancelSell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCom(selectSellCom);
                selectedTrader.sellEnabled(false);
                updateDisplay();
                App.vibrate();
            }
        });
    }

    private void sellButtonEvent() {
        btnSell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initDisplay();
                selectCom(selectSellCom);
                selectedTrader.setSellCond(sell_to_english(selectSellCond.getSelectedItemPosition()));
                try {

                    selectedTrader.setSellValues(Integer.parseInt(editSellQuant.getText().toString()), App.sim.DtoC(editSellPrice.getText().toString()));
                    selectedTrader.sellEnabled(true);
                    updateDisplay();
                    App.vibrate();
                } catch (Throwable e) {
                    Toast.makeText(getContext(), R.string.currErr2, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void buyCancelEvent() {
        btnCancelBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCom(selectBuyCom);
                selectedTrader.buyEnabled(false);
                updateDisplay();
                App.vibrate();
            }
        });
    }

    private void orderButtonEvent() {
        btnOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initDisplay();
                String buyCond = buy_to_english(selectBuyCond.getSelectedItemPosition());
                selectCom(selectBuyCom);
                try {
                    selectedTrader.setBuyValues(Integer.parseInt(editBuyQuant.getText().toString()), App.sim.DtoC(editBuyPrice.getText().toString()));
                    selectedTrader.setBuyCond(buyCond);

                    selectedTrader.buyEnabled(true);
                    updateDisplay();
                    App.vibrate();
                } catch (Throwable e) {
                    Toast.makeText(getContext(), R.string.currErr2, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectCom(Spinner select) {

        int comSelect = select.getSelectedItemPosition();
        switch(comSelect) {
            case 0: {
                selectedTrader = App.sim.access.master.foodTrader;
                break;
            }
            case 1: {
                selectedTrader = App.sim.access.master.toolTrader;
                break;
            }
            case 2: {
                selectedTrader = App.sim.access.master.coalTrader;
                break;
            }
            case 3: {
                selectedTrader = App.sim.access.master.ironTrader;
                break;
            }
        }
    }
    public void initFromState(){
        initDisplay();
        updateDisplay();
    }
    private void updateDisplay() {
        try {
            if (App.sim == null) {
                return;
            }
            MasterTrader m = App.sim.access.master;
            String buyDisp = "";
            String sellDisp = "";

            if (m.fb_enabled)
                buyDisp += getResources().getString(R.string.buyingfood) + _format_string(buy_to_local(m.f_buyCond), m.fb_setValue);
            if (m.tb_enabled)
                buyDisp += getResources().getString(R.string.buyingtools) + _format_string(buy_to_local(m.t_buyCond), m.tb_setValue);
            if (m.cb_enabled)
                buyDisp += getResources().getString(R.string.buyingcoal) + _format_string(buy_to_local(m.c_buyCond), m.cb_setValue);
            if (m.ib_enabled)
                buyDisp += getResources().getString(R.string.buyingiron) + _format_string(buy_to_local(m.i_buyCond), m.ib_setValue);


            if (m.fs_enabled)
                sellDisp += getResources().getString(R.string.sellingfood) + _format_string(sell_to_local(m.f_sellCond), m.fs_setValue);
            if (m.ts_enabled)
                sellDisp += getResources().getString(R.string.sellingtools) + _format_string(sell_to_local(m.t_sellCond), m.ts_setValue);
            if (m.cs_enabled)
                sellDisp += getResources().getString(R.string.sellingcoal) + _format_string(sell_to_local(m.c_sellCond), m.cs_setValue);
            if (m.is_enabled)
                sellDisp += getResources().getString(R.string.sellingiron) + _format_string(sell_to_local(m.i_sellCond), m.is_setValue);

            buyOrderDisplay.setText(buyDisp);
            sellOrderDisplay.setText(sellDisp);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Anomaly detected, no big deal.", Toast.LENGTH_SHORT).show();
        }
    }

    private String _format_string(String condition, long setValue) {
        if(condition == getResources().getString(R.string.buy0))
        {
            return (' ' + condition + '\n').toLowerCase();
        }else {
            return (!condition.contains("Now") ? " when " : "") + (condition.replace(getResources().getString(R.string.setprice),  getResources().getString(R.string.currSymbol) + App.sim.CtD(setValue)) + '\n') .toLowerCase().replace("now", "");
        }
    }

    private String buy_to_english(int pos) {
        switch(pos) {
            case 0: return "Now at market value";
            case 1: return "Price is below [set price]";
            case 2: return "Price is above [set price]";
        }
        return null;
    }
    private String sell_to_english(int pos) {
        switch(pos) {
            case 0: return "Now at market value";
            case 1: return "Now at [set price]";
            case 2: return "Now at [set price] above market value";
            case 3: return "Now at [set price] below market value";
            case 4: return "Price is below [set price]";
            case 5: return "Price is above [set price]";
        }
        return null;
    }
    private String sell_to_local(String english) {
        int id = 0;
        switch(english) {
            case "Now at market value": {
                id = R.string.sell0;
                break;
            }
            case "Now at [set price]": {
                id = R.string.sell1;
                break;
            }
            case "Now at [set price] above market value":{
                id = R.string.sell2;
                break;
            }
            case "Now at [set price] below market value": {
                id = R.string.sell3;
                break;
            }
            case "Price is below [set price]": {
                id = R.string.sell4;
                break;
            }
            case "Price is above [set price]": {
                id = R.string.sell5;
                break;
            }
        }
        return getResources().getString(id);
    }
    private String buy_to_local(String english) {
        int id = 0;
        switch(english) {
            case "Now at market value": {
                id = R.string.buy0;
                break;
            }
            case "Price is below [set price]": {
                id = R.string.buy1;
                break;
            }
            case "Price is above [set price]": {
                id = R.string.buy2;
                break;
            }
        }
        return getResources().getString(id);
    }


    private void initSpinners() {
        selectBuyCom = findViewById(R.id.selectComBuy);
        selectBuyCond = findViewById(R.id.selectBuyCond);
        selectSellCond = findViewById(R.id.selectSellCond);
        selectSellCom = findViewById(R.id.selectComSell);

        List<String> buyOptions = new ArrayList<String>();
        List<String> sellOptions = new ArrayList<String>();
        List<String> comOptions = new ArrayList<String>();

        comOptions.add(getResources().getString(R.string.food));
        comOptions.add(getResources().getString(R.string.tools));
        comOptions.add(getResources().getString(R.string.coal));
        comOptions.add(getResources().getString(R.string.iron));

        buyOptions.add(getResources().getString(R.string.buy0));
        buyOptions.add(getResources().getString(R.string.buy1));
        buyOptions.add(getResources().getString(R.string.buy2));

        sellOptions.add(getResources().getString(R.string.sell0));
        sellOptions.add(getResources().getString(R.string.sell1));
        sellOptions.add(getResources().getString(R.string.sell2));
        sellOptions.add(getResources().getString(R.string.sell3));
        sellOptions.add(getResources().getString(R.string.sell4));
        sellOptions.add(getResources().getString(R.string.sell5));

        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, comOptions);
        ArrayAdapter<String> adapterB = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, buyOptions);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, sellOptions);

        adapterB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectBuyCom.setAdapter(adapterC);
        selectSellCom.setAdapter(adapterC);
        selectBuyCond.setAdapter(adapterB);
        selectSellCond.setAdapter(adapterS);

    }




    private void initTabs() {
        TabHost host = (TabHost)findViewById(R.id.tabBuySell);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("buytab");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.buy));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("selltab");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.sell));
        host.addTab(spec);
    }
}
