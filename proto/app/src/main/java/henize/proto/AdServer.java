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
//import android.view.View;
//import android.widget.Toast;
//
//
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.reward.RewardItem;
//import com.google.android.gms.ads.reward.RewardedVideoAd;
//import com.google.android.gms.ads.reward.RewardedVideoAdListener;
//
//
// //Created by ACR411 on 8/13/2018.
//
//
//
//public class AdServer extends AdListener implements RewardedVideoAdListener{
//    static AdServer instance;
//    Context context;
//
//    InterstitialAd interstitialAd;
//    RewardedVideoAd mRewardedVideoAd;
//
//    String adMobAppID, adMobMainAdID, adMobCurrAdID, adMobTraderAdID, adMobRewardVidID;
//
//    static boolean testAds;
//    private boolean show;
//    private boolean showing;
//
//    public AdServer(Context mainActivity, boolean useTestAds){
//        //if(instance != null) throw new RuntimeException("AdServer already exists.");
//
//        instance = this;
//        context = mainActivity;
//
//        initAdIDs(useTestAds);
//        MobileAds.initialize(context, adMobAppID);
//
//        interstitialAd = new InterstitialAd(context);
//        interstitialAd.setAdUnitId(adMobMainAdID);
//        interstitialAd.setAdListener(this);
//
//        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
//        mRewardedVideoAd.setRewardedVideoAdListener(this);
//    }
//    private String loadID(int id) {
//        return context.getResources().getString(id);
//    }
//    void initAdIDs(boolean useTestAds) {
//        if(useTestAds) {
//            adMobAppID = loadID(R.string.adMobAppIDTest);
//            adMobMainAdID = loadID(R.string.adMobMainAdIDTest);
//            adMobCurrAdID = loadID(R.string.adMobBannerAdIDTest);
//            adMobTraderAdID = loadID(R.string.adMobBannerAdIDTest);
//            adMobRewardVidID = loadID(R.string.adMobRewardVidIDTest);
//            testAds = true;
//        } else {
//            adMobAppID = loadID(R.string.adMobAppID);
//            adMobMainAdID = loadID(R.string.adMobMainAdID);
//            adMobCurrAdID = loadID(R.string.adMobCurrAdID);
//            adMobTraderAdID = loadID(R.string.adMobTraderAdID);
//            adMobRewardVidID = loadID(R.string.adMobRewardVidID);
//            testAds = false;
//        }
//    }
//    public void setShowInteruptiveAds(boolean b) {
//        show = b;
//    }
//    public boolean isShowing() {
//        return showing;
//    }
//    public void displayBanner(View view) {
//        if(context == null || testAds) return;
//        ((AdView)view).loadAd(new AdRequest.Builder().build());
//    }
//    public void displayMainAd() {
//        if(context == null || !show) return;
//
//        interstitialAd.loadAd(new AdRequest.Builder().build());
//
//    }
//    public void displayRewardVid() {
//        if(context == null || !show) return;
//
//        mRewardedVideoAd.loadAd(adMobRewardVidID,
//                new AdRequest.Builder().build());
//    }
//    public void pause(){
//        mRewardedVideoAd.pause(context);
//    }
//    public void resume(){
//        mRewardedVideoAd.resume(context);
//    }
//
//    @Override
//    public void onAdLoaded() {
//        showing = true;
//        interstitialAd.show();
//    }
//
//    @Override
//    public void onAdFailedToLoad(int errorCode) {
//        showing = false;
//    }
//
//    @Override
//    public void onAdOpened() {
//
//    }
//
//    @Override
//    public void onAdLeftApplication() {
//        // Code to be executed when the user has left the app.
//    }
//
//    @Override
//    public void onAdClosed() {
//        showing = false;
//    }
//
//    @Override
//    public void onRewardedVideoAdLoaded() {
//        showing = true;
//        mRewardedVideoAd.show();
//    }
//
//    @Override
//    public void onRewardedVideoAdOpened() {
//
//    }
//
//    @Override
//    public void onRewardedVideoStarted() {
//
//    }
//
//    @Override
//    public void onRewardedVideoAdClosed() {
//        showing = false;
//    }
//
//    @Override
//    public void onRewarded(RewardItem rewardItem) {
//        App.setSupportPoints(App.getSupportPoints() + 1);
//       // App.activity.billingView.update();
//    }
//
//    @Override
//    public void onRewardedVideoAdLeftApplication() {
//
//    }
//
//    @Override
//    public void onRewardedVideoAdFailedToLoad(int i) {
//        showing = false;
//    }
//
//    @Override
//    public void onRewardedVideoCompleted() {
//
//    }
//
//
//}
