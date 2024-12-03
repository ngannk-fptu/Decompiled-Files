/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

public interface ActionEventListener {
    public Object prepare(Object var1, ValueStack var2);

    public String handleException(Throwable var1, ValueStack var2);
}

