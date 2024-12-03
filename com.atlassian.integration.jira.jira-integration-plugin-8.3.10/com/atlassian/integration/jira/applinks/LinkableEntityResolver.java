/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.integration.jira.applinks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface LinkableEntityResolver {
    @Nullable
    public Object resolve(@Nonnull String var1);
}

