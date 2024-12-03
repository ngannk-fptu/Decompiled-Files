/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CanonicalHttpRequest {
    @Nonnull
    public String getMethod();

    @Nullable
    public String getRelativePath();

    @Nonnull
    public Map<String, String[]> getParameterMap();
}

