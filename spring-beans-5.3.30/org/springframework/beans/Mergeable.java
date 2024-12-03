/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import org.springframework.lang.Nullable;

public interface Mergeable {
    public boolean isMergeEnabled();

    public Object merge(@Nullable Object var1);
}

