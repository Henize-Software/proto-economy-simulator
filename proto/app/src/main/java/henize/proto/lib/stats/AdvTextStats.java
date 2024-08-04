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
import android.text.Layout;
import android.text.StaticLayout;

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
 * Created by ACR411 on 1/11/2018.
 */

public class AdvTextStats extends TextStats implements Serializable {
    String assetBanner, banksumBanner, bsLabor, bsFood, bsTool, bsCoal, bsIron, bsShip, stockBanner2, sLabor, sFood, sTool, sCoal, sIron,
            comSumBanner, totalBanner, tpLabor, tpFood, tpTool, tpCoal, tpIron, tpShip, currExBanner, cxLabor, cxFood, cxTool, cxCoal, cxIron, cxShip;
    public AdvTextStats(Simulator sim) {
        super(sim);
        fullStats = true;
    }

    public void setLanguage() {
        super.setLanguage();
        Resources r = App.activity.getResources();
        assetBanner = r.getString(R.string.assetBanner);
        banksumBanner = r.getString(R.string.banksumBanner);
        bsLabor = r.getString(R.string.bsLabor);
        bsFood = r.getString(R.string.bsFood);
        bsTool = r.getString(R.string.bsTool);
        bsCoal = r.getString(R.string.bsCoal);
        bsIron = r.getString(R.string.bsIron);
        bsShip = r.getString(R.string.bsShip);
        stockBanner2 = r.getString(R.string.stockBanner2);
        sLabor = r.getString(R.string.sLabor);
        sFood = r.getString(R.string.sFood);
        sTool = r.getString(R.string.sTool);
        sCoal = r.getString(R.string.sCoal);
        sIron = r.getString(R.string.sIron);
        comSumBanner = r.getString(R.string.comSumBanner);
        totalBanner = r.getString(R.string.totalBanner);
        tpLabor = r.getString(R.string.tpLabor);
        tpFood = r.getString(R.string.tpFood);
        tpTool = r.getString(R.string.tpTool);
        tpCoal = r.getString(R.string.tpCoal);
        tpIron = r.getString(R.string.tpIron);
        tpShip = r.getString(R.string.tpShip);
        currExBanner = r.getString(R.string.currExBanner);
        cxLabor = r.getString(R.string.cxLabor);
        cxFood = r.getString(R.string.cxFood);
        cxTool = r.getString(R.string.cxTool);
        cxCoal = r.getString(R.string.cxCoal);
        cxIron = r.getString(R.string.cxIron);
        cxShip = r.getString(R.string.cxShip);

    }
    @Override
    public Bitmap draw(int width, int height, Canvas c) {
        super.draw(width, height, c);

        String text = "\n\n\n\n\n\n\n\n\n\n";
        text += assetBanner;

        StaticLayout textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n";
        text += banksumBanner;
        text += bsLabor + currSym + sim.CtD(sim.bankSum(LABOR)) + '\n';
        text += bsFood + currSym + sim.CtD(sim.bankSum(FOOD)) + '\n';
        text += bsTool + currSym + sim.CtD(sim.bankSum(TOOL)) + '\n';
        text += bsCoal + currSym + sim.CtD(sim.bankSum(COAL)) + '\n';
        text += bsIron + currSym + sim.CtD(sim.bankSum(IRON)) + '\n';
        text += bsShip + currSym + sim.CtD(sim.bankSum(TRANSPORTATION)) + '\n';
        text += "Debt    : $" + sim.CtD(sim.access.bankStats.totalDebt);


        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n";

        text += stockBanner2 + '\n';
        text += sLabor + '\n';
        text += sFood + '\n';
        text += sTool + '\n';
        text += sCoal + '\n';
        text += sIron + '\n';

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n\n";

        text += Integer.toString(sim.stockSum(LABOR)) + '\n';
        text += Integer.toString(sim.stockSum(FOOD)) + '\n';
        text += Integer.toString(sim.stockSum(TOOL)) + '\n';
        text += Integer.toString(sim.stockSum(COAL)) + '\n';
        text += Integer.toString(sim.stockSum(IRON)) + '\n';

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        text += comSumBanner;

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        text += totalBanner + '\n';
        text += tpLabor + Integer.toString(sim.access.comStats.t_laborCount) + '\n';
        text += tpFood + Integer.toString(sim.access.comStats.t_foodCount) + '\n';
        text += tpTool + Integer.toString(sim.access.comStats.t_toolCount) + '\n';
        text += tpCoal + Integer.toString(sim.access.comStats.t_coalCount) + '\n';
        text += tpIron + Integer.toString(sim.access.comStats.t_ironCount) + '\n';
        text += tpShip + Integer.toString(sim.access.comStats.t_trCount) + '\n';


        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        text += currExBanner + '\n';
        text += cxLabor + '\n';
        text += cxFood + '\n';
        text += cxTool + '\n';
        text += cxCoal + '\n';
        text += cxIron + '\n';
        text += cxShip + '\n';
        text += "Reserve  :        ";

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        text = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
        text += Integer.toString(sim.access.comStats.laborCount) + '\n';
        text += Integer.toString(sim.access.comStats.foodCount) + '\n';
        text += Integer.toString(sim.access.comStats.toolCount) + '\n';
        text += Integer.toString(sim.access.comStats.coalCount) + '\n';
        text += Integer.toString(sim.access.comStats.ironCount) + '\n';
        text += Integer.toString(sim.access.comStats.trCount) + '\n';
        text += "$" + sim.CtD(sim.access.bank.reserve);

        textLayout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
        canvas.save();
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;


    }
}
