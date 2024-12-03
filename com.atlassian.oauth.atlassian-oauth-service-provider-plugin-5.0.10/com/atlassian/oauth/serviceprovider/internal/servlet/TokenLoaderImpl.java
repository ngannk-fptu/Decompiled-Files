/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.InvalidTokenException
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Authorization
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  net.oauth.server.OAuthServlet
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.InvalidTokenException;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.servlet.TokenLoader;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;
import org.springframework.beans.factory.annotation.Qualifier;

public final class TokenLoaderImpl
implements TokenLoader {
    private final ServiceProviderTokenStore store;
    private final Clock clock;

    public TokenLoaderImpl(@Qualifier(value="tokenStore") ServiceProviderTokenStore store, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public ServiceProviderToken getTokenForAuthorization(HttpServletRequest request) throws OAuthProblemException, IOException {
        ServiceProviderToken token;
        OAuthMessage requestMessage = OAuthServlet.getMessage((HttpServletRequest)request, null);
        requestMessage.requireParameters(new String[]{"oauth_token"});
        try {
            token = this.store.get(requestMessage.getToken());
        }
        catch (InvalidTokenException e) {
            throw new OAuthProblemException("token_rejected");
        }
        if (token == null || token.isAccessToken()) {
            throw new OAuthProblemException("token_rejected");
        }
        if (token.getAuthorization() == ServiceProviderToken.Authorization.AUTHORIZED || token.getAuthorization() == ServiceProviderToken.Authorization.DENIED) {
            throw new OAuthProblemException("token_used");
        }
        if (token.hasExpired(this.clock)) {
            throw new OAuthProblemException("token_expired");
        }
        return token;
    }
}

