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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACR411 on 12/21/2017.
 */

public class TraderView_old_1_0 extends RelativeLayout {
    Spinner bf, bt, bc, bi, sf, st, sc, si;
    ToggleButton tbf, tbt, tbc, tbi, tsf, tst, tsc, tsi;
    EditText ebf, ebt, ebc, ebi, esf, est, esc, esi;
    boolean spinInit = true;
    public TraderView_old_1_0(Context context) {
        super(context);
        inflate(getContext(), R.layout.trader_panel, this);
        bf = findViewById(R.id.spinnerBF);
        bt = findViewById(R.id.spinnerBT);
        bc = findViewById(R.id.spinnerBC);
        bi = findViewById(R.id.spinnerBI);
        sf = findViewById(R.id.spinnerSF);
        st = findViewById(R.id.spinnerST);
        sc = findViewById(R.id.spinnerSC);
        si = findViewById(R.id.spinnerSI);

        tbf = findViewById(R.id.tbf);
        tbt = findViewById(R.id.tbt);
        tbc = findViewById(R.id.tbc);
        tbi = findViewById(R.id.tbi);

        tsf = findViewById(R.id.tsf);
        tst = findViewById(R.id.tst);
        tsc = findViewById(R.id.tsc);
        tsi = findViewById(R.id.tsi);

        ebf = findViewById(R.id.editBF);
        ebt = findViewById(R.id.editBT);
        ebc = findViewById(R.id.editBC);
        ebi = findViewById(R.id.editBI);

        esf = findViewById(R.id.editSF);
        est = findViewById(R.id.editST);
        esc = findViewById(R.id.editSC);
        esi = findViewById(R.id.editSI);

        //////////////////////////food
        ebf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.fb_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a value number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });
        esf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.fs_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tbf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.fb_enabled = b;
            }
        });
        tsf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.fs_enabled = b;
            }
        });

        /////////////////////tool
        ebt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.tb_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        est.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.ts_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.tb_enabled = b;
            }
        });
        tst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.ts_enabled = b;
            }
        });

        ///////////////coal
        ebc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.cb_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        esc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.cs_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tbc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.cb_enabled = b;
            }
        });
        tsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.cs_enabled = b;
            }
        });

        ////////////////iron
        ebi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.ib_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        esi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    App.sim.access.master.is_setValue = App.sim.DtoC(charSequence.toString());
                }catch (Exception e) {
                    Toast.makeText(getContext(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tbi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.ib_enabled = b;
            }
        });
        tsi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.sim.access.master.is_enabled = b;
            }
        });

        List<String> buyOptions = new ArrayList<String>();
        buyOptions.add("Now at market value");
        buyOptions.add("Price is below [set value]");
        buyOptions.add("Price is above [set value]");

        List<String> sellOptions = new ArrayList<String>();
        sellOptions.add("Now at market value");
        sellOptions.add("Now at [set price]");
        sellOptions.add("Now at [set value] above market value");
        sellOptions.add("Now at [set value] below market value");
        sellOptions.add("Price is below [set value]");
        sellOptions.add("Price is above [set value]");

        ArrayAdapter<String> adapterB = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, buyOptions);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(getContext(), R.layout.spinner_big, sellOptions);

        adapterB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bf.setAdapter(adapterB);
        bt.setAdapter(adapterB);
        bc.setAdapter(adapterB);
        bi.setAdapter(adapterB);

        sf.setAdapter(adapterS);
        st.setAdapter(adapterS);
        sc.setAdapter(adapterS);
        si.setAdapter(adapterS);

        SetSpinnerListeners();
        initFromState();


    }

    public void initFromState() {
        tbf.setChecked( App.sim.access.master.fb_enabled);
        tbt.setChecked( App.sim.access.master.tb_enabled);
        tbc.setChecked( App.sim.access.master.cb_enabled);
        tbi.setChecked( App.sim.access.master.ib_enabled);
        tsf.setChecked( App.sim.access.master.fs_enabled);
        tst.setChecked( App.sim.access.master.ts_enabled);
        tsc.setChecked( App.sim.access.master.cs_enabled);
        tsi.setChecked( App.sim.access.master.is_enabled);

        ebf.setText(App.sim.CtD(App.sim.access.master.fb_setValue));
        ebt.setText(App.sim.CtD(App.sim.access.master.tb_setValue));
        ebc.setText(App.sim.CtD(App.sim.access.master.cb_setValue));
        ebi.setText(App.sim.CtD(App.sim.access.master.ib_setValue));
        esf.setText(App.sim.CtD(App.sim.access.master.fs_setValue));
        est.setText(App.sim.CtD(App.sim.access.master.ts_setValue));
        esc.setText(App.sim.CtD(App.sim.access.master.cs_setValue));
        esi.setText(App.sim.CtD(App.sim.access.master.is_setValue));

        bf.setSelection(((ArrayAdapter)bf.getAdapter()).getPosition(App.sim.access.master.f_buyCond));
        bt.setSelection(((ArrayAdapter)bt.getAdapter()).getPosition(App.sim.access.master.t_buyCond));
        bc.setSelection(((ArrayAdapter)bc.getAdapter()).getPosition(App.sim.access.master.c_buyCond));
        bi.setSelection(((ArrayAdapter)bi.getAdapter()).getPosition(App.sim.access.master.i_buyCond));
        sf.setSelection(((ArrayAdapter)sf.getAdapter()).getPosition(App.sim.access.master.f_sellCond));
        st.setSelection(((ArrayAdapter)st.getAdapter()).getPosition(App.sim.access.master.t_sellCond));
        sc.setSelection(((ArrayAdapter)sc.getAdapter()).getPosition(App.sim.access.master.c_sellCond));
        si.setSelection(((ArrayAdapter)si.getAdapter()).getPosition(App.sim.access.master.i_sellCond));;
    }

    private void SetSpinnerListeners() {

        bf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.f_buyCond = (String)adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.t_buyCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.c_buyCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.i_buyCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.f_sellCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        st.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.t_sellCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinInit) return;
                App.sim.access.master.c_sellCond = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        si.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(spinInit) return;
            App.sim.access.master.i_sellCond = (String)adapterView.getItemAtPosition(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
        spinInit = false;
    }

}
