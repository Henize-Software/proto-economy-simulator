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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import henize.proto.lib.sim.Config;


import henize.proto.lib.sim.Simulator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ACR411 on 8/15/2018.
 */

public class App {
    public static String stateFile = "henize.proto.state43";
    public static Simulator sim;
    public static MainActivity activity;
    public static Vibrator vibrator;
    public static String map;
    public static int scale;
    public volatile static boolean loading;
    public volatile static boolean saving;
    public static SupportPoints supportPoints;
    public static ProtoCoin coins;

public static int getSupportPoints() {
    syncPointsCoinsToMem();
    return supportPoints.points;
}
public static void setSupportPoints(int val) {
    supportPoints = new SupportPoints();
    supportPoints.points = val;
    syncPointsCoinsToStorage();
}
    public static int getCoins() {
        syncPointsCoinsToMem();
        return coins.bal;
    }
    public static void setCoins(int val) {
        coins = new ProtoCoin();
        coins.bal = val;
        syncPointsCoinsToStorage();
    }
    public static void syncPointsCoinsToMem() {
        try {
            InputStream is = activity.openFileInput("dat");
            byte[] buff = new byte[is.available()];
            is.read(buff);
            is.close();

            is = new ByteArrayInputStream(buff);
            //read file and convert to object
            ObjectInputStream in = new ObjectInputStream(is);
            supportPoints = (SupportPoints)in.readObject();
            coins = (ProtoCoin)in.readObject();
        } catch (Exception e) {
            supportPoints = new SupportPoints();
            coins = new ProtoCoin();
            activity.bootView.log(e.getMessage());
        }
    }
    public static void syncPointsCoinsToStorage()  {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(supportPoints);
            out.writeObject(coins);
            //open file and write output stream
            FileOutputStream fo = activity.openFileOutput("dat", MODE_PRIVATE);
            fo.write(os.toByteArray());
            fo.close();
            os.close();
        } catch (Exception e) {
            activity.bootView.log(e.getMessage());
        }
    }
    public static void initSim() {
        App.sim = new Simulator(scale, map, null);
    }

    public static void initSimWithFile(int id) {
        try {
            //read file and convert to object

            ObjectInputStream in = new ObjectInputStream(activity.getResources().openRawResource(id));
            Config cfg = (Config)in.readObject();
            map = (String)in.readObject();
            in.close();
            App.sim = new Simulator(scale, map, cfg);

        } catch (Throwable e) {
            Log.println(0, "init", e.getMessage());
        }
    }

    public static void deleteStateAndTerminate(){
        for(int i = 0; i < 100; i++) {
            activity.deleteFile("henize.proto.state" + Integer.toString(i));
        }
        activity.deleteFile("henize.proto.graph");
        System.exit(0);
    }

    public static void _record_serialization () {
        if(true) return; //false = record
        try {
            String record_map = activity.getResources().getString(R.string.bigmap2);
            //save sim state in os output stream.
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(new Config());
            out.writeObject(record_map);
            //open file and write output stream
            FileOutputStream fo = activity.openFileOutput("big2.proto", MODE_PRIVATE);
            fo.write(os.toByteArray());
            fo.close(); os.close();
        } catch (IOException e) {
        //    Toast.makeText(activity, "Unexpected error; serialization", Toast.LENGTH_SHORT).show();
        }
        System.exit(0);
    }

    public static AlertDialog.Builder builder;
    public static DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(i == DialogInterface.BUTTON_POSITIVE) {
                activity.moveTaskToBack(true);
            }
        }
    };
    public static DialogInterface.OnClickListener bigRedButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(i == DialogInterface.BUTTON_POSITIVE) {
                App.vibrate();
                deleteStateAndTerminate();

            }
        }
    };

    public static void vibrate() {

        // Vibrate for 1000 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            App.vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            App.vibrator.vibrate(50);
        }
    }

    public static void showExitDialgue() {
        builder = new AlertDialog.Builder(activity);

        builder.setMessage(R.string.exit).setPositiveButton(R.string.yes, listener).setNegativeButton(R.string.no, listener);
        builder.create().show();
    }

    public static void showBigRedDialogue() {
        builder = new AlertDialog.Builder(activity);

        builder.setMessage("Continue with factory reset?").setPositiveButton(R.string.yes, bigRedButtonListener).setNegativeButton(R.string.no, bigRedButtonListener).setTitle("The app will terminate!");
        builder.create().show();
    }

    public static void showBasicDialogue(int title, int message) {
        showBasicDialogue(activity.getResources().getString(title), activity.getResources().getString(message));
    }

    public static void showBasicDialogue(String title, String message) {
        builder = new AlertDialog.Builder(activity);

        builder.setMessage(message).setTitle(title);
        builder.create().show();
    }
}
