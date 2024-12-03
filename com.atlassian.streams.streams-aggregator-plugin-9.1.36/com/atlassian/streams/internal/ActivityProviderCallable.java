/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.internal.ActivityProvider;
import java.util.concurrent.Callable;

public interface ActivityProviderCallable<V>
extends Callable<V> {
    public ActivityProvider getActivityProvider();
}

