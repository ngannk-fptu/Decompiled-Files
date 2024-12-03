/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.internal.common.net.ResponseContentException;
import com.atlassian.applinks.internal.common.net.ResponsePreconditions;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.RestRequestBuilder;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkOAuthStatus;
import com.atlassian.applinks.internal.rest.status.ApplinkStatusResource;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthStatusFetchStrategy;
import com.atlassian.applinks.internal.util.remote.AnonymousApplinksResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

final class StatusApiOAuthFetchStrategy
implements OAuthStatusFetchStrategy {
    private final Class<? extends AuthenticationProvider> authentication;

    StatusApiOAuthFetchStrategy(Class<? extends AuthenticationProvider> authentication) {
        this.authentication = authentication;
    }

    @Override
    @Nullable
    public ApplinkOAuthStatus fetch(@Nonnull ApplicationId localId, @Nonnull ApplicationLink applink) throws ApplinkStatusException, ResponseException {
        AuthorisationUriAwareRequest request = new RestRequestBuilder(applink).authentication(this.authentication).url(ApplinkStatusResource.oAuthStatusUrl(localId)).buildAnonymous();
        return (ApplinkOAuthStatus)request.execute(OAuthStatusHandler.INSTANCE);
    }

    private static final class OAuthStatusHandler
    extends AnonymousApplinksResponseHandler<ApplinkOAuthStatus> {
        static final OAuthStatusHandler INSTANCE = new OAuthStatusHandler();

        private OAuthStatusHandler() {
        }

        public ApplinkOAuthStatus handle(Response response) throws ResponseException {
            ResponsePreconditions.checkStatus(response, Response.Status.OK, Response.Status.NOT_FOUND);
            return response.getStatusCode() == Response.Status.OK.getStatusCode() ? this.getStatusFrom(response) : null;
        }

        private ApplinkOAuthStatus getStatusFrom(Response response) throws ResponseException {
            try {
                return ((RestApplinkOAuthStatus)response.getEntity(RestApplinkOAuthStatus.class)).asDomain();
            }
            catch (Exception e) {
                throw new ResponseContentException(response, e);
            }
        }
    }
}

