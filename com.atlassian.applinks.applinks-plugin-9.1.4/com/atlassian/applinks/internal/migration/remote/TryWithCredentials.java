/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.internal.migration.remote.TryWithAuthentication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TryWithCredentials
extends TryWithAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(TryWithCredentials.class);
    private final TryWithAuthentication tryWithAuthentication;

    public TryWithCredentials(TryWithAuthentication tryWithAuthentication) {
        this.tryWithAuthentication = tryWithAuthentication;
    }

    @Override
    protected boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull ApplicationLinkRequestFactory factory) throws IOException, ResponseException, AuthenticationConfigurationException {
        return false;
    }

    @Override
    boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull Class<? extends AuthenticationProvider> providerClass) throws IOException, ResponseException, AuthenticationConfigurationException {
        try {
            return this.tryWithAuthentication.execute(applicationLink, applicationId, providerClass);
        }
        catch (CredentialsRequiredException ex) {
            LOGGER.warn(ex.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Failed to execute remote action {}", (Object)this.tryWithAuthentication.getClass().getSimpleName(), (Object)ex);
            }
            return false;
        }
    }
}

