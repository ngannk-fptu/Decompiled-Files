/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.spi.Message;

interface ErrorHandler {
    public void handle(Object var1, Errors var2);

    public void handle(Message var1);
}

