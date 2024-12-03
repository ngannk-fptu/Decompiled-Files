/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;

@Internal
public interface Ticker
extends AutoCloseable {
    public static final Ticker NO_OP = () -> {};

    @Override
    public void close();
}

