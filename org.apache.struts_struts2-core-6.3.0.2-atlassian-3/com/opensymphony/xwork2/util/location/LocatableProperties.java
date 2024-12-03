/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.PropertiesReader;
import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LocatableProperties
extends Properties
implements Locatable {
    Location location;
    Map<String, Location> propLocations;

    public LocatableProperties() {
        this(Location.UNKNOWN);
    }

    public LocatableProperties(Location loc) {
        this.location = loc;
        this.propLocations = new HashMap<String, Location>();
    }

    @Override
    public void load(InputStream in) throws IOException {
        try (PropertiesReader pr = new PropertiesReader(new InputStreamReader(in));){
            while (pr.nextProperty()) {
                String name = pr.getPropertyName();
                String val = pr.getPropertyValue();
                int line = pr.getLineNumber();
                String desc = this.convertCommentsToString(pr.getCommentLines());
                LocationImpl loc = new LocationImpl(desc, this.location.getURI(), line, 0);
                this.setProperty(name, val, loc);
            }
        }
    }

    String convertCommentsToString(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    public Object setProperty(String key, String value, Object locationObj) {
        Object obj = super.setProperty(key, value);
        if (this.location != null) {
            Location loc = LocationUtils.getLocation(locationObj);
            this.propLocations.put(key, loc);
        }
        return obj;
    }

    public Location getPropertyLocation(String key) {
        Location loc = this.propLocations.get(key);
        if (loc != null) {
            return loc;
        }
        return Location.UNKNOWN;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}

