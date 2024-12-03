/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api;

import com.oracle.webservices.api.message.BaseDistributedPropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.PropertySet;

public abstract class DistributedPropertySet
extends BaseDistributedPropertySet {
    public void addSatellite(@NotNull PropertySet satellite) {
        super.addSatellite(satellite);
    }

    public void addSatellite(@NotNull Class keyClass, @NotNull PropertySet satellite) {
        super.addSatellite(keyClass, satellite);
    }

    public void copySatelliteInto(@NotNull DistributedPropertySet r) {
        super.copySatelliteInto(r);
    }

    public void removeSatellite(PropertySet satellite) {
        super.removeSatellite(satellite);
    }
}

