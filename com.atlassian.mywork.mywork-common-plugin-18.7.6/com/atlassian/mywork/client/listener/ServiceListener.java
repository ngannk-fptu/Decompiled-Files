/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.mywork.client.listener;

import com.google.common.base.Function;
import java.io.Closeable;

public interface ServiceListener {
    public <T> Closeable addListener(Class<T> var1, Function<T, Void> var2);
}

