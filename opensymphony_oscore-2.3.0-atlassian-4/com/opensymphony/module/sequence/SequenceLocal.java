/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.opensymphony.module.sequence;

import javax.ejb.EJBLocalObject;

public interface SequenceLocal
extends EJBLocalObject {
    public long getActualCount();

    public long getCount(int var1);

    public String getName();
}

