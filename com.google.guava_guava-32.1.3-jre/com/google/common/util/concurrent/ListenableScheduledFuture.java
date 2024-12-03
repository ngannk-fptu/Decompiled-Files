/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ScheduledFuture;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ListenableScheduledFuture<V>
extends ScheduledFuture<V>,
ListenableFuture<V> {
}

