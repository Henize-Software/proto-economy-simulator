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
package henize.proto.lib.traders;

/**
 * Created by ACR411 on 12/13/2017.
 */

public enum ComType {
    LABOR, FOOD, TOOL, COAL, IRON, TRANSPORTATION;
    public static ComType fromInt(int i){
        switch (i){
            case 0: return LABOR;
            case 1: return FOOD;
            case 2: return TOOL;
            case 3: return COAL;
            case 4: return IRON;
            case 5: return TRANSPORTATION;
        }
        return null;
    }
}
