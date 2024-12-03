/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.pkce;

import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import javax.annotation.Nonnull;

public interface PkceService {
    public boolean isValidCode(@Nonnull String var1);

    public boolean isExpectedCodeChallengeGenerated(@Nonnull String var1, @Nonnull CodeChallengeMethod var2, @Nonnull String var3);
}

