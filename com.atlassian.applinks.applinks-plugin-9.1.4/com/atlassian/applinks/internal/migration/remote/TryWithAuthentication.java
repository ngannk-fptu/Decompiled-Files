/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.migration.remote;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import javax.annotation.Nonnull;

@Internal
abstract class TryWithAuthentication {
    public static int TIME_OUT_IN_SECONDS = 15;

    TryWithAuthentication() {
    }

    protected abstract boolean execute(@Nonnull ApplicationLink var1, @Nonnull ApplicationId var2, @Nonnull ApplicationLinkRequestFactory var3) throws IOException, CredentialsRequiredException, ResponseException, AuthenticationConfigurationException;

    boolean execute(@Nonnull ApplicationLink applicationLink, @Nonnull ApplicationId applicationId, @Nonnull Class<? extends AuthenticationProvider> providerClass) throws IOException, CredentialsRequiredException, ResponseException, AuthenticationConfigurationException {
        ApplicationLinkRequestFactory factory = applicationLink.createAuthenticatedRequestFactory(providerClass);
        return factory != null && this.execute(applicationLink, applicationId, factory);
    }
}

