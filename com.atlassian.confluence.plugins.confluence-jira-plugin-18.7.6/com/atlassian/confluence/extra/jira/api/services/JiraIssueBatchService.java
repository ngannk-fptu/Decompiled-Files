/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.jira.exception.UnsupportedJiraServerException;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;
import java.util.Set;

public interface JiraIssueBatchService {
    public static final String ELEMENT_MAP = "elementMap";
    public static final String JIRA_DISPLAY_URL = "jiraDisplayUrl";
    public static final String JIRA_RPC_URL = "jiraRpcUrl";
    public static final Long SUPPORTED_JIRA_SERVER_BUILD_NUMBER = 6097L;

    public Map<String, Object> getBatchResults(String var1, Set<String> var2, ConversionContext var3) throws MacroExecutionException, UnsupportedJiraServerException;

    public Map<String, Object> getPlaceHolderBatchResults(ClientId var1, String var2, Set<String> var3, ConversionContext var4) throws MacroExecutionException, UnsupportedJiraServerException;
}

