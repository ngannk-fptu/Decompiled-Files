/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.authorization;

import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationFlowResult;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.scopes.api.Scope;
import java.time.Duration;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AuthorizationService {
    public String startAuthorizationFlow(@Nonnull String var1, @Nonnull String var2, @Nonnull Scope var3, CodeChallengeMethod var4, String var5);

    public AuthorizationFlowResult completeAuthorizationFlow(@Nonnull String var1, @Nonnull String var2, @Nonnull String var3);

    public Optional<Authorization> getAuthorization(@Nonnull String var1);

    public boolean isPkceEnabledForAuthorization(@Nonnull String var1);

    public boolean isPkceCodeVerifierValidAgainstAuthorization(@Nonnull String var1, @Nonnull String var2);

    public void removeExpiredAuthorizations(@Nonnull Duration var1);
}

