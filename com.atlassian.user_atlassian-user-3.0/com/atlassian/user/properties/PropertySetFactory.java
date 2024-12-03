/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.atlassian.user.properties;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.opensymphony.module.propertyset.PropertySet;

public interface PropertySetFactory {
    public PropertySet getPropertySet(Entity var1) throws EntityException;
}

