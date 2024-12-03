/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.confluence.plugins.metadata.jira.service;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.sal.api.net.ResponseException;
import java.util.List;
import java.util.Map;

public interface JiraMetadataDelegate {
    public String getUrl(List<String> var1, Map<String, String> var2);

    public List<JiraMetadataSingleGroup> getGroups(ReadOnlyApplicationLink var1, ApplicationLinkRequest var2, Map<String, String> var3, List<String> var4) throws ResponseException;

    public boolean isSupported(ReadOnlyApplicationLink var1);
}

