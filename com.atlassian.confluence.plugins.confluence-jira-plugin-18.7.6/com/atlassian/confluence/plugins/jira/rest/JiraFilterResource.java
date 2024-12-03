/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.jira.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.AsyncJiraIssueBatchService;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.model.JiraResponseData;
import com.atlassian.confluence.plugins.jira.beans.MacroTableParam;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URLDecoder;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/jira")
@ReadOnlyAccessAllowed
@Produces(value={"application/json"})
@AnonymousAllowed
public class JiraFilterResource {
    private final ReadOnlyApplicationLinkService appLinkService;
    private final JiraIssuesManager jiraIssuesManager;
    private final AsyncJiraIssueBatchService asyncJiraIssueBatchService;
    private final Renderer viewRenderer;
    private final StorageFormatCleaner storageFormatCleaner;
    private final PermissionManager permissionManager;

    public JiraFilterResource(ReadOnlyApplicationLinkService appLinkService, JiraIssuesManager jiraIssuesManager, AsyncJiraIssueBatchService asyncJiraIssueBatchService, Renderer viewRenderer, StorageFormatCleaner storageFormatCleaner, PermissionManager permissionManager) {
        this.appLinkService = appLinkService;
        this.jiraIssuesManager = jiraIssuesManager;
        this.asyncJiraIssueBatchService = asyncJiraIssueBatchService;
        this.viewRenderer = viewRenderer;
        this.storageFormatCleaner = storageFormatCleaner;
        this.permissionManager = permissionManager;
    }

    @POST
    @Path(value="clientIds")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response getRenderedJiraMacros(@Nonnull String clientIds) throws Exception {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        String[] clientIdArr = StringUtils.split((String)clientIds, (String)",");
        JsonArray clientIdJsons = new JsonArray();
        Response.Status globalStatus = Response.Status.OK;
        for (String clientIdString : clientIdArr) {
            JsonObject resultJsonObject;
            ClientId clientId = ClientId.fromClientId(clientIdString);
            JiraResponseData jiraResponseData = this.asyncJiraIssueBatchService.getAsyncJiraResults(clientId);
            if (jiraResponseData == null) {
                if (this.asyncJiraIssueBatchService.reprocessRequest(clientId)) {
                    resultJsonObject = this.createResultJsonObject(clientId, Response.Status.ACCEPTED.getStatusCode(), "");
                    globalStatus = Response.Status.ACCEPTED;
                } else {
                    resultJsonObject = this.createResultJsonObject(clientId, Response.Status.PRECONDITION_FAILED.getStatusCode(), "Jira issues is not available");
                }
            } else if (jiraResponseData.getStatus() == JiraResponseData.Status.WORKING) {
                resultJsonObject = this.createResultJsonObject(clientId, Response.Status.ACCEPTED.getStatusCode(), "");
                globalStatus = Response.Status.ACCEPTED;
            } else {
                resultJsonObject = this.createResultJsonObject(clientId, Response.Status.OK.getStatusCode(), new Gson().toJson((Object)jiraResponseData));
            }
            clientIdJsons.add((JsonElement)resultJsonObject);
        }
        return Response.status((Response.Status)globalStatus).entity((Object)clientIdJsons.toString()).build();
    }

    private JsonObject createResultJsonObject(ClientId clientId, int statusCode, String data) {
        JsonObject responseDataJson = new JsonObject();
        if (clientId != null) {
            responseDataJson.addProperty("clientId", clientId.toString());
        }
        responseDataJson.addProperty("data", data);
        responseDataJson.addProperty("status", (Number)statusCode);
        return responseDataJson;
    }

    @POST
    @Path(value="renderTable")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response getRenderedJiraMacroTable(MacroTableParam macroTableParam) throws Exception {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)new PageContext());
        conversionContext.setProperty("clearCache", (Object)macroTableParam.getClearCache());
        conversionContext.setProperty("orderColumnName", (Object)macroTableParam.getColumnName());
        conversionContext.setProperty("order", (Object)macroTableParam.getOrder());
        conversionContext.setProperty("placeholder", (Object)Boolean.FALSE);
        String wikiMarkup = this.storageFormatCleaner.cleanQuietly(URLDecoder.decode(macroTableParam.getWikiMarkup(), GeneralUtil.getCharacterEncoding()));
        String htmlTableContent = this.viewRenderer.render(wikiMarkup, (ConversionContext)conversionContext);
        return Response.ok((Object)this.createResultJsonObject(null, Response.Status.OK.getStatusCode(), htmlTableContent).toString()).build();
    }

    @GET
    @Path(value="appLink/{appLinkId}/filter/{filterId}")
    public Response getJiraFilterObject(@PathParam(value="appLinkId") String appLinkId, @PathParam(value="filterId") String filterId) {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        ReadOnlyApplicationLink appLink = this.appLinkService.getApplicationLink(new ApplicationId(appLinkId));
        if (appLink != null) {
            try {
                String jql = this.jiraIssuesManager.retrieveJQLFromFilter(filterId, appLink);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("jql", jql);
                return Response.ok((Object)jsonObject.toString()).build();
            }
            catch (ResponseException e) {
                if (e.getCause() instanceof CredentialsRequiredException) {
                    String authorisationURI = ((CredentialsRequiredException)e.getCause()).getAuthorisationURI().toString();
                    return this.buildUnauthorizedResponse(authorisationURI);
                }
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response buildUnauthorizedResponse(String oAuthenticationUri) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).header("WWW-Authenticate", (Object)("OAuth realm=\"" + oAuthenticationUri + "\"")).build();
    }
}

