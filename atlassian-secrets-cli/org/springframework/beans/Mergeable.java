/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.lang.Nullable;

public interface Mergeable {
    public boolean isMergeEnabled();

    public Object merge(@Nullable Object var1);
}

