/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.module.propertyset.ejb.types;

import java.io.Serializable;
import javax.ejb.EJBLocalObject;

public interface PropertyNumberLocal
extends EJBLocalObject {
    public Long getId();

    public void setValue(int var1, Serializable var2);

    public Serializable getValue(int var1);
}

