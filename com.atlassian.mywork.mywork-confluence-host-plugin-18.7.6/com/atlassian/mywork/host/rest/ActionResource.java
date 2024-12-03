/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.mywork.service.ActionResult
 *  com.atlassian.mywork.service.ActionServiceSelector
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.mywork.service.LocalTaskService
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.mywork.host.service.AppLinkHelper;
import com.atlassian.mywork.host.util.HostUtils;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.service.ActionResult;
import com.atlassian.mywork.service.ActionServiceSelector;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.mywork.service.LocalTaskService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="action")
@Produces(value={"application/json"})
public class ActionResource {
    private static final Logger log = LoggerFactory.getLogger(ActionResource.class);
    private final UserManager userManager;
    private final LocalNotificationService notificationService;
    private final LocalTaskService taskService;
    private final HostApplication hostApplication;
    private final ActionServiceSelector actionService;
    private final ApplicationLinkService applicationLinkService;
    private final AppLinkHelper appLinkHelper;
    private final I18nResolver i18nResolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActionResource(UserManager userManager, LocalNotificationService notificationService, LocalTaskService taskService, @Qualifier(value="hostApplication") HostApplication hostApplication, ActionServiceSelector actionService, ApplicationLinkService applicationLinkService, AppLinkHelper appLinkHelper, I18nResolver i18nResolver) {
        this.userManager = userManager;
        this.notificationService = notificationService;
        this.taskService = taskService;
        this.hostApplication = hostApplication;
        this.actionService = actionService;
        this.applicationLinkService = applicationLinkService;
        this.appLinkHelper = appLinkHelper;
        this.i18nResolver = i18nResolver;
    }

    @POST
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response execute(@Context HttpServletRequest request, JsonNode action) {
        String username = this.userManager.getRemoteUsername(request);
        long notificationId = action.get("id").getLongValue();
        Notification notification = this.notificationService.find(username, notificationId);
        return Response.ok((Object)this.send(action, username, notification.getApplicationLinkId())).build();
    }

    @POST
    @Path(value="task")
    @XsrfProtectionExcluded
    public Response executeTask(@Context HttpServletRequest request, JsonNode action) {
        String username = this.userManager.getRemoteUsername(request);
        Task task = this.taskService.get(action.get("id").getLongValue());
        return Response.ok((Object)this.send(action, username, task.getApplicationLinkId())).build();
    }

    private ActionResult send(final JsonNode action, String username, String applicationLinkId) {
        ActionResult actionResult;
        if (this.hostApplication.getId().get().equals(applicationLinkId) || StringUtils.isEmpty((CharSequence)applicationLinkId)) {
            actionResult = this.actionService.get(action.get("application").getTextValue()).execute(username, action);
        } else {
            final ApplicationLink applicationLink = this.findApplicationLink(applicationLinkId);
            if (applicationLink == null) {
                return InternalFailedActionResult.failure(this.i18nResolver.getText("com.atlassian.mywork.action.no.applink"));
            }
            try {
                actionResult = this.appLinkHelper.execute(username, applicationLink, "/rest/mywork-client/1/action", new Function<ApplicationLinkRequest, ApplicationLinkRequest>(){

                    public ApplicationLinkRequest apply(ApplicationLinkRequest appLinkReq) {
                        return (ApplicationLinkRequest)((ApplicationLinkRequest)((ApplicationLinkRequest)appLinkReq.setRequestBody(action.toString())).setHeader("Content-Type", "application/json")).setHeader("X-Atlassian-Token", "no-check");
                    }
                }, new Function<com.atlassian.sal.api.net.Response, ActionResult>(){

                    public ActionResult apply(com.atlassian.sal.api.net.Response response) {
                        try {
                            if (response.isSuccessful()) {
                                return (ActionResult)ActionResource.this.objectMapper.readValue(response.getResponseBodyAsStream(), ActionResult.class);
                            }
                            log.info("Request failed with status '" + response.getStatusText() + "' and response '" + response.getResponseBodyAsString() + "'");
                            return ActionResult.FAILED;
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        catch (ResponseException e) {
                            log.info("Failed to perform inline action at remote host", (Throwable)e);
                            return ActionResult.FAILED;
                        }
                    }
                }, new Function<AuthorisationURIGenerator, ActionResult>(){

                    public ActionResult apply(AuthorisationURIGenerator from) {
                        String appId = applicationLink.getId().get();
                        String url = ActionResource.this.hostApplication.getBaseUrl().toString() + "/plugins/servlet/mwauthredirect?target=" + HostUtils.urlEncode(appId);
                        return InternalFailedActionResult.oauthFailure(from.getAuthorisationURI(URI.create(url)).toASCIIString());
                    }
                });
            }
            catch (ResponseException e) {
                log.info("Failed to perform inline action at remote host", (Throwable)e);
                actionResult = ActionResult.FAILED;
            }
        }
        return actionResult;
    }

    private ApplicationLink findApplicationLink(String applicationLinkId) {
        try {
            return this.applicationLinkService.getApplicationLink(new ApplicationId(applicationLinkId));
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
    }

    public static class InternalFailedActionResult
    extends ActionResult {
        private final String authUrl;
        private final String message;

        private InternalFailedActionResult(String authUrl, String message) {
            super(false, null, null);
            this.authUrl = authUrl;
            this.message = message;
        }

        @JsonProperty
        public String getAuthUrl() {
            return this.authUrl;
        }

        @JsonProperty
        public String getMessage() {
            return this.message;
        }

        public static InternalFailedActionResult oauthFailure(String authUrl) {
            return new InternalFailedActionResult(authUrl, null);
        }

        public static InternalFailedActionResult failure(String message) {
            return new InternalFailedActionResult(null, message);
        }
    }
}

