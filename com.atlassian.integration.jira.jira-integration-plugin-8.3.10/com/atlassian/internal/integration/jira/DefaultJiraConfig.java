/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.internal.integration.jira.JiraConfig;
import com.atlassian.internal.integration.jira.JiraSettings;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJiraConfig
implements JiraConfig {
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    public static final int DEFAULT_CREATE_META_MAX_RESULTS = 200;
    public static final int DEFAULT_MAX_BULK_ISSUES = 25;
    public static final int DEFAULT_MAX_ISSUES = 20;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int MAX_CREATE_META_MAX_RESULTS = 1000;
    public static final int MAX_MAX_BULK_ISSUES = 35;
    public static final int MAX_MAX_ISSUES = 50;
    public static final int MAX_TIMEOUT = 60000;
    public static final int MIN_CREATE_META_MAX_RESULTS = 10;
    public static final int MIN_MAX_BULK_ISSUES = 5;
    public static final int MIN_MAX_ISSUES = 5;
    public static final int MIN_TIMEOUT = 2000;
    public static final String PROP_CONNECT_TIMEOUT = "plugin.jira-integration.remote.timeout.connection";
    public static final String PROP_CREATE_META_MAX_RESULTS = "plugin.jira-integration.create.meta.max.results";
    public static final String PROP_MAX_BULK_ISSUES = "plugin.jira-integration.bulk.max.issues";
    public static final String PROP_MAX_ISSUES = "plugin.jira-integration.remote.page.max.issues";
    public static final String PROP_SOCKET_TIMEOUT = "plugin.jira-integration.remote.timeout.socket";
    private static final Logger log = LoggerFactory.getLogger(DefaultJiraConfig.class);
    private ApplicationProperties properties;
    private boolean basicAuthenticationAllowed;
    private int connectTimeout = 5000;
    private int createMetaMaxResults = 200;
    private int maxBulkIssues;
    private int maxIssues = 20;
    private int socketTimeout = 10000;

    @Override
    public void configure(@Nonnull ApplicationLinkRequest request) {
        ((ApplicationLinkRequest)Preconditions.checkNotNull((Object)request, (Object)"request")).setConnectionTimeout(this.connectTimeout);
        request.setSoTimeout(this.socketTimeout);
    }

    @Override
    @Nonnull
    public String getBaseUrl() {
        return this.properties.getBaseUrl();
    }

    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    @Override
    public int getCreateMetaMaxResults() {
        return this.createMetaMaxResults;
    }

    @Override
    public int getMaxBulkIssues() {
        return this.maxBulkIssues;
    }

    @Override
    public int getMaxIssues() {
        return this.maxIssues;
    }

    @Override
    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    @Override
    public boolean isBasicAuthenticationAllowed() {
        return this.basicAuthenticationAllowed;
    }

    @Autowired
    public void setApplicationProperties(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Autowired(required=false)
    public void setJiraSettings(JiraSettings settings) {
        this.basicAuthenticationAllowed = settings.isBasicAuthenticationAllowed();
        this.connectTimeout = settings.getProperty(PROP_CONNECT_TIMEOUT, 5000, 2000, 60000);
        this.createMetaMaxResults = settings.getProperty(PROP_CREATE_META_MAX_RESULTS, 200, 10, 1000);
        this.maxBulkIssues = settings.getProperty(PROP_MAX_BULK_ISSUES, 25, 5, 35);
        this.maxIssues = settings.getProperty(PROP_MAX_ISSUES, 20, 5, 50);
        this.socketTimeout = settings.getProperty(PROP_SOCKET_TIMEOUT, 10000, 2000, 60000);
    }
}

