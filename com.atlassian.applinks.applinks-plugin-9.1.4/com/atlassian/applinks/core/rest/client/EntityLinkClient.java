/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.rest.client;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.rest.EntityLinkResource;
import com.atlassian.applinks.core.rest.model.EntityLinkEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityLinkClient {
    private static final Logger LOG = LoggerFactory.getLogger((String)EntityLinkClient.class.getName());
    private final InternalHostApplication internalHostApplication;
    private final RestUrlBuilder restUrlBuilder;

    @Autowired
    public EntityLinkClient(InternalHostApplication internalHostApplication, RestUrlBuilder restUrlBuilder) {
        this.internalHostApplication = internalHostApplication;
        this.restUrlBuilder = restUrlBuilder;
    }

    public void createEntityLinkFrom(EntityLink entityLink, EntityType localType, String localKey) throws ReciprocalActionException, CredentialsRequiredException {
        this.createEntityLinkFrom(entityLink, localType, localKey, entityLink.getApplicationLink().createAuthenticatedRequestFactory());
    }

    public void createEntityLinkFrom(EntityLink entityLink, EntityType localType, String localKey, ApplicationLinkRequestFactory requestFactory) throws ReciprocalActionException, CredentialsRequiredException {
        EntityReference localEntity = this.internalHostApplication.toEntityReference(localKey, localType.getClass());
        EntityLinkEntity linkBack = new EntityLinkEntity(this.internalHostApplication.getId(), localKey, TypeId.getTypeId((EntityType)localType), localEntity.getName(), null, null, null, false);
        URI baseUri = RestUtil.getBaseRestUri(entityLink.getApplicationLink());
        ApplicationLinkRequest createLinkBackRequest = requestFactory.createRequest(Request.MethodType.PUT, ((EntityLinkResource)this.restUrlBuilder.getUrlFor(baseUri, EntityLinkResource.class)).createEntityLink(TypeId.getTypeId((EntityType)entityLink.getType()), entityLink.getKey(), false, linkBack).toString());
        createLinkBackRequest.setEntity((Object)linkBack);
        if (LOG.isDebugEnabled()) {
            String message = String.format("Creating Entity Link to [%s] [%s] from [%s] as [%s] ", TypeId.getTypeId((EntityType)localType), localKey, baseUri, entityLink);
            LOG.debug(message);
        }
        try {
            createLinkBackRequest.execute((ResponseHandler)new ResponseHandler<Response>(){

                public void handle(Response createLinkBackResponse) throws ResponseException {
                    if (createLinkBackResponse.getStatusCode() != 201) {
                        throw new ResponseException(String.format("Received %s - %s", createLinkBackResponse.getStatusCode(), createLinkBackResponse.getStatusText()));
                    }
                }
            });
        }
        catch (ResponseException e) {
            throw new ReciprocalActionException((Throwable)e);
        }
    }

    public void deleteEntityLinkFrom(EntityLink remoteEntity, EntityType localType, String localKey) throws ReciprocalActionException, CredentialsRequiredException {
        ApplicationLink applicationLink = remoteEntity.getApplicationLink();
        URI baseUri = RestUtil.getBaseRestUri(applicationLink);
        String url = ((EntityLinkResource)this.restUrlBuilder.getUrlFor(baseUri, EntityLinkResource.class)).deleteApplicationEntityLink(TypeId.getTypeId((EntityType)remoteEntity.getType()), remoteEntity.getKey(), TypeId.getTypeId((EntityType)localType), localKey, this.internalHostApplication.getId().get(), true).toString();
        StringBuilder deletionUri = new StringBuilder(url);
        deletionUri.append(String.format("?typeId=%s&key=%s&applicationId=%s", TypeId.getTypeId((EntityType)localType), localKey, this.internalHostApplication.getId()));
        ApplicationLinkRequest deleteReciprocalLinkRequest = applicationLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.DELETE, deletionUri.toString());
        if (LOG.isDebugEnabled()) {
            String message = String.format("Deleting remote Entity Link for [%s] [%s] on [%s] was [%s] ", TypeId.getTypeId((EntityType)localType), localKey, baseUri, remoteEntity);
            LOG.debug(message);
        }
        try {
            deleteReciprocalLinkRequest.execute((ResponseHandler)new ResponseHandler<Response>(){

                public void handle(Response response) throws ResponseException {
                    if (response.getStatusCode() != 200) {
                        throw new ResponseException(String.format("Received %s - %s", response.getStatusCode(), response.getStatusText()));
                    }
                }
            });
        }
        catch (ResponseException e) {
            throw new ReciprocalActionException((Throwable)e);
        }
    }
}

