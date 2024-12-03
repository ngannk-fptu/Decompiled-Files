/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.rest.client;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.v1.rest.ApplicationLinkResource;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLinkClient {
    private final InternalHostApplication internalHostApplication;
    private final RestUrlBuilder restUrlBuilder;

    @Autowired
    public ApplicationLinkClient(InternalHostApplication internalHostApplication, RestUrlBuilder restUrlBuilder) {
        this.internalHostApplication = internalHostApplication;
        this.restUrlBuilder = restUrlBuilder;
    }

    public void deleteReciprocalLinkFrom(ApplicationLink link) throws ReciprocalActionException, CredentialsRequiredException {
        String url;
        URI baseUri = RestUtil.getBaseRestUri(link);
        try {
            url = ((ApplicationLinkResource)this.restUrlBuilder.getUrlFor(baseUri, ApplicationLinkResource.class)).deleteApplicationLink(this.internalHostApplication.getId().get(), false).toString();
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
        ApplicationLinkRequest deleteReciprocalLinkRequest = link.createAuthenticatedRequestFactory().createRequest(Request.MethodType.DELETE, url);
        try {
            boolean credentialsRequired = (Boolean)deleteReciprocalLinkRequest.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Boolean>(){

                public Boolean handle(Response response) throws ResponseException {
                    if (response.getStatusCode() == 200) {
                        return false;
                    }
                    throw new ResponseException(String.format("Received %s - %s", response.getStatusCode(), response.getStatusText()));
                }

                public Boolean credentialsRequired(Response response) throws ResponseException {
                    return true;
                }
            });
            if (credentialsRequired) {
                throw new CredentialsRequiredException((AuthorisationURIGenerator)link.createAuthenticatedRequestFactory(), "Authentication not attempted as credentials are required.");
            }
        }
        catch (ResponseException e) {
            throw new ReciprocalActionException((Throwable)e);
        }
    }
}

