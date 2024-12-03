/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import javax.annotation.Nonnull;

public interface Component {
    @Nonnull
    public String getId();

    @Nonnull
    public String getName();
}

