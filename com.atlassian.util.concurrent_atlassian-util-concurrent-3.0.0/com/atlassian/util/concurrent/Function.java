/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface Function<D, R> {
    public R get(D var1);
}

