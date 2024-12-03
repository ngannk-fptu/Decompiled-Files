/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.confluence.plugins.metadata.jira.exception;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;

public class JiraMetadataException
extends Exception {
    private String applinkName;
    private JiraMetadataErrorHelper.Status status;

    public JiraMetadataException(ReadOnlyApplicationLink applink, JiraMetadataErrorHelper.Status status, Throwable throwable) {
        super(throwable);
        this.applinkName = applink.getName();
        this.status = status;
    }

    public JiraMetadataException(JiraMetadataErrorHelper.Status status, Throwable throwable) {
        super(throwable);
        this.applinkName = null;
        this.status = status;
    }

    public String getApplinkName() {
        return this.applinkName;
    }

    public JiraMetadataErrorHelper.Status getStatus() {
        return this.status;
    }
}

