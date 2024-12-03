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

public interface GroupLocal
extends EJBLocalObject {
    public Long getId();

    public String getName();

    public PropertySet getPropertySet();

    public void setUsers(Set var1);

    public Set getUsers();

    public List getUserNames();
}

