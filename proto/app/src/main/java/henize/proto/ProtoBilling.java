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
//import com.android.billingclient.api.AcknowledgePurchaseParams;
//import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingClientStateListener;
//import com.android.billingclient.api.BillingFlowParams;
//import com.android.billingclient.api.BillingResult;
//import com.android.billingclient.api.ConsumeParams;
//import com.android.billingclient.api.ConsumeResponseListener;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchasesUpdatedListener;
//import com.android.billingclient.api.SkuDetails;
//import com.android.billingclient.api.SkuDetailsParams;
//import com.android.billingclient.api.SkuDetailsResponseListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by ACR411 on 5/11/2020.
// */
//
//public class ProtoBilling implements PurchasesUpdatedListener {
//    private String skuString = "proto_coin"; //"android.test.purchased"; //
//    private BillingClient billingClient;
//    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
//    public Purchase.PurchasesResult purchasesResult;
//    private SkuDetails skuProtoCoin;
//
//    public ProtoBilling() {
//        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
//            @Override
//            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
//
//            }
//        };
//    }
//    public void syncPurchases() {
//        purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
//    }
//    public int getCoinBal() {
///*        if(purchasesResult == null) {
//            syncPurchases();
//            //return 0;
//        }
//        int bal = 0;
//        List<Purchase> list = purchasesResult.getPurchasesList();
//        if(list != null) {
//            for (Purchase p : list) {
//                if (p.getSku().equals(skuString)) {
//                    bal++;
//                    consume(p);
//                }
//            }
//        }*/
//
//        return App.getCoins();
//    }
//    public void startConnection() {
//        billingClient = BillingClient.newBuilder(App.activity).setListener(this).enablePendingPurchases().build();
//
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//
//            @Override
//            public void onBillingSetupFinished(BillingResult billingResult) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                    List<String> skuList = new ArrayList<>();
//                    skuList.add(skuString);
//                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
//                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
//                    billingClient.querySkuDetailsAsync(params.build(),
//                            new SkuDetailsResponseListener() {
//                                @Override
//                                public void onSkuDetailsResponse(BillingResult billingResult,
//                                                                 List<SkuDetails> skuDetailsList) {
//                                    // Process the result.
//                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
//                                        for (SkuDetails skuDetails : skuDetailsList) {
//                                            String sku = skuDetails.getSku();
//                                            String price = skuDetails.getPrice();
//                                            if (skuString.equals(sku)) {
//                                                skuProtoCoin = skuDetails;
//                                            }
//                                        }
//                                    }
//                                }
//                            });
//                }
//
//            }
//        });
//}
//    public void enterBillingFlow() {
//        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
//                .setSkuDetails(skuProtoCoin)
//                .build();
//        int responseCode = billingClient.launchBillingFlow(App.activity, flowParams).getResponseCode();
//
//    }
//    public void consume(Purchase p) {
//        ConsumeParams consumeParams =
//                ConsumeParams.newBuilder()
//                        .setPurchaseToken(p.getPurchaseToken())
//                        //.setDeveloperPayload(/* payload */)
//                        .build();
//
//        ConsumeResponseListener listener = new ConsumeResponseListener() {
//            @Override
//            public void onConsumeResponse(BillingResult billingResult, String outToken) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    // Handle the success of the consume operation.
//                    // For example, increase the number of coins inside the user's basket.
//                    App.setCoins(App.getCoins() + 1);
//                }
//            };
//
//
//    };
//        billingClient.consumeAsync(consumeParams, listener);
//    }
//
//    @Override
//    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
//        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
//                && purchases != null) {
//            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
//            }
//        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//            // Handle an error caused by a user cancelling the purchase flow.
//        } else {
//            App.activity.bootView.log(billingResult.getDebugMessage());
//        }
//    }
//
//    private void handlePurchase(Purchase purchase) {
//        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
//            // Grant entitlement to the user.
//
//            // Acknowledge the purchase if it hasn't already been acknowledged.
//            if (!purchase.isAcknowledged()) {
//                AcknowledgePurchaseParams acknowledgePurchaseParams =
//                        AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.getPurchaseToken())
//                                .setDeveloperPayload("REV0")
//                                .build();
//                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
//                consume(purchase);
//            }
//        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
//            // Here you can confirm to the user that they've started the pending
//            // purchase, and to complete it, they should follow instructions that
//            // are given to them. You can also choose to remind the user in the
//            // future to complete the purchase if you detect that it is still
//            // pending.
//        }
//    }
//}
