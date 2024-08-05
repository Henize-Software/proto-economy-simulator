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

//package henize.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Commodity {
    //                    type    inputs (type   (multiplier (type factor)))
    public static HashMap<String, 
                          HashMap<String, /*inputs*/
         /*multiplier  */ Map.Entry<String, Integer>>> types = new HashMap<>();
    
    private static int mult; //calculated multiplier

    public static List<Commodity> generate(String type, List<Commodity> inputs) {
        var requiredInputs = types.get(type);
        
        var v = inputs.stream()
            .map(c -> c.type)
            .filter(requiredInputs::containsKey)
            .collect(Collectors.toList());
        if(v.size() != requiredInputs.size() ||
           v.size() != inputs.size()) {
            //check for multipliers.
            var remaining = inputs.stream()
                .map(c -> c.type) 
                .filter(t -> !v.contains(t))
                .collect(Collectors.toList());
 
            for (String t : remaining) {
                var multiplier = requiredInputs.values().stream()
                                 .filter(entry -> entry.getKey().equals(t))
                                 .map(Map.Entry::getValue) 
                                 .findFirst();
                mult += multiplier.orElseThrow();
            }
           }
        var commodities = new ArrayList<Commodity>();
        while(mult-- > 0) {
            commodities.add(new Commodity(type));
        }
        return commodities;
    }
    
    public String type; //food, tool, ect...
    

    private Commodity(String type) {
        this.type = type;
    }

    private void use() {

    }
}
