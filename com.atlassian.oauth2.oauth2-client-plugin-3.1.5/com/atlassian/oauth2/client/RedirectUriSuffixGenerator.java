/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface RedirectUriSuffixGenerator {
    @Nonnull
    public String generateRedirectUriSuffix(@Nonnull String var1);
}

