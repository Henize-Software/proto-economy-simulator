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

/**
 * Created by ACR411 on 10/2/2018.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


import henize.proto.lib.traders.Trader;



public class ListAdapter extends ArrayAdapter<String>{

    private final Activity context;
/*    private final List<String> title, balance, comValue, isWaiting;
    private final List<Bitmap> imageId;
    private final List<String> comSold;*/
    private final List<Trader> traders;
    //public ListAdapter(Activity context, List<Bitmap> imageId, List<String> title, List<String> balance, List<String> comValue, List<String> comSold, List<String> isWaiting, List<String> traderID) {
    public ListAdapter(Activity context, List<Trader> traders, List<String> numberID){
        super(context, R.layout.list_single, numberID);
        this.context = context;
        this.traders = traders;
/*        this.imageId = imageId;
        this.title = title;
        this.balance = balance;
        this.comValue = comValue;
        this.comSold = comSold;
        this.traderID = traderID;
        this.isWaiting = isWaiting;*/

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;

            rowView = inflater.inflate(R.layout.list_single, null, true);
            ImageView icon = rowView.findViewById(R.id.traderIcon);
            TextView txtType =  rowView.findViewById(R.id.textTraderType);
            TextView txtBal = rowView.findViewById(R.id.textBankBal);
            TextView txtComVal = rowView.findViewById(R.id.textComVal);
            TextView txtUnitsSold = rowView.findViewById(R.id.textUnitsSold);
            TextView txtTraderID = rowView.findViewById(R.id.textTraderID);
            TextView txtIsWaiting = rowView.findViewById(R.id.textIsWaiting);
            TextView txtPrice = rowView.findViewById(R.id.textPrice);
            TextView txtDebt = rowView.findViewById(R.id.textDebt);

            txtType.setText(traders.get(position).traderType);
            txtBal.setText((traders.get(position).account.freeze ? "FROZE" : "") + "Acct bal: $"+App.sim.CtD(traders.get(position).account.getBalance()));
            txtComVal.setText("Units for Sale: " + Integer.toString(traders.get(position).com.size()));
            txtUnitsSold.setText("Sale Count: " + Long.toString(traders.get(position).sale_count));
            txtTraderID.setText(traders.get(position).id);
            txtIsWaiting.setText(traders.get(position).isWaiting ? "Traveling..." :  "");
            txtPrice.setText("Price: $" + App.sim.CtD(traders.get(position).getPrice()));
            txtDebt.setText("Debt: $" + App.sim.CtD(traders.get(position).account.debt));
            try {
                Bitmap b = Bitmap.createScaledBitmap(Icons.traderIcons.get(traders.get(position).traderType),
                        icon.getMaxWidth(), icon.getMaxHeight(), false);
                icon.setImageBitmap(b);
            }catch (Exception e) {
                icon.setImageResource(R.mipmap.ic_launcher);
            }



        return rowView;
    }
}