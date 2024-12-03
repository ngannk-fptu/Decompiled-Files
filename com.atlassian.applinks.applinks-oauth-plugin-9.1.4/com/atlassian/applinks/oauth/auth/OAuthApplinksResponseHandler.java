/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException
 *  com.atlassian.applinks.internal.common.net.AuthenticationAwareApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException;
import com.atlassian.applinks.internal.common.net.AuthenticationAwareApplicationLinkResponseHandler;
import com.atlassian.applinks.oauth.auth.OAuthParameters;
import com.atlassian.applinks.oauth.auth.OAuthRedirectingApplicationLinkResponseHandler;
import com.atlassian.applinks.oauth.auth.OAuthRequest;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OAuthApplinksResponseHandler<R>
extends OAuthRedirectingApplicationLinkResponseHandler
implements AuthenticationAwareApplicationLinkResponseHandler<R> {
    private final AuthenticationAwareApplicationLinkResponseHandler<R> applicationLinkResponseHandler;

    public OAuthApplinksResponseHandler(String url, ApplicationLinkResponseHandler<R> applicationLinkResponseHandler, ConsumerTokenStoreService consumerTokenStoreService, OAuthRequest wrappedRequest, ApplicationId applicationId, String username, boolean followRedirects) {
        super(url, (ApplicationLinkRequest)wrappedRequest, consumerTokenStoreService, applicationId, username, followRedirects);
        this.applicationLinkResponseHandler = this.getAuthenticationAwareApplicationLinkResponseHandler(applicationLinkResponseHandler);
    }

    public OAuthApplinksResponseHandler(String url, ApplicationLinkResponseHandler<R> applicationLinkResponseHandler, OAuthRequest wrappedRequest, ApplicationId applicationId, boolean followRedirects) {
        super(url, (ApplicationLinkRequest)wrappedRequest, null, applicationId, null, followRedirects);
        this.applicationLinkResponseHandler = this.getAuthenticationAwareApplicationLinkResponseHandler(applicationLinkResponseHandler);
    }

    public R credentialsRequired(Response response) throws ResponseException {
        return (R)this.applicationLinkResponseHandler.credentialsRequired(response);
    }

    @Nonnull
    public R credentialsRequired(@Nonnull Response response, @Nullable String problem, @Nullable String problemAdvice) throws ResponseException {
        return (R)this.applicationLinkResponseHandler.credentialsRequired(response, problem, problemAdvice);
    }

    @Nonnull
    public R authenticationFailed(@Nonnull Response response, @Nullable String problem, @Nullable String problemAdvice) throws ResponseException {
        return (R)this.applicationLinkResponseHandler.authenticationFailed(response, problem, problemAdvice);
    }

    public R handle(Response response) throws ResponseException {
        this.checkForOAuthProblemAndRemoveConsumerTokenIfNecessary(response);
        if (this.hasTokenProblems) {
            return (R)this.applicationLinkResponseHandler.credentialsRequired(response, this.authenticationProblem, this.authenticationProblemAdvice);
        }
        if (this.authenticationProblem != null) {
            return (R)this.applicationLinkResponseHandler.authenticationFailed(response, this.authenticationProblem, this.authenticationProblemAdvice);
        }
        if (this.followRedirects && this.redirectHelper.responseShouldRedirect(response)) {
            this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
            return (R)this.wrappedRequest.execute((ApplicationLinkResponseHandler)this);
        }
        return (R)this.applicationLinkResponseHandler.handle(response);
    }

    private AuthenticationAwareApplicationLinkResponseHandler<R> getAuthenticationAwareApplicationLinkResponseHandler(final ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) {
        return new AuthenticationAwareApplicationLinkResponseHandler<R>(){

            @Nonnull
            public R authenticationFailed(@Nonnull Response response, @Nullable String reason, @Nullable String authenticationProblemAdvice) throws ResponseException {
                if (AuthenticationAwareApplicationLinkResponseHandler.class.isInstance(applicationLinkResponseHandler)) {
                    return ((AuthenticationAwareApplicationLinkResponseHandler)applicationLinkResponseHandler).authenticationFailed(response, reason, authenticationProblemAdvice);
                }
                throw new OAuthMessageProblemException("OAuth authentication failed: " + reason, reason, authenticationProblemAdvice, OAuthParameters.asMap(OAuthApplinksResponseHandler.this.allParameters));
            }

            public R credentialsRequired(Response response) throws ResponseException {
                return applicationLinkResponseHandler.credentialsRequired(response);
            }

            @Nonnull
            public R credentialsRequired(@Nonnull Response response, @Nullable String problem, @Nullable String problemAdvice) throws ResponseException {
                if (AuthenticationAwareApplicationLinkResponseHandler.class.isAssignableFrom(applicationLinkResponseHandler.getClass())) {
                    return ((AuthenticationAwareApplicationLinkResponseHandler)applicationLinkResponseHandler).credentialsRequired(response, problem, problemAdvice);
                }
                return applicationLinkResponseHandler.credentialsRequired(response);
            }

            public R handle(Response response) throws ResponseException {
                return applicationLinkResponseHandler.handle(response);
            }
        };
    }
}

