/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  javax.annotation.Nonnull
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import javax.annotation.Nonnull;

public interface JiraConfig {
    public void configure(@Nonnull ApplicationLinkRequest var1);

    @Nonnull
    public String getBaseUrl();

    public int getConnectTimeout();

    public int getCreateMetaMaxResults();

    public int getMaxBulkIssues();

    public int getMaxIssues();

    public int getSocketTimeout();

    public boolean isBasicAuthenticationAllowed();
}

