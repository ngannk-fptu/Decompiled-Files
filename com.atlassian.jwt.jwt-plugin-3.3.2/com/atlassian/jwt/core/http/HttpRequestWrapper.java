/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.jwt.core.http;

import com.atlassian.jwt.CanonicalHttpRequest;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface HttpRequestWrapper {
    @Nullable
    public String getParameter(String var1);

    @Nonnull
    public Iterable<String> getHeaderValues(String var1);

    @Nonnull
    public CanonicalHttpRequest getCanonicalHttpRequest();
}

