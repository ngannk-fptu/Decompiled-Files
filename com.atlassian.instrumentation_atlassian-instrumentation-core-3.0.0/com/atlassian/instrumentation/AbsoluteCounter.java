/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Counter;

public interface AbsoluteCounter
extends Counter {
    @Override
    public long getValue();
}

