/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.MessageContext;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.Nullable;
import java.util.Map;

public interface DistributedPropertySet
extends PropertySet {
    @Nullable
    public <T extends PropertySet> T getSatellite(Class<T> var1);

    public Map<Class<? extends PropertySet>, PropertySet> getSatellites();

    public void addSatellite(PropertySet var1);

    public void addSatellite(Class<? extends PropertySet> var1, PropertySet var2);

    public void removeSatellite(PropertySet var1);

    public void copySatelliteInto(MessageContext var1);
}

