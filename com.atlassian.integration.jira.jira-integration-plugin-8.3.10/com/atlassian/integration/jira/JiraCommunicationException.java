/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.ApplicationNameAwareJiraException;
import java.net.URI;
import javax.annotation.Nonnull;

public class JiraCommunicationException
extends ApplicationNameAwareJiraException {
    private String applicationName;
    private URI applicationUrl;

    public JiraCommunicationException(@Nonnull String message, String applicationName, URI applicationUrl) {
        super(message);
        this.applicationName = applicationName;
        this.applicationUrl = applicationUrl;
    }

    @Override
    public String getApplicationName() {
        return this.applicationName;
    }

    public URI getApplicationUrl() {
        return this.applicationUrl;
    }
}

