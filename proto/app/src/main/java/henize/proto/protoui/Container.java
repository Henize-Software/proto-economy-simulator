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
package henize.proto.protoui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

import henize.proto.App;
import henize.proto.R;

/**
 * Created by ACR411 on 4/12/2020.
 */

public class Container {
    public static final int boarder = 16;
    public Canvas canvas, boarderCanvas;
    public Bitmap bitmap, boarderBitmap;
    public Paint paint;
    public TextPaint textPaint;

    public int posX, posY;
    public int width, height;
    public int zOrder;

    public boolean noBoarder;
    public int scaleX;

    int listLast;

    public Container _super;
    //List<Container> containers;
    volatile Container[] containers;
    puiOnTouch onTouch;
    private int scaleY;
    private float prevTranslatedX;
    private float prevTranslatedY;
    private float prevY, prevX;
    private float scale;
    private int defaultX, defaultY;
    private float subScale;

    Container activelySelectedSub;
    public boolean selected;
    Bitmap icon;
    int count;

    public Container(int w, int h, int x, int y) {
        listLast = -1;
        containers = new Container[200];
        paint = new Paint();
        textPaint = new TextPaint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(48);
        setSize(w, h);
        setPosition(x, y);
        icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(App.activity.getResources(), R.drawable.window), 180, 180, false);

        //containers = new ArrayList<>();

    }
    public void setScale( float scale) {
        this.subScale = scale;
        for (Container c : containers) {
            if(c == null) continue;
            c.setScale(scale);
        }

        if(scale == 0.0f) {
            noBoarder = true;
            setToDefaultPosition();
        }else {
            noBoarder = false;
        }
        if(_super == null) {
            noBoarder = true;
            scale = 0.0f;
        }


        this.scale = scale;
        this.scaleX = (int) (width * scale);
        this.scaleY = (int) (height * scale);
        if(width + scale < 1 || height + scale < 1) return;


            //bitmap = Bitmap.createBitmap(width + scaleX, height + scaleY, Bitmap.Config.RGB_565);
            //canvas = new Canvas(bitmap);
            boarderBitmap = Bitmap.createBitmap(width + scaleX , height + scaleY , Bitmap.Config.RGB_565);
            boarderCanvas = new Canvas(boarderBitmap);




    }

    public void setSize(int w, int h) {
        if(w < 1) w = 1;
        if(h < 1) h = 1;
        //if(width != w || height != h){
            width = w;
            height = h;
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
           // boarderBitmap = Bitmap.createBitmap(w , h , Bitmap.Config.RGB_565);

           // boarderCanvas = new Canvas(boarderBitmap);

            canvas = new Canvas(bitmap);
            setScale(subScale);
       // }

    }

    public void setPosition(int x, int y) {
        posX = x ; posY = y;
    }

    void addContainer(final Container container, boolean isIcon) {
        listLast++;
        containers[listLast] = container;
        container.zOrder = listLast;
        container._super = this;
        container.setScale(this.subScale);
        if (!isIcon) {
            final Container _C = new Container(180, 180, ((count ) * 180) + 5, height - 180 - 5) {
                 @Override
                protected void draw() {
                    noBoarder = true;
                    this.setToDefaultPosition();
                    canvas.drawBitmap(icon, 0, 0, paint);
                }

                @Override
                protected void onTouchEvent(MotionEvent event) {
                    super.onTouchEvent(event);
                    try {
                        container.zTop();
                        zBottom();
                    }catch(Throwable e) {
                        _super.removeContainer(this);
                        _super.count--;
                        if(_super.count < 0) _super.count = 0;
                    }

                }
            };
            addContainer(_C, true);
            _C.setDefaultPosition(_C.posX, _C.posY);
            _C.zBottom();
            count++;
        }


    }




    public void addContainer(Container container) {
        addContainer(container, false);

    }

    protected void removeContainer(Container container) {
        //container.zBottom();
        containers[container.zOrder] = null;
        listLast--;
        if(listLast < 0) listLast = 0;

    }

    public void delete() {
        if(_super != null) {
            _super.removeContainer(this);
            _super = null;
        }
    }

    private boolean swap(int a, int dir) {
        if(a < 0 || a >= containers.length || containers[a] == null) return false;
        if(a + dir < 0 || a + dir >= containers.length) return false;
        Container t = containers[a];
        Container b = containers[a + dir];
        if(b != null) {
            int temp = t.zOrder;
            t.zOrder = b.zOrder;
            b.zOrder = temp;

        } else {
            t.zOrder += dir;
        }
        containers[a] = b;
        containers[a + dir] = t;
        return true;
    }
    protected boolean changeZ(int v, int d) {
          return swap(v, d);
    }

    public boolean zUp() {
        return _super.changeZ(zOrder, 1);
    }

    public boolean zDown() {
        return _super.changeZ(zOrder, -1);
    }

    public void zTop() {
        while(zUp());
    }
    public void zBottom() {
        while(zDown());
    }
    public void render() {
    if(_super == null) {
        canvas.drawColor(Color.BLACK);
        String text = "Tablet mode (Experimental)";
        float w = textPaint.measureText(text);
        float s = width - w;
        canvas.drawText(text, s, height - 5, textPaint);

    }

        for(int i = 0; i < containers.length; i++) {
            Container c = containers[i];
            if(c == null) continue;

            c.render();
            canvas.drawBitmap(c.boarderBitmap, c.posX, c.posY, null);
        }
        draw();
        width += scaleX;
        height += scaleY;
        paint.setColor(Color.rgb(0, 0, 128));
        boarderCanvas.drawRect(0, 0, width, height, paint);
        if (!noBoarder) {
            paint.setColor(Color.WHITE);
            boarderCanvas.drawRect(boarder, boarder * 3, width - boarder, height - boarder, paint);
            boarderCanvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, width - boarder * 2, height - boarder * 4, false), boarder, boarder * 3, paint);
        } else {
            if(scaleX != 0) {
                boarderCanvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false), 0, 0, paint);
            } else {
                boarderCanvas.drawBitmap(bitmap, 0, 0, paint);
            }
        }
        width -= scaleX;
        height -= scaleY;

    }

    protected void draw() {

    }

    public final void _onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if(event.getAction() == MotionEvent.ACTION_UP) {
            selected = false;
        }
        if(activelySelectedSub != null && activelySelectedSub.selected) {
            activelySelectedSub._onTouchEvent(event);
        } else {
            for (int i = containers.length - 1; i >= 0; i--) {
                Container c = containers[i];
                if (c == null) continue;
                if (x >= c.posX && x <= (c.posX + c.width + c.scaleX) &&
                        y >= c.posY && y <= (c.posY + c.height + c.scaleY)) {
                    c.selected = true;
                    activelySelectedSub = c;
                    c.zTop();
                    c._onTouchEvent(event);
                    return;
                }
            }
        }
