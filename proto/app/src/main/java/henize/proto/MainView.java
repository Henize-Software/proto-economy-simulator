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

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowInsets;

import henize.proto.lib.map.Traveler;
import henize.proto.lib.traders.Trader;
import henize.proto.pathfinder.Coordinate;
import henize.proto.protoui.Container;
import henize.proto.protoui.Desktop;
import henize.proto.protoui.puiOnTouch;


/**
 * Created by ACR411 on 12/17/2017.
 */

public class MainView extends SurfaceView implements Runnable {

    public Thread thread;
    boolean ok;
    volatile boolean running;
    long lastCall;

    SurfaceHolder holder;
    Canvas primaryCanvas, mapCanvas;
    Paint paint;
    TextPaint overlayTextPaint;

   public  String overlayText;

    int offsetX, offsetY, _offsetX, _offsetY;


    Bitmap bmpMap, bmpGraph, bmpStats;
    Bitmap ground, house, food, tool, coal, iron, shipping, master, car, truck, tree;
    Matrix matrix;

    int screenWidth, height;
    public int graphSpread = 0;
    public int scale;
    public int speed = -1;

    public static final int MAP = 1;
    public static final int GRAPH = 2;
    public static final int STATS = 3;
    public int viewMode;

    public volatile boolean simPaused;
    private volatile int inc;

    Container cmap, ctextStats, ctextStats2,cmain, cgraph, cadvstats, cmaingraph, cmainadvstats;
    Desktop puiDesktop;
    private int previousView = -1;
    float prevX, prevY;
    private boolean isScroling;
    private TextPaint smallTextPaint;

