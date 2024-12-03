/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;

public abstract class Located
implements Locatable {
    protected Location location;

    @Override
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location loc) {
        this.location = loc;
    }
}

