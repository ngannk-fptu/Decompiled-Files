/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.opensymphony.user.provider;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.provider.UserProvider;

public interface ProfileProvider
extends UserProvider {
    public PropertySet getPropertySet(String var1);
}

