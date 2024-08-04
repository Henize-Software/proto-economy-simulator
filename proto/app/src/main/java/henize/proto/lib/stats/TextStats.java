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
package henize.proto.lib.stats;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.Serializable;

import henize.proto.App;
import henize.proto.R;
import henize.proto.lib.sim.Simulator;

import static henize.proto.lib.traders.ComType.COAL;
import static henize.proto.lib.traders.ComType.FOOD;
import static henize.proto.lib.traders.ComType.IRON;
import static henize.proto.lib.traders.ComType.LABOR;
import static henize.proto.lib.traders.ComType.TOOL;
import static henize.proto.lib.traders.ComType.TRANSPORTATION;

/**
 * Created by ACR411 on 12/21/2017.
 */

public class TextStats implements Serializable {
    Bitmap bitmap;
    Canvas canvas;
    TextPaint paint;
    Simulator sim;
    long lastInterval;
    boolean blinkSwitch;

    String laborPrice, foodPrice, toolPrice, coalPrice, ironPrice, shipPrice,
            transCount, gdp, totalCurr, acctBal, stockBanner, foodStock, toolStock, coalStock, ironStock, lcSpacer, popCount, currSym;

    boolean fullStats;
    boolean init;
    public TextStats(Simulator sim) {
        this.sim = sim;
        paint = new TextPaint();
        paint.setColor(Color.WHITE);

        paint.setTypeface(Typeface.MONOSPACE);
    }

    public void setLanguage() {
        Resources r = App.activity.getResources();
        laborPrice = r.getString(R.string.laborPrice);
        foodPrice = r.getString(R.string.foodPrice);
        toolPrice = r.getString(R.string.toolPrice);
        coalPrice = r.getString(R.string.coalPrice);
        ironPrice = r.getString(R.string.ironPrice);
        shipPrice = r.getString(R.string.shipPrice);
        transCount = r.getString(R.string.transCount);
        gdp = r.getString(R.string.gdp);
        totalCurr = r.getString(R.string.totalCurr);
        acctBal = r.getString(R.string.acctBal);
        stockBanner = r.getString(R.string.stockBanner);
        foodStock = r.getString(R.string.foodStock);
        toolStock = r.getString(R.string.toolStock);
        coalStock = r.getString(R.string.coalStock);
        ironStock = r.getString(R.string.ironStock);
        lcSpacer = r.getString(R.string.lcSpacer);
        popCount = r.getString(R.string.popCount);
        currSym = r.getString(R.string.currSymbol);
    }

    public Bitmap draw(int width, int height, Canvas c) {

        if(bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            float textSize = width / App.sim.access.config.TEXT_SIZE_DIV;

            if(fullStats) {
                if(textSize * App.sim.access.config.TEXT_SIZE_DIV_LIMITED_HEIGHT_FULL_SCREEN > height) {
                    textSize = height / App.sim.access.config.TEXT_SIZE_DIV_LIMITED_HEIGHT_FULL_SCREEN;
                }
                paint.setTextSize(textSize);

}
            else {
                if(textSize * App.sim.access.config.TEXT_SIZE_DIV_LIMITED_HEIGHT > height) {
                    textSize = height / App.sim.access.config.TEXT_SIZE_DIV_LIMITED_HEIGHT;
                }
                paint.setTextSize(textSize);
            }
            //bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            //canvas = new Canvas(bitmap);
            setLanguage();
        }

        if(blinkSwitch) {
            if (System.currentTimeMillis() - lastInterval > 5000) {
                blinkSwitch = false;
            }
        }
        canvas = c;
        canvas.drawColor(Color.BLACK);

        String text = "";
        text = laborPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(LABOR)) + '\n';
        paint.setColor(Color.GREEN);
        StaticLayout textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();

        textLayout.draw(canvas);
        canvas.restore();

        text = foodPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(FOOD)) + '\n';
        paint.setColor(Color.YELLOW);
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = toolPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(TOOL)) + '\n';
        paint.setColor(Color.CYAN);
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = coalPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(COAL)) + '\n';
        paint.setColor(Color.RED);
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = ironPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(IRON)) + '\n';
        paint.setColor(Color.rgb(255, 165, 0));
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        String overload = "";
        if(sim.wait_for_t) {
            lastInterval = System.currentTimeMillis();
            blinkSwitch = true;
        }
        if(blinkSwitch) {
            overload = " OVERLOAD";
        }


        text = shipPrice + currSym + sim.CtD(sim.access.dir.getAveragePriceOf(TRANSPORTATION)) + overload + '\n';
        paint.setColor(Color.rgb(255, 192, 203));
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = transCount + Long.toString(sim.access.bankStats.transactionCount) + '\n';
        text += gdp + currSym + sim.CtD(sim.access.bankStats.totalTrValue) + '\n';
        text += totalCurr + currSym + sim.CtD(sim.access.bankStats.totalCurrency);



        paint.setColor(Color.WHITE);
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "";

        text += acctBal + '\n';
        text += stockBanner + '\n';
        text += foodStock + '\n';
        text += toolStock + '\n';
        text += coalStock + '\n';
        text += ironStock + '\n';

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "";

        text += currSym + sim.CtD(sim.access.taxAccount.getBalance()) + "\n\n";
        text += Integer.toString(sim.access.master.foodTrader.com.size()) + '\n';
        text += Integer.toString(sim.access.master.toolTrader.com.size()) + '\n';
        text += Integer.toString(sim.access.master.coalTrader.com.size()) + '\n';
        text += Integer.toString(sim.access.master.ironTrader.com.size()) + '\n';
        text += (sim.access.bank.fail ? "*BANK FAILURE*" : "");
        text += (sim.access.bank.creditMeltdown ? "CREDIT CRUNCH" : "");


        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "";

        text += "\n\n";
        text += popCount + '\n';
        text += lcSpacer + Integer.toString(sim.access.traderStats.laborTraderCount);
        paint.setColor(Color.GREEN);
        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();
        paint.setColor(Color.WHITE);

        return bitmap;
    }
}
