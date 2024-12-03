/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.spi.CancellableTask
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.internal.ActivityProviderCallable;
import com.atlassian.streams.spi.CancellableTask;

public interface ActivityProviderCancellableTask<V>
extends ActivityProviderCallable<V>,
CancellableTask<V> {
}