    public MainView(Context c) {
        super(c);
        lastCall = 0;

        holder = getHolder();
        paint = new Paint();
        matrix = new Matrix();
        overlayTextPaint = new TextPaint();
        smallTextPaint = new TextPaint();
        overlayTextPaint.setColor(Color.YELLOW);
        overlayTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        house = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.house), Color.WHITE);
        Icons.traderIcons.put("labor", house);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        tool = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.tool), Color.WHITE);
        Icons.traderIcons.put("tool", tool);
        coal = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.coal), Color.WHITE);
        Icons.traderIcons.put("coal", coal);
        iron = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.iron), Color.WHITE);
        Icons.traderIcons.put("iron", iron);
        shipping = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.shipping), Color.WHITE);
        Icons.traderIcons.put("shipping", shipping);
        master = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.master), Color.WHITE);
        Icons.traderIcons.put("master", master);
        food = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.food), Color.WHITE);
        Icons.traderIcons.put("food", food);
        car = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.car), Color.WHITE);
        truck = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.truck), Color.WHITE);
        tree = createTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.tree), Color.WHITE);

        puiDesktop = new Desktop();
        cmain = new Container(1, 1, 0, 0);
        cmap = new Container(1, 1, 0, 0);
        cgraph = new Container(1, 1, 0, 0);
        cadvstats = new Container(1, 1, 0, 0);
        ctextStats = new Container(1, 1, 0, 0);
        ctextStats2 = new Container(1, 1, 0, 0);
        cmaingraph = new Container(1, 1, 0, 0);
        cmainadvstats = new Container(1, 1, 0, 0);
        cmain.addContainer(cmap);
        cmain.addContainer(ctextStats);
        cmaingraph.addContainer(cgraph);
        cmaingraph.addContainer(ctextStats2);
        cmainadvstats.addContainer(cadvstats);
        puiDesktop.add(cmain, "map");
        puiDesktop.add(cmaingraph, "graph");
        puiDesktop.add(cmainadvstats, "advStats");
        puiDesktop.select("map");

        cmap.setOnTouch(new puiOnTouch() {
            @Override
            public void onTouchEvent(final MotionEvent event) {
             //   event.setLocation(event.getX(), event.getY() - App.activity.getSupportActionBar().getHeight());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE: {

                        int px = (int) event.getX() - (int) prevX;
                        int py = (int) event.getY() - (int) prevY;

                        incrementOffsets(px, py);

                        prevX = event.getX();
                        prevY = event.getY();
                        if(px > 5 || py > 5)
                            isScroling = true;
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        prevX = event.getX();
                        prevY = event.getY();
                        isScroling = false;

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!isScroling) {
                            final int x = (int) (prevX - offsetX) / scale;
                            final int y = (int) (prevY - offsetY) / scale;
                            Coordinate c = App.sim.map.getTranslatedAddress(x, y);
                            Trader te = App.sim.access.dir.getAtaAddress(c.x, c.y);
                            if(te == null) {
                                for (byte yy = -1; yy <= 1; yy++){
                                    {
                                        for(byte xx = -1; xx <= 1; xx++) {
                                            te = App.sim.access.dir.getAtaAddress(xx + c.x, yy + c.y);
                                            if(te != null) break;
                                        }
                                        if(te != null) break;
                                    }}
                                if(te == null) {
                                    return;
                                }
                            }
                            final Trader t = te;

                            Container container = new Container(1, 1,10, 10 ) {

                                @Override
                                public void draw() {

                                    String text = getStringsForPopUp(t);
                                    StaticLayout textLayout = new StaticLayout(text, smallTextPaint, (int)(screenWidth / 1.5), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);                                    this.setSize(textLayout.getWidth(), textLayout.getHeight());
                                    setSize(textLayout.getWidth(), textLayout.getHeight() );
                                    if(noBoarder) {
                                        canvas.drawColor(Color.rgb(0, 0, 128));
                                    } else {
                                        canvas.drawColor(Color.BLACK);
                                    }
                                    canvas.save();
                                    textLayout.draw(canvas);
                                    canvas.restore();
/*                                    smallTextPaint.setColor(Color.RED);
                                    text = "\n\n\n\n\n\nEXIT";

                                    textLayout = new StaticLayout(text, smallTextPaint, 400, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                    canvas.save();

                                    textLayout.draw(canvas);
                                    canvas.restore();*/


                                }

                                @Override
                                protected void onTouchEvent(MotionEvent event) {
                                    if(event.getAction() == MotionEvent.ACTION_UP) {
                                        delete();
                                        App.vibrate();
                                    }
                                }
                            };

                            cmain.addContainer(container);
                            container.zTop();
                            App.vibrate();

                        }
                        break;

                    }
                }

            }

            @NonNull
            protected String getStringsForPopUp(Trader t) {

                return t.id + " <-Trader I.D.\n" + "Price:    $" + App.sim.CtD(t.getPrice()) + "\n" +
                        "Acct bal: $" + App.sim.CtD(t.account.getBalance()) + "\n" +
                        "Debt:     $" + App.sim.CtD(t.account.debt) + "\n" +
                        "Units for sale: " + Integer.toString(t.com.size()) + "\n" +
                        "Units sold: " + Long.toString(t.sale_count) + "\n" + (t.activeTrade == null ? "\n" :
                        "==============\nLast trade attempt by: " + t.activeTrade.id + " Ordered [" + Integer.toString(t.getQuantityOfLastTransaction()) + "] " + t.traderType) + "\n==============\nTap window to close.";

            }
        });
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        height = h;
        scale = w / 20;
        App.scale = scale;
        //scale = w / App.sim.access.config.MAP_SIZE;
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(scale / 3);
        overlayTextPaint.setTextSize(screenWidth / 15);
        smallTextPaint.setTypeface(Typeface.MONOSPACE);
        smallTextPaint.setColor(Color.WHITE);
        smallTextPaint.setTextSize(screenWidth / 25);
        if(speed == -1) {
            App.sim.setScale(scale);
        }

        ///FULL SCREEN
        App.sim.map.setFrameSize(20, h / scale);
        ///////////

        App.sim.map.updateAll();
       // if (bmpMap == null) { ///////////// h full screen
            bmpMap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            //mapCanvas = new Canvas(bmpMap);
            house = Bitmap.createScaledBitmap(house, scale, scale, false);
            ground = Bitmap.createScaledBitmap(ground, scale, scale, false);
            tool = Bitmap.createScaledBitmap(tool, scale, scale, false);
            coal = Bitmap.createScaledBitmap(coal, scale, scale, false);
            iron = Bitmap.createScaledBitmap(iron, scale, scale, false);
            shipping = Bitmap.createScaledBitmap(shipping, scale, scale, false);
            master = Bitmap.createScaledBitmap(master, scale, scale, false);
            food = Bitmap.createScaledBitmap(food, scale, scale, false);
            car = Bitmap.createScaledBitmap(car, scale, scale, false);
            truck = Bitmap.createScaledBitmap(truck, scale, scale, false);
            tree = Bitmap.createScaledBitmap(tree, scale, scale, false);
      //  }

        cmain.setSize(screenWidth, height);
        cmaingraph.setSize(screenWidth, height);
        cmainadvstats.setSize(screenWidth, height);
        cmap.setSize(screenWidth, height);
        cgraph.setSize(screenWidth, height);
        cadvstats.setSize(screenWidth, height);
        ctextStats.setSize(screenWidth, height - screenWidth);
        ctextStats2.setSize(screenWidth, height - screenWidth);
        cmap.setDefaultPosition( 0, 0);
        cmap.setToDefaultPosition();
        ctextStats.setDefaultPosition(0, screenWidth);
        ctextStats.setToDefaultPosition();
        ctextStats2.setDefaultPosition(0, screenWidth);
        ctextStats2.setToDefaultPosition();
        mapCanvas = cmap.canvas;
        bmpGraph = cgraph.bitmap;
        bmpStats = cadvstats.bitmap;
        selectViewMode();
        ctextStats.zTop();
        ctextStats2.zTop();

    }

    public void start() {
        running = true;
        thread = new Thread(this, "Main loop for simulator and renderer");
        thread.start();

    }

    public void stop() {
        running = false;
        _offsetX = 0; _offsetY = 0;
    }
    public void incrementOffsets(int x, int y) {

/*        int fsX = App.sim.map.frameStartX;
        int fsY = App.sim.map.frameStartY;
        int fw = App.sim.map.frameWidth;
        int fh = App.sim.map.frameHeight;
        int mw = App.sim.map.width;
        int mh = App.sim.map.height;*/

        //if (_offsetX + x > _offsetX && fsX != 0 || _offsetX < 0)
            _offsetX += x;
        //if (_offsetX + x < _offsetX && fsX < mw - fw || _offsetX > 0)
        //    _offsetX += x;
       // if (_offsetY + y > _offsetY && fsY != 0 || _offsetY < 0)
            _offsetY += y;
       // if (_offsetY + y < _offsetY && fsY < mh - fh || _offsetY > 0)
       //     _offsetY += y;

/*        if(fsX == 0 && _offsetX > 0)
            _offsetX = 0;
        if(fsX == mw - fw && _offsetX < 0)
            _offsetX = 0;
        if(fsY == 0 && _offsetY > 0)
            _offsetY = 0;
        if(fsY == mh - fh && _offsetY < 0)
            _offsetY = 0;*/


    }
    @Override
    public synchronized void run() {
        Thread.currentThread().setPriority(9);

        inc++;

        while (running) {
           synchronized (this) {
               if (!simPaused) {
                   try {
                       if(previousView != viewMode) {
                           selectViewMode();
                           previousView = viewMode;
                       }
                       App.sim.Next();
                       App.sim.map.Tick();
                   } catch (NullPointerException e) {
                       ok = false;
                   } catch (Exception e) {
                     //  App.initSim();
                       ok = false;
                   }
               } else {
                   //synchronized (this) {
                   App.sim.map.Tick(false);
                   //}
               }
               try {
                  // synchronized (this) {
                   updateScreen();
                   ok = true;
                  //}
               } catch (Exception e) {
                   ok = false;
               }
           }

        }
        inc--;
        if(inc > 0)
            throw new RuntimeException();
        App.sim.interrupt();
    }

    protected void selectViewMode() {
        if(viewMode == MAP) {
            puiDesktop.select("map");
        } else if(viewMode == GRAPH) {
            puiDesktop.select("graph");
        } else if(viewMode == STATS) {
            puiDesktop.select("advStats");
        }
    }

    private void updateScreen() {
        if (scale == 0) return;

        processOffsets();

        long currTime = System.currentTimeMillis();
        if(App.sim.access.config.warp && currTime - lastCall < 500) return;
        lastCall = currTime;
        if (viewMode == GRAPH) {
            adjustTextStatsForTabMode(ctextStats2);
            /*cgraph.bitmap =*/ App.sim.graph.DrawGraph(screenWidth, screenWidth, graphSpread, cgraph.canvas);
            /*ctextStats2.bitmap = */ App.sim.textStats.draw(ctextStats2.width, ctextStats2.height, ctextStats2.canvas);
            if(!App.activity.tabMode) ctextStats2.zTop();
        }
        else if (viewMode == MAP) {
            if(!App.activity.tabMode) {
                if (App.sim.access.config.FULL_SCREEN) {
                    ctextStats.zDown();
                } else {
                    ctextStats.zTop();
                }
            }
            int __x = App.sim.map.frameStartX + App.sim.map.frameWidth;
            int __y = App.sim.map.frameStartY + App.sim.map.frameHeight;
            for (int _y = App.sim.map.frameStartY - 1; _y < __y + 1 ; _y++) {
                for (int _x = App.sim.map.frameStartX - 1; _x < __x + 1; _x++) {
                    renderTile(scale, _y, _x);
                }
            }

            renderTravelers(scale);
        }

        if(viewMode == STATS) {
            /*cadvstats.bitmap = */ App.sim.advTextStats.draw(screenWidth, height, cadvstats.canvas );
        } else {
            //bmpStats = App.sim.textStats.draw(width, height - width);

            adjustTextStatsForTabMode(ctextStats);
            /*ctextStats.bitmap =*/ App.sim.textStats.draw(ctextStats.width, ctextStats.height, ctextStats.canvas);
        }

        puiDesktop.render();
        renderScreen(getTextOverlay());
    }

    private void adjustTextStatsForTabMode(Container c) {
        if(!c.noBoarder) {
            if (c.height != c.width / 2) {
                c.setSize(c.width, c.width / 2);
            }

        } else {
            if (c.height != height - screenWidth) {
                c.setSize(screenWidth, height - screenWidth);
            }
        }
    }

    private void processOffsets() {
        int fsX = App.sim.map.frameStartX;
        int fsY = App.sim.map.frameStartY;
        int fw = App.sim.map.frameWidth;
        int fh = App.sim.map.frameHeight;
        int mw = App.sim.map.width;
        int mh = App.sim.map.height;

        if(_offsetX != offsetX || _offsetY != offsetY) {
            offsetX = _offsetX;
            offsetY = _offsetY;
            App.sim.map.updateAll();

            if (Math.abs(offsetX) > scale || Math.abs(offsetY) > scale) {
                int _x = (offsetX) / scale;
                int _y = (offsetY) / scale;
                {
                    offsetX = (offsetX) % scale;// *-1;
                    offsetY = (offsetY) % scale;// *-1;
                }
                App.sim.map.moveFrame(_x * -1, _y * -1);
                mapCanvas.drawColor(Color.BLACK);
            }
            _offsetX = offsetX;
            _offsetY = offsetY;
        }

        if(fsX == 0 && offsetX > 0)
            offsetX = 0;
        if(fsX == mw - fw && offsetX < 0)
            offsetX = 0;
        if(fsY == 0 && offsetY > 0)
            offsetY = 0;
        if(fsY == mh - fh && offsetY < 0)
            offsetY = 0;
    }

    private String getTextOverlay() {
        if(overlayText == null) overlayText = "";
        String message = overlayText + "\n";
        try {
            if (App.sim.access.config.fresh) {
                message += "\nPRESS PLAY TO START\n\nTAP UNIT ICONS FOR INFORMATION";
            } else if (App.sim.access.config.computing) {
                message += getResources().getString(R.string.computing);
            }
/*            if(App.sim.wait_for_t) {
                message += "TRANSPORT SERVICE OVERLOAD!";
            }*/
        }catch (Exception e) {
            ok = false;
        }
        if(!ok) {
            message += "\nEXPERIENCING UNKNOWN MALFUNCTION\nRESET IF PROBLEM PERSISTS\n";
        }
        return message;
    }

    private void renderTravelers(int pxSize) {
        for (Traveler t : App.sim.map.traveler_list) {
            Bitmap temp = null;
            if (App.sim.map.insideFrame(t.loc.x, t.loc.y)) {
                matrix.setRotate(t.rotation);

                switch (t.tr_color) {
                    case 8: {
                        temp = Bitmap.createBitmap(car, 0, 0, pxSize, pxSize, matrix, false);
                        paint.setColor(Color.GREEN);
                        break;
                    }
                    case 9: {
                        temp = Bitmap.createBitmap(truck, 0, 0, pxSize, pxSize, matrix, false);
                        paint.setColor(Color.rgb(255, 0, 255));
                        break;
                    }
                }
                int x = App.sim.map.translateX(t.loc.x);
                int y = App.sim.map.translateY(t.loc.y);
                mapCanvas.drawBitmap(temp, (x * pxSize) + t.currXOffset + offsetX, (y * pxSize) + t.currYOffset + offsetY, null);
                //mapCanvas.drawPoint((t.loc.x * pxSize) + (pxSize / 2) + t.currXOffset, (t.loc.y * pxSize) + (pxSize / 2) + t.currYOffset, paint);
            }
        }
    }

    private void renderTile(int pxSize, int _y, int _x) {
        if(App.sim.map.isOutOfBounds(_x, _y))
            return;

        int x = App.sim.map.translateX(_x);
        int y = App.sim.map.translateY(_y);
        if (App.sim.map.update[_x][_y]) {
            int __x = x * pxSize + offsetX;
            int __y = y * pxSize + offsetY;
            switch (App.sim.map.getAtAddress(_x, _y)) {
                case 0: {
                    mapCanvas.drawBitmap(ground, __x , __y, null);
                    break;
                }
                case 1: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(house, __x , __y, null);
                    break;
                }
                case 2: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(food, __x , __y, null);
                    break;
                }
                case 3: {
                    mapCanvas.drawBitmap(ground, __x , __y, null);
                    mapCanvas.drawBitmap(tool, __x , __y, null);
                    break;
                }
                case 4: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(coal, __x , __y, null);
                    break;
                }
                case 5: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(iron, __x, __y, null);
                    break;
                }
                case 6: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(shipping, __x, __y, null);
                    break;
                }
                case 7: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(master, __x, __y, null);
                    break;
                }
                case 8: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    break;
                }
                case 9: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    break;
                }
                case 11: {
                    mapCanvas.drawBitmap(ground, __x, __y, null);
                    mapCanvas.drawBitmap(tree, __x, __y, null);
                    break;
                }


            }
            App.sim.map.update[_x][_y] = false;
        }
    }

    private void renderScreen(String textOverlay) {
            if(Thread.interrupted()) {
                return;
            }
            Surface surface = holder.getSurface();
            if (surface.isValid()) {
                try {
                    primaryCanvas = holder.lockCanvas();

                    if (primaryCanvas != null) {
                        try {
                            if (viewMode == MAP) {
                                primaryCanvas.drawBitmap(puiDesktop.bitmap, 0, 0, null);
/*                                primaryCanvas.drawBitmap(bmpMap, 0, 0, null);
                                if(!App.sim.access.config.FULL_SCREEN) {
                                    primaryCanvas.drawBitmap(bmpStats, 0, width, null);
                                }*/
                            } else if (viewMode == GRAPH) {
                                primaryCanvas.drawBitmap(puiDesktop.bitmap, 0, 0, null);
                                //primaryCanvas.drawBitmap(bmpGraph, 0, 0, null);
                                //primaryCanvas.drawBitmap(bmpStats, 0, width, null);
                            } else if (viewMode == STATS) {
                                primaryCanvas.drawBitmap(puiDesktop.bitmap, 0, 0, null);
                                //primaryCanvas.drawBitmap(bmpStats, 0, 0, null);
                            }
                        } finally {
                            if (textOverlay != null) {
                                StaticLayout textLayout = new StaticLayout(textOverlay, overlayTextPaint, screenWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                                primaryCanvas.save();
                                textLayout.draw(primaryCanvas);
                                primaryCanvas.restore();
                            }
                        }
                    }

                } finally {
                    holder.unlockCanvasAndPost(primaryCanvas);
                }
            }

    }

    public static Bitmap createTransparency(Bitmap bitmap,
                                            int replaceThisColor) {
        if (bitmap != null) {
            int picw = bitmap.getWidth();
            int pich = bitmap.getHeight();
            int[] pix = new int[picw * pich];
            bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);

            for (int y = 0; y < pich; y++) {
                for (int x = 0; x < picw; x++) {
                    int index = y * picw + x;
                    if (pix[index] == replaceThisColor) {
                        pix[index] = Color.TRANSPARENT;
                    }
                }
            }

            Bitmap bm = Bitmap.createBitmap(pix, picw, pich,
                    Bitmap.Config.ARGB_4444);

            return bm;
        }
        return null;
    }



}


