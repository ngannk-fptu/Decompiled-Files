/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Awaitable;

public interface ReusableLatch
extends Awaitable {
    public void release();
}

