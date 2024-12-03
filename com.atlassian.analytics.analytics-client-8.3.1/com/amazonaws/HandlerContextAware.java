/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.handlers.HandlerContextKey;

public interface HandlerContextAware {
    public <X> void addHandlerContext(HandlerContextKey<X> var1, X var2);

    public <X> X getHandlerContext(HandlerContextKey<X> var1);
}

