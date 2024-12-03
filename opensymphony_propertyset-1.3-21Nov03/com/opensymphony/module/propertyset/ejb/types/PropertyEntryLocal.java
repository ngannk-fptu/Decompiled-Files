/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.module.propertyset.ejb.types;

import java.io.Serializable;
import javax.ejb.EJBLocalObject;

public interface PropertyEntryLocal
extends EJBLocalObject {
    public long getEntityId();

    public String getEntityName();

    public Long getId();

    public String getKey();

    public int getType();

    public void setValue(Serializable var1);

    public Serializable getValue();
}