/*        for(Container c : containers) {
            if(x >= c.posX && x <= (c.posX + c.width) &&
                    y >= c.posY && y <= (c.posY + c.height) ) {
                c._onTouchEvent(event);
                //c.onTouch.onTouchEvent(event);
                return;
            }
        }*/

        ScaleTranslator scaleTranslator = new ScaleTranslator(x, y, false).invoke();
        float translatedX = scaleTranslator.getTranslatedX();
        float translatedY = scaleTranslator.getTranslatedY();


        float boffset = noBoarder ? 0 : Math.abs(boarder *  scale ) + boarder;
        if(translatedX > boffset && translatedX < width - boffset &&
            translatedY > boffset * 3 && translatedY < height - boffset) {
            scaleTranslator = new ScaleTranslator(x, y, true).invoke();
            float _translatedX = scaleTranslator.getTranslatedX();
            float _translatedY = scaleTranslator.getTranslatedY();
            if(_translatedX < 0 || _translatedY < 0) {
                handleWindowFrameAction(event, x, y, translatedX, translatedY);
                return;
            }
     //       App.activity.simulator.overlayText = Float.toString(_translatedX) + " " + Float.toString(_translatedY) + "\n" + Float.toString(x) + " " + Float.toString(y);

            event.setLocation((int)_translatedX, (int)_translatedY);
            onTouchEvent(event);
            if(onTouch != null)
                onTouch.onTouchEvent(event);
        } else {
           // if(isTopLevelWindowContainer_NOBOARDER) return;
            //event.setLocation((int)translatedX, (int)translatedY);
            //window frame interaction

            handleWindowFrameAction(event, x, y, translatedX, translatedY);
        }



    }

    protected void handleWindowFrameAction(MotionEvent event, int x, int y, float translatedX, float translatedY) {
        if(noBoarder) return;
        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                prevTranslatedX = translatedX;
                prevTranslatedY = translatedY;
                prevX = x;
                prevY = y;
               // posY += (int) (y - prevY) ;
                break;
            }
            case MotionEvent.ACTION_MOVE : {
                posX += (int) (x - prevX) ;
                posY += (int) (y - prevY);
                prevTranslatedX = translatedX;
                prevTranslatedY = translatedY;
                prevX = x;
                prevY = y;
                break;
            }
        }
    }

    protected void onTouchEvent(MotionEvent event) {

    }

    public void setOnTouch(puiOnTouch onTouch) {
        this.onTouch = onTouch;
    }

    public void setDefaultPosition(int x, int y) {
        defaultX = x;
        defaultY = y;
    }
    public void setToDefaultPosition() {
        setPosition(defaultX, defaultY);
        for(Container c : containers){
            if(c == null) continue;
            c.setToDefaultPosition();
        }
    }

    private class ScaleTranslator {
        private int x;
        private int y;
        private boolean inside;
        private float translatedX;
        private float translatedY;

        public ScaleTranslator(int x, int y, boolean inside) {
            this.x = x;
            this.y = y;
            this.inside = inside;
        }

        public float getTranslatedX() {
            return translatedX;
        }

        public float getTranslatedY() {
            return translatedY;
        }

        public ScaleTranslator invoke() {
            int _scaleX = scaleX;
            int _scaleY = scaleY;
            float _posX = (float)posX;
            float _posY = (float)posY;
             //_posX /= ((float)width / (width + _scaleX));
             //_posY /= ((float)height / (height + _scaleY));
            if(inside && !noBoarder) {
                _scaleX -= boarder * 2;
                _scaleY -= boarder * 4;
                _posX += boarder;
                _posY += boarder * 3;
            }

            float o = (float)width  / (width + _scaleX);
            translatedX = ((float)x - _posX) * o;
            o = (float)height / (height + _scaleY);
            translatedY = (y - _posY) * o;



            return this;
        }
    }
}
