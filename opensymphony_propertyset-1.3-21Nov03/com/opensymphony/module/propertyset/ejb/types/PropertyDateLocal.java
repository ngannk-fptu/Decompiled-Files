/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.module.propertyset.ejb.types;

import java.sql.Timestamp;
import javax.ejb.EJBLocalObject;

public interface PropertyDateLocal
extends EJBLocalObject {
    public Long getId();

    public void setValue(int var1, Timestamp var2);

    public Timestamp getValue(int var1);
}

