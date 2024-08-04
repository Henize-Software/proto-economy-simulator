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
//package henize.proto;
//
//import android.content.Context;
//import android.support.constraint.ConstraintLayout;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.billingclient.api.Purchase;
//
///**
// * Created by ACR411 on 5/11/2020.
// */
//
//public class BillingView extends ConstraintLayout {
//    ProtoBilling billing;
//    TextView coinBal, points;
//    Button buyCoin, watchVid;
//    public BillingView(Context context) {
//        super(context);
//        inflate(context, R.layout.billing_panel, this);
//        billing = new ProtoBilling();
//        billing.startConnection();
//        coinBal = findViewById(R.id.textCoinCount);
//        points = findViewById(R.id.textPoints);
//        buyCoin = findViewById(R.id.btnBuyProtoCoin);
//        buyCoin.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                App.vibrate();
//                billing.enterBillingFlow();
//            }
//        });
//        watchVid = findViewById(R.id.btnWatchVid);
//        watchVid.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                App.vibrate();
//                //App.activity.adServer.displayRewardVid();
//                Toast.makeText(App.activity, "Video will play when loaded.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    public void update() {
//        billing.syncPurchases();
//        coinBal.setText("Coin bal: " + Integer.toString(billing.getCoinBal()));
//        points.setText("Support Points: " + App.getSupportPoints());
//    }
//}
