/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.mywork.model.NotificationBuilder
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  org.codehaus.jackson.node.JsonNodeFactory
 *  org.codehaus.jackson.node.ObjectNode
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.mywork.host.provider.MyWorkRegistrationProvider;
import com.atlassian.mywork.host.service.ClientServiceImpl;
import com.atlassian.mywork.host.util.HostUtils;
import com.atlassian.mywork.model.NotificationBuilder;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.net.URI;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AppLinkHelper {
    private final LocalNotificationService notificationService;
    private final InternalHostApplication internalHostApplication;
    private final I18nResolver i18nResolver;

    public AppLinkHelper(LocalNotificationService notificationService, @ComponentImport @Qualifier(value="internalHostApplication") InternalHostApplication internalHostApplication, @ComponentImport I18nResolver i18nResolver) {
        this.notificationService = notificationService;
        this.internalHostApplication = internalHostApplication;
        this.i18nResolver = i18nResolver;
    }

    public <T> T execute(String username, ApplicationLink appLink, String url, Function<Response, T> f2) throws ResponseException {
        return this.execute(username, appLink, url, (Function<ApplicationLinkRequest, ApplicationLinkRequest>)Functions.identity(), f2, AppLinkHelper.constant(null), Request.MethodType.POST);
    }

    public <T> T execute(String username, ApplicationLink appLink, String url, Function<ApplicationLinkRequest, ApplicationLinkRequest> processRequest, Function<Response, T> processResponse, Function<AuthorisationURIGenerator, T> processCredentialsRequired) throws ResponseException {
        return this.execute(username, appLink, url, processRequest, processResponse, processCredentialsRequired, Request.MethodType.POST);
    }

    public <T> T execute(final String username, final ApplicationLink appLink, String url, Function<ApplicationLinkRequest, ApplicationLinkRequest> processRequest, final Function<Response, T> processResponse, final Function<AuthorisationURIGenerator, T> processCredentialsRequired, Request.MethodType type) throws ResponseException {
        final ApplicationLinkRequestFactory authenticatedRequestFactory = appLink.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class);
        try {
            ApplicationLinkRequest request = (ApplicationLinkRequest)processRequest.apply((Object)authenticatedRequestFactory.createRequest(type, url));
            request.setHeader("X-Atlassian-Token", "no-check");
            return (T)request.execute(new ApplicationLinkResponseHandler<T>(){

                public T credentialsRequired(Response response) {
                    AppLinkHelper.this.createNotification(username, appLink, (AuthorisationURIGenerator)authenticatedRequestFactory);
                    return processCredentialsRequired.apply((Object)authenticatedRequestFactory);
                }

                public T handle(Response response) {
                    return processResponse.apply((Object)response);
                }
            });
        }
        catch (CredentialsRequiredException e) {
            this.createNotification(username, appLink, (AuthorisationURIGenerator)authenticatedRequestFactory);
            return (T)processCredentialsRequired.apply((Object)authenticatedRequestFactory);
        }
    }

    protected void createNotification(String username, ApplicationLink applicationLink, AuthorisationURIGenerator authorisationURIGenerator) {
        String appId = applicationLink.getId().get();
        String globalId = ClientServiceImpl.generateGlobalId(appId);
        if (this.notificationService.count(username, globalId) != 0) {
            return;
        }
        String url = this.internalHostApplication.getBaseUrl().toString() + "/plugins/servlet/mwauthredirect?target=" + HostUtils.urlEncode(appId);
        String authUrl = this.stripBaseUrl(authorisationURIGenerator.getAuthorisationURI(URI.create(url)));
        String localName = this.internalHostApplication.getName();
        String remoteName = applicationLink.getName();
        String remoteType = this.i18nResolver.getText(applicationLink.getType().getI18nKey());
        String remoteUrl = applicationLink.getDisplayUrl().toString();
        ObjectNode metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("appId", appId);
        metadata.put("localName", localName);
        metadata.put("remoteName", remoteName);
        metadata.put("remoteType", remoteType);
        metadata.put("remoteUrl", remoteUrl);
        this.notificationService.createOrUpdate(username, new NotificationBuilder().globalId(globalId).title(remoteName + " requires authentication to create notifications").itemTitle(remoteName + " requires authentication to create notifications").url(authUrl).applicationLinkId(this.internalHostApplication.getId().get()).itemUrl(authUrl).application(new MyWorkRegistrationProvider().getApplication()).entity("authentication").action("require").pinned(true).metadata(metadata).createNotification());
    }

    private String stripBaseUrl(URI uri) {
        String baseUrl;
        String url = uri.toASCIIString();
        if (url.startsWith(baseUrl = this.internalHostApplication.getBaseUrl().toASCIIString())) {
            return url.substring(baseUrl.length());
        }
        return url;
    }

    private static <F, E> Function<F, E> constant(E value) {
        return from -> value;
    }
}

