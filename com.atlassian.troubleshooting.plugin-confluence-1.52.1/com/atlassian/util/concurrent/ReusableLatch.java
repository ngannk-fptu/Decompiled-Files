/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Awaitable;

public interface ReusableLatch
extends Awaitable {
    public void release();
}

