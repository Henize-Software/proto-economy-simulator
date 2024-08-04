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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import henize.proto.App;

/**
 * Created by ACR411 on 12/17/2017.
 */

public class Graph implements Serializable {
    public static final int FOOD_OFFSET = 4;
    public static final int TR_OFFSET = 1;
    public static final int COAL_OFFSET = 3;
    public static final int IRON_OFFSET = 2;
    public transient List<GraphData> virt_days;
    transient List<GraphData> _temp;
    transient Bitmap _graph;
    transient Canvas c;
    transient Paint _p;
    transient long timeElapsed;
    public boolean instantRefresh;

    DecimalFormat df;

    transient volatile boolean doneLoading;
    transient volatile float loadProgress;
    transient Thread _thread;

    int resolution;
    //Timer timer;
    public Graph(){

        virt_days = new ArrayList<GraphData>();
        _temp = new ArrayList<>();
        df = new DecimalFormat();
        df.setMaximumFractionDigits(0);

    }

    public Bitmap DrawGraph(int width, int height, int spread, Canvas c)
    {
        this.c = c;
        if(_graph == null || _graph.getWidth() != width || _graph.getHeight() != height) {
            _graph = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            _p = new Paint();
            _p.setColor(Color.WHITE);
            _p.setTextSize( 24);
        }

        if(doneLoading) {
            for(GraphData p : _temp) {
                virt_days.add(p);
            }
            doneLoading = false;
            _thread = null;
        }

        if(_thread != null && _thread.isAlive()) {
            c.drawColor(Color.BLACK);
            c.drawText("Loading graph data... %" + df.format(loadProgress), 0, height - 24, _p );
            return _graph;
        }


        if (virt_days == null || virt_days.size() == 0) return _graph;


/*        if(virt_days.size() > 5000) {
            List<GraphData> compacted = new ArrayList<>();
            for (int i = 0; i < virt_days.size(); i+= i < virt_days.size() - 1250 ? 2 : 1) {
                compacted.add(virt_days.get(i));
            }
            virt_days.clear();
            virt_days = compacted;
        }*/


        List<GraphData> points = virt_days;
/*        if(spread > 0 && spread < virt_days.size()) {
            points = new ArrayList<>();
            for (int i = virt_days.size() - 1 - spread; i < virt_days.size(); i++) {
                points.add(virt_days.get(i));
            }
        }*/

        if(!instantRefresh && System.currentTimeMillis() - timeElapsed < 100) return _graph;
        instantRefresh = false;
        timeElapsed = System.currentTimeMillis();

        c.drawColor(Color.BLACK);



        //Canvas c = new Canvas(_graph);
        Paint p = new Paint();
        GraphData dot;
        if(spread < 0) spread = 0;
        int _spread = spread;
        if(spread > virt_days.size() || spread <= 0) _spread = virt_days.size();

        resolution = _spread / width;
        if(resolution == 0) resolution = 1;
        long _max = GetMaximum(points, _spread);
        float _xstep = (float)width / _spread * resolution;
        float _ystep = (float)height / _max;

        float _xprev = 0;
        float _yprev = height - points.get(points.size() - _spread).tool * _ystep;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.CYAN);
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - dot.tool * _ystep, p);
            _xprev += _xstep;
            _yprev = height - dot.tool * _ystep;
        }

        _xprev = 0;
        _yprev = height - points.get(points.size() - _spread).food * _ystep;;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.YELLOW);
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - (dot.food + FOOD_OFFSET) * _ystep, p);
            _xprev += _xstep;
            _yprev = height - (dot.food + FOOD_OFFSET) * _ystep;
        }

        _xprev = 0;
        _yprev = height - points.get(points.size() - _spread).labor * _ystep;;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.GREEN);
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - dot.labor * _ystep, p);
            _xprev += _xstep;
            _yprev = height - dot.labor * _ystep;
        }
        _xprev = 0;
        _yprev = height - points.get(points.size() - _spread).transport * _ystep;;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.rgb(255, 192, 203));
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - (dot.transport + TR_OFFSET) * _ystep, p);
            _xprev += _xstep;
            _yprev = height - (dot.transport + TR_OFFSET) * _ystep;
        }
        _xprev = 0;
        _yprev = height - points.get(points.size() - _spread).coal * _ystep;;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.RED);
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - (dot.coal + COAL_OFFSET) * _ystep, p);
            _xprev += _xstep;
            _yprev = height - (dot.coal + COAL_OFFSET) * _ystep;
        }
        _xprev = 0;
        _yprev = height - points.get(points.size() - _spread).iron * _ystep;;
        for (int i = virt_days.size() - _spread; i < virt_days.size(); i += resolution)
        {
            dot = virt_days.get(i);
            p.setColor(Color.rgb(255, 165, 0));
            c.drawLine(_xprev, _yprev, _xstep + _xprev, height - (dot.iron + IRON_OFFSET) * _ystep, p);
            _xprev += _xstep;
            _yprev = height - (dot.iron + IRON_OFFSET) * _ystep;
        }

        p.setTextSize(width / App.sim.access.config.TEXT_SIZE_DIV);
        p.setColor(Color.WHITE);
        p.setTypeface(Typeface.MONOSPACE);

        String spreadString;
        if(spread == 0)
            spreadString = "infinite";
        else
            spreadString = Integer.toString(spread);
        c.drawText("Spread: " + spreadString, 0, p.getTextSize(), p);
        //_graph.Save(path, ImageFormat.Png);
        return _graph;
    }

    private long GetMaximum(List<GraphData> virt_days, int spread)
    {
        long _max = 0;
        GraphData dot;
        for (int i = virt_days.size() - spread; i < virt_days.size(); i++)
        {
            dot = virt_days.get(i);
            if (dot.labor > _max)
                _max = dot.labor;
            if (dot.food + FOOD_OFFSET > _max)
                _max = dot.food + FOOD_OFFSET;
            if (dot.tool > _max)
                _max = dot.tool;
            if (dot.coal + COAL_OFFSET > _max)
                _max = dot.coal + COAL_OFFSET;
            if (dot.iron + IRON_OFFSET > _max)
                _max = dot.iron + IRON_OFFSET;
            if (dot.transport + TR_OFFSET > _max)
                _max = dot.transport + TR_OFFSET;
        }
        return _max;
    }

    public void deleteStateFile() {
        App.activity.deleteFile("henize.proto.graph");
    }

    public void saveState() {
        if(_thread != null && _thread.isAlive()) {
            _thread.interrupt();
            try {
                _thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(virt_days == null) return;
        try {
            deleteStateFile();
            FileOutputStream os = App.activity.openFileOutput("henize.proto.graph", Context.MODE_PRIVATE);
            ByteBuffer buff = ByteBuffer.allocate(virt_days.size()*48);
            for (GraphData p : virt_days) {
                buff.putLong(p.labor);
                buff.putLong(p.food);
                buff.putLong(p.tool);
                buff.putLong(p.coal);
                buff.putLong(p.iron);
                buff.putLong(p.transport);
            }
            os.write(buff.array());
            os.close();


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void initFromState() {
        _temp = new ArrayList<>();
        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(1);

                virt_days = new ArrayList<>();
                try {
                    FileInputStream is = App.activity.openFileInput("henize.proto.graph");
                    int size = is.available();
                    byte[] dat = new byte[size];
                    is.read(dat);
                    ByteBuffer buff = ByteBuffer.wrap(dat);
                    while(buff.hasRemaining() && !Thread.currentThread().isInterrupted()) {
                        virt_days.add(new GraphData(buff.getLong(), buff.getLong(), buff.getLong(), buff.getLong(), buff.getLong(), buff.getLong()));
                        loadProgress = ((float)size - (float)buff.remaining()) / (float)size * 100;
                    }
                    is.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                doneLoading = true;
            }
        });

        _thread.start();

    }
    public void addPoint(GraphData p) {
        try {
            if (_thread != null && _thread.isAlive()) {
                _temp.add(p);
            } else {
                virt_days.add(p);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePoint() {

        if(_thread != null && _thread.isAlive()) {
            _temp.remove(_temp.size() - 1);
        } else {
            virt_days.remove(virt_days.size() - 1);
        }
    }
}