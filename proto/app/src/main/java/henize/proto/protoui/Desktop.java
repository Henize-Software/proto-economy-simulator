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
import android.view.MotionEvent;

import java.util.HashMap;

/**
 * Created by ACR411 on 4/12/2020.
 */

public class Desktop {
    HashMap<String, Container> containers;
    Container selected;
    public Bitmap bitmap;
    public volatile float scale;
    public volatile  boolean updateScale;

    public Desktop() {
        containers = new HashMap<>();
    }

    public void setScale(float s) {
        this.scale = s;
        updateScale = false;
        for (Container c : containers.values()) {
            c.setScale(s);
        }
    }
    public void add(Container c, String id) {
        //c.isTopLevelWindowContainer_NOBOARDER = true;
        containers.put(id, c);
    }

    public void remove(String id) {
        containers.remove(id);
    }

    public void select(String id) {
        selected = containers.get(id);
        bitmap = selected.boarderBitmap;
    }

    public void render() {
        //update value from gui thread by updating fields
        if(updateScale) {
            setScale(scale);
            if(selected!= null) {
                bitmap = selected.boarderBitmap;
            }
        }
        if(selected != null) {
            selected.render();
        }
    }

    public void postScreenTouchEvent(MotionEvent event) {
        if(selected != null) {
            selected._onTouchEvent(event);
        }
    }
}
