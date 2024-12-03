/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.jwt.applinks.exception.JwtRegistrationFailedException;
import javax.annotation.Nonnull;

public interface JwtPeerService {
    public void issueSharedSecret(@Nonnull ApplicationLink var1, @Nonnull String var2) throws JwtRegistrationFailedException;

    public void revokeSharedSecret(@Nonnull ApplicationLink var1);
}

