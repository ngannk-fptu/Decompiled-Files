/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations.registry;

import com.atlassian.instrumentation.operations.OpSnapshot;

public interface OpFinderFilter {
    public boolean filter(OpSnapshot var1);
}

