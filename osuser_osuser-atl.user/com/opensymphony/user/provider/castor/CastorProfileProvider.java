/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.opensymphony.user.provider.castor;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.castor.CastorBaseProvider;
import java.util.List;

public class CastorProfileProvider
extends CastorBaseProvider
implements ProfileProvider {
    public PropertySet getPropertySet(String name) {
        return null;
    }

    public boolean create(String name) {
        return false;
    }

    public boolean handles(String name) {
        return false;
    }

    public List list() {
        return null;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        return false;
    }

    public boolean remove(String name) {
        return false;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return false;
    }
}

