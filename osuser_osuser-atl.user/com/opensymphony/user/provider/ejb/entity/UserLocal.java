/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.module.propertyset.PropertySet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBLocalObject;

public interface UserLocal
extends EJBLocalObject {
    public Long getId();

    public String getName();

    public List getGroupNames();

    public void setGroups(Set var1);

    public Set getGroups();

    public void setPassword(String var1);

    public PropertySet getPropertySet();

    public boolean authenticate(String var1);

    public boolean inGroup(String var1);

    public boolean removeGroup(String var1);
}

