/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ModelDriven;

public interface ScopedModelDriven<T>
extends ModelDriven<T> {
    public void setModel(T var1);

    public void setScopeKey(String var1);

    public String getScopeKey();
}

