/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.model.JiraResponseData;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AsyncJiraIssueBatchService {
    public static final String DARK_FEATURE_DISABLE_ASYNC_LOADING_KEY = "confluence.extra.jira.async.loading.disable";

    public void processRequest(ClientId var1, String var2, Set<String> var3, List<MacroDefinition> var4, ConversionContext var5);

    public boolean reprocessRequest(ClientId var1) throws XhtmlException, MacroExecutionException;

    public void processRequestWithJql(ClientId var1, Map<String, String> var2, ConversionContext var3, ReadOnlyApplicationLink var4) throws MacroExecutionException;

    public JiraResponseData getAsyncJiraResults(ClientId var1);
}

