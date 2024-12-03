/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.net;

import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AuthenticationAwareApplicationLinkResponseHandler<R>
extends ApplicationLinkResponseHandler<R> {
    @Nonnull
    public R credentialsRequired(@Nonnull Response var1, @Nullable String var2, @Nullable String var3) throws ResponseException;

    @Nonnull
    public R authenticationFailed(@Nonnull Response var1, @Nullable String var2, @Nullable String var3) throws ResponseException;
}

