/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.PropertyException;

public interface PropertyHandler {
    public Object processGet(int var1, Object var2) throws PropertyException;

    public Object processSet(int var1, Object var2) throws PropertyException;
}

