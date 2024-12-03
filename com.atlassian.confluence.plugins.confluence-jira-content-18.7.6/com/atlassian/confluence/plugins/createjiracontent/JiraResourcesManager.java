/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createjiracontent;

import com.atlassian.confluence.plugins.createjiracontent.rest.beans.CachableJiraServerBean;
import java.util.List;

public interface JiraResourcesManager {
    public List<CachableJiraServerBean> getJiraServers();

    public List<CachableJiraServerBean> getSupportedJiraServers();
}

