/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;

@Internal
public interface Counter {
    public Counter increase();

    public Counter increase(long var1);
}

