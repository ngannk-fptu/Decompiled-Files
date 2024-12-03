/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.migration.remote.TryWithAuthentication;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

class OAuthConfigMigrator
extends TryWithAuthentication {
    private final OAuthAutoConfigurator configurator;
    private final OAuthConfig incoming;
    private final OAuthConfig outgoing;

    public OAuthConfigMigrator(@Nonnull OAuthAutoConfigurator configurator, @Nonnull OAuthConfig incoming, @Nonnull OAuthConfig outgoing) {
        this.configurator = Objects.requireNonNull(configurator, "configurator");
        this.incoming = Objects.requireNonNull(incoming, "incoming");
        this.outgoing = Objects.requireNonNull(outgoing, "outgoing");
    }

    @Override
    public boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull ApplicationLinkRequestFactory factory) throws IOException, CredentialsRequiredException, ResponseException, AuthenticationConfigurationException {
        try {
            this.configurator.enable(this.incoming, this.outgoing, applicationLink, factory);
            return true;
        }
        catch (AuthenticationConfigurationException ex) {
            ResponseStatusException cause = ApplinkErrors.findCauseOfType(ex, ResponseStatusException.class);
            if (cause != null && (OAuthConfigMigrator.equalsStatus(cause.getResponse(), Response.Status.FORBIDDEN) || OAuthConfigMigrator.equalsStatus(cause.getResponse(), Response.Status.UNAUTHORIZED))) {
                return false;
            }
            throw ex;
        }
    }

    private static boolean equalsStatus(Response response, Response.Status status) {
        return response.getStatusCode() == status.getStatusCode();
    }

    protected static class Factory {
        protected Factory() {
        }

        OAuthConfigMigrator getInstance(OAuthAutoConfigurator authAutoConfigurator, OAuthConfig incoming, OAuthConfig outgoing) {
            return new OAuthConfigMigrator(authAutoConfigurator, incoming, outgoing);
        }
    }
}

