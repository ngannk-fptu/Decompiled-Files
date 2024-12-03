/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.core.rest.ManifestResource;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.net.ResponsePreconditions;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.RestRequestBuilder;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.NetworkErrorTranslator;
import com.atlassian.applinks.internal.status.error.SimpleApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthConnectionVerifier;
import com.atlassian.applinks.internal.util.remote.AnonymousApplinksResponseHandler;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

public class TwoLeggedOAuthConnectionVerifier
implements OAuthConnectionVerifier {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;

    @Autowired
    public TwoLeggedOAuthConnectionVerifier(AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.authenticationConfigurationManager = authenticationConfigurationManager;
    }

    @Override
    public void verifyOAuthConnection(@Nonnull ApplicationLink link) throws ApplinkStatusException {
        Objects.requireNonNull(link, "link");
        if (!this.is2LoConfigured(link)) {
            return;
        }
        try {
            AuthorisationUriAwareRequest request = new RestRequestBuilder(link).methodType(Request.MethodType.GET).url(ManifestResource.manifestUrl()).accept("application/json").authentication(TwoLeggedOAuthAuthenticationProvider.class).buildAnonymous();
            request.execute(new OAuthEchoResponseHandler());
        }
        catch (ResponseException e) {
            throw NetworkErrorTranslator.toApplinkErrorException(e, "2LO OAuth request failed");
        }
    }

    private boolean is2LoConfigured(ApplicationLink link) {
        return this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthAuthenticationProvider.class);
    }

    private static class OAuthEchoResponseHandler
    extends AnonymousApplinksResponseHandler<Void> {
        private OAuthEchoResponseHandler() {
        }

        public Void handle(Response response) throws ResponseException {
            ResponsePreconditions.checkStatus(response, Response.Status.OK, Response.Status.UNAUTHORIZED);
            if (response.getStatusCode() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                if (ApplinksOAuth.isAuthLevelDisabled(response)) {
                    throw new SimpleApplinkStatusException(ApplinkErrorType.AUTH_LEVEL_UNSUPPORTED, "Received 401 from remote application, indicating that 2LO is not enabled");
                }
                ResponsePreconditions.fail(response);
            }
            return null;
        }
    }
}

