/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import java.util.List;

public interface JiraConnectorManager {
    public List<JiraServerBean> getJiraServers();

    public JiraServerBean getJiraServer(ReadOnlyApplicationLink var1);

    public void updateDetailJiraServerInfor(ReadOnlyApplicationLink var1);

    public void updatePrimaryServer(ReadOnlyApplicationLink var1);
}

