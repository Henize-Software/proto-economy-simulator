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

import android.content.SharedPreferences;


/**
 * Created by ACR411 on 8/14/2018.
 */

public class StoredPreferences {
    SharedPreferences pref;

    int viewId;
    boolean unlocked;

    public StoredPreferences(SharedPreferences pref, int defViewId, boolean defUnlocked) {
        this.pref = pref;
        viewId = defViewId;
        unlocked = defUnlocked;
    }

    public int getView() {
        return pref.getInt("VIEW", viewId);

    }
    public StoredPreferences setView(int id) {
        pref.edit().putInt("VIEW", id).commit();
        return this;
    }
    public boolean getUnlocked() {
        pref.getBoolean("UNLOCKED", unlocked);
        return unlocked;
    }
    public StoredPreferences setUnlocked(boolean b) {
        pref.edit().putBoolean("UNLOCKED", b).commit();
        return this;
    }

}
