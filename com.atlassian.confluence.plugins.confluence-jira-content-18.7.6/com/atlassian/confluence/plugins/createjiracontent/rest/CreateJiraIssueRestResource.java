/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.extra.jira.JiraIssuesManager
 *  com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService
 *  com.atlassian.confluence.extra.jira.api.services.JqlBuilder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createjiracontent.rest;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.extra.jira.api.services.JqlBuilder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createjiracontent.JiraResourcesManager;
import com.atlassian.confluence.plugins.createjiracontent.rest.beans.CachableJiraServerBean;
import com.atlassian.confluence.plugins.createjiracontent.rest.beans.JiraIssue;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@UnlicensedSiteAccess
@Path(value="/")
public class CreateJiraIssueRestResource {
    private final PageManager pageManager;
    private MacroManager macroManager;
    private JiraMacroFinderService jiraMacroFinderService;
    private JiraIssuesManager jiraIssuesManager;
    private ReadOnlyApplicationLinkService appLinkService;
    private JiraResourcesManager jiraResourcesManager;

    public CreateJiraIssueRestResource(PageManager pageManager, MacroManager macroManager, JiraMacroFinderService jiraMacroFinderService, ReadOnlyApplicationLinkService applicationLinkService, JiraIssuesManager jiraIssuesManager, JiraResourcesManager jiraResourcesManager) {
        this.pageManager = pageManager;
        this.macroManager = macroManager;
        this.jiraMacroFinderService = jiraMacroFinderService;
        this.appLinkService = applicationLinkService;
        this.jiraIssuesManager = jiraIssuesManager;
        this.jiraResourcesManager = jiraResourcesManager;
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="find-epic-issue")
    public Response findJiraEpicIssue(@QueryParam(value="pageId") long pageId, @QueryParam(value="serverId") String serverId, @QueryParam(value="epicIssueTypeId") String epicIssueTypeId) throws MacroExecutionException, XhtmlException, CredentialsRequiredException, ResponseException {
        if (StringUtils.isBlank((CharSequence)serverId)) {
            throw new ResponseException("Server Id cannot be empty");
        }
        AbstractPage abstractPage = this.pageManager.getAbstractPage(pageId);
        Predicate epicMacroDefinitionFilter = macroDef -> {
            String macroServerId = (String)macroDef.getParameters().get("serverId");
            return StringUtils.equals((CharSequence)macroServerId, (CharSequence)serverId);
        };
        Set macroDefinitions = this.jiraMacroFinderService.findJiraIssueMacros(abstractPage, epicMacroDefinitionFilter);
        ReadOnlyApplicationLink applicationLink = this.getApplicationLink(serverId);
        if (applicationLink == null) {
            throw new ResponseException("Cannot find the applicationLink for serverId=" + serverId);
        }
        List<JiraIssue> jiraEpics = this.filterEpicIssue(applicationLink, macroDefinitions, epicIssueTypeId);
        if (jiraEpics.size() != 1) {
            return Response.noContent().build();
        }
        Macro macro = this.macroManager.getMacroByName("jira");
        if (macro != null) {
            String epicKey = jiraEpics.get(0).getKey();
            HashMap params = Maps.newHashMap();
            params.put("key", epicKey);
            params.put("showSummary", Boolean.TRUE.toString());
            params.put("serverId", serverId);
            String htmlPlaceHolder = macro.execute((Map)params, null, (ConversionContext)new DefaultConversionContext((RenderContext)abstractPage.toPageContext()));
            JsonObject epicResult = new JsonObject();
            epicResult.addProperty("epicKey", epicKey);
            epicResult.addProperty("epicHtmlPlaceHolder", htmlPlaceHolder);
            return Response.ok((Object)epicResult.toString()).build();
        }
        return Response.noContent().build();
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="get-jira-servers")
    public Response getJiraServers() {
        List<CachableJiraServerBean> jiraServers = this.jiraResourcesManager.getJiraServers();
        return jiraServers == null ? Response.ok((Object)Collections.EMPTY_LIST).build() : Response.ok(jiraServers).build();
    }

    private List<JiraIssue> filterEpicIssue(ReadOnlyApplicationLink applicationLink, Set<MacroDefinition> macroDefinitionsHaveServer, String epicIssueTypeId) throws CredentialsRequiredException, ResponseException {
        Function extractMacroDefinitionKeyFunction = definition -> (String)definition.getParameters().get("key");
        Collection issueKeysServer = Collections2.transform(macroDefinitionsHaveServer, (Function)extractMacroDefinitionKeyFunction);
        Iterables.removeAll((Iterable)issueKeysServer, Arrays.asList(null, ""));
        ArrayList keys = Lists.newArrayList((Iterable)issueKeysServer);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        String jsonStringResult = this.executeEpicJqlWithKeys(applicationLink, epicIssueTypeId, keys);
        List<String> errorKeys = this.detectErrorKeys(jsonStringResult, keys);
        if (!errorKeys.isEmpty()) {
            keys.removeAll(errorKeys);
            if (keys.isEmpty()) {
                return Collections.emptyList();
            }
            jsonStringResult = this.executeEpicJqlWithKeys(applicationLink, epicIssueTypeId, keys);
        }
        List<JiraIssue> issueWithServer = this.parseFromJsonIssue(jsonStringResult);
        return issueWithServer;
    }

    private String executeEpicJqlWithKeys(ReadOnlyApplicationLink applicationLink, String epicIssueTypeId, List<String> issueKeys) throws ResponseException, CredentialsRequiredException {
        String[] keys = issueKeys.toArray(new String[issueKeys.size()]);
        String jqlQuery = new JqlBuilder().issueTypes(new String[]{epicIssueTypeId}).issueKeys(keys).buildAndEncode();
        return this.jiraIssuesManager.executeJqlQuery(jqlQuery, applicationLink);
    }

    private List<String> detectErrorKeys(String jsonString, List<String> issueKeys) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
        if (!jsonObject.has("errorMessages")) {
            return Collections.emptyList();
        }
        ArrayList errorKeys = Lists.newArrayList();
        JsonArray errorJson = jsonObject.get("errorMessages").getAsJsonArray();
        for (int i = 0; i < errorJson.size(); ++i) {
            String errorMessage = errorJson.get(i).getAsString();
            for (String key : issueKeys) {
                if (!errorMessage.contains(key)) continue;
                errorKeys.add(key);
            }
        }
        return errorKeys;
    }

    private List<JiraIssue> parseFromJsonIssue(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
        ArrayList jiraIssueBeans = Lists.newArrayList();
        JsonArray issuesJson = jsonObject.get("issues").getAsJsonArray();
        for (int i = 0; i < issuesJson.size(); ++i) {
            JsonObject json = (JsonObject)issuesJson.get(i);
            jiraIssueBeans.add(new JiraIssue(json.get("key").getAsString()));
        }
        return jiraIssueBeans;
    }

    private ReadOnlyApplicationLink getApplicationLink(String serverId) {
        for (ReadOnlyApplicationLink applicationLink : this.appLinkService.getApplicationLinks(JiraApplicationType.class)) {
            if (!StringUtils.equals((CharSequence)serverId, (CharSequence)applicationLink.getId().toString())) continue;
            return applicationLink;
        }
        return null;
    }
}

