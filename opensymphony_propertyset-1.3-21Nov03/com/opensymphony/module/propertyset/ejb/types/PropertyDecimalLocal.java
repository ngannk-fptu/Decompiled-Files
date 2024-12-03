/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.module.propertyset.ejb.types;

import javax.ejb.EJBLocalObject;

public interface PropertyDecimalLocal
extends EJBLocalObject {
    public Long getId();

    public void setValue(int var1, Double var2);

    public Double getValue(int var1);
}

