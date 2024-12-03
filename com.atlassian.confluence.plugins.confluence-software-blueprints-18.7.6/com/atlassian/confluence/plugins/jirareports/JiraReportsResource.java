/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/")
@Produces(value={"application/json"})
@AnonymousAllowed
public class JiraReportsResource {
    private static final String PROJECT_REST_URI = "/rest/api/2/project";
    private static final Logger LOG = LoggerFactory.getLogger(JiraReportsResource.class);
    private final ApplicationLinkService appLinkService;

    public JiraReportsResource(ApplicationLinkService appLinkService) {
        this.appLinkService = appLinkService;
    }

    @GET
    @Path(value="appLink/{appLinkId}/projects")
    public Response getProjectsByAppLinkId(@PathParam(value="appLinkId") String appLinkId) {
        ApplicationLink appLink = null;
        String url = null;
        try {
            appLink = this.appLinkService.getApplicationLink(new ApplicationId(appLinkId));
            url = appLink.getRpcUrl() + PROJECT_REST_URI;
            String projects = this.requestJiraByAuthenticatedUser((ReadOnlyApplicationLink)appLink, url);
            return Response.ok((Object)projects).build();
        }
        catch (CredentialsRequiredException e) {
            String projects = this.requestJiraByAnonymousUser((ReadOnlyApplicationLink)appLink, url);
            return this.buildUnAuthenticatedResponse(projects, e.getAuthorisationURI().toString());
        }
        catch (Exception e) {
            LOG.error("Can not retrieve projects", (Throwable)e);
            return Response.status((int)400).build();
        }
    }

    @GET
    @Path(value="appLink/{appLinkId}/project/{projectKey}/versions")
    public Response getVersionsByKeyProject(@PathParam(value="appLinkId") String appLinkId, @PathParam(value="projectKey") String projectKey) {
        ApplicationLink appLink = null;
        String url = null;
        try {
            appLink = this.appLinkService.getApplicationLink(new ApplicationId(appLinkId));
            url = appLink.getRpcUrl() + "/rest/api/2/project/" + projectKey + "/versions";
            String versions = this.requestJiraByAuthenticatedUser((ReadOnlyApplicationLink)appLink, url);
            return Response.ok((Object)versions).build();
        }
        catch (CredentialsRequiredException e) {
            String versions = this.requestJiraByAnonymousUser((ReadOnlyApplicationLink)appLink, url);
            return this.buildUnAuthenticatedResponse(versions, e.getAuthorisationURI().toString());
        }
        catch (Exception e) {
            LOG.error("Can not retrieve versions", (Throwable)e);
            return Response.status((int)400).build();
        }
    }

    private Response buildUnAuthenticatedResponse(String responseData, String authorisationURI) {
        Response.ResponseBuilder response = Response.status((int)401).header("WWW-Authenticate", (Object)("OAuth realm=\"" + authorisationURI + "\""));
        if (responseData != null) {
            response.entity((Object)responseData);
        }
        return response.build();
    }

    private String requestJiraByAnonymousUser(ReadOnlyApplicationLink appLink, String url) {
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
            return request.execute();
        }
        catch (Exception e) {
            LOG.error("Can not retrieve data from jira server by anonymous user", (Throwable)e);
            return null;
        }
    }

    private String requestJiraByAuthenticatedUser(ReadOnlyApplicationLink appLink, String url) throws CredentialsRequiredException, ResponseException {
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
        ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
        return request.execute();
    }
}

