/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import java.util.concurrent.Callable;

public interface AsyncManager {
    public boolean hasAsyncActionResult();

    public Object getAsyncActionResult();

    public void invokeAsyncAction(Callable var1);
}

