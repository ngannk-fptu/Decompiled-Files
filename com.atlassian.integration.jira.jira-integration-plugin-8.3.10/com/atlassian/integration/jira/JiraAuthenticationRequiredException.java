/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.integration.jira.ApplicationNameAwareJiraException;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.annotation.Nonnull;

public class JiraAuthenticationRequiredException
extends ApplicationNameAwareJiraException {
    private String applicationName;
    private AuthorisationURIGenerator uriGenerator;

    public JiraAuthenticationRequiredException(@Nonnull String message, @Nonnull String applicationName, @Nonnull AuthorisationURIGenerator uriGenerator) {
        super(message);
        this.applicationName = (String)Preconditions.checkNotNull((Object)applicationName, (Object)"applicationName");
        this.uriGenerator = (AuthorisationURIGenerator)Preconditions.checkNotNull((Object)uriGenerator, (Object)"uriGenerator");
    }

    @Override
    @Nonnull
    public String getApplicationName() {
        return this.applicationName;
    }

    @Nonnull
    public URI getAuthenticationUri() {
        return this.uriGenerator.getAuthorisationURI();
    }

    @Nonnull
    public URI getAuthenticationUri(URI callback) {
        return this.uriGenerator.getAuthorisationURI(callback);
    }
}

