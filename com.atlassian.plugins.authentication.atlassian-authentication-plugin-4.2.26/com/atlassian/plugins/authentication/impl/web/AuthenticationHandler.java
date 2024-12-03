/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationHandler<T extends IdpConfig> {
    @Nonnull
    public String getConsumerServletUrl();

    @Nonnull
    public String getIssuerUrl();

    public void processAuthenticationRequest(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2, @Nullable String var3, T var4) throws IOException;

    public boolean isCorrectlyConfigured(IdpConfig var1);
}

