/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.google.common.collect.Iterables
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.rest.client;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.model.ReferenceEntityList;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityRetriever {
    private final InternalTypeAccessor typeAccessor;

    @Autowired
    public EntityRetriever(InternalTypeAccessor typeAccessor) {
        this.typeAccessor = typeAccessor;
    }

    public Iterable<EntityReference> getEntities(ApplicationLink link) throws ResponseException, CredentialsRequiredException {
        return this.getEntities(link.createAuthenticatedRequestFactory());
    }

    private Iterable<EntityReference> getEntities(ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, ResponseException {
        ApplicationLinkRequest req = requestFactory.createRequest(Request.MethodType.GET, "/rest/applinks/1.0/entities");
        final ArrayList<EntityReference> entities = new ArrayList<EntityReference>();
        req.execute((ResponseHandler)new ResponseHandler<Response>(){

            public void handle(Response response) throws ResponseException {
                if (response.getStatusCode() != 200) {
                    throw new ResponseException(String.format("Failed to retrieve entity list, received %s response: %s", response.getStatusCode(), response.getStatusText()));
                }
                Iterables.addAll((Collection)entities, ((ReferenceEntityList)response.getEntity(ReferenceEntityList.class)).getEntities(EntityRetriever.this.typeAccessor));
            }
        });
        return entities;
    }

    public Iterable<EntityReference> getEntitiesForAnonymousAccess(ApplicationLink link) throws ResponseException {
        try {
            return this.getEntities(link.createAuthenticatedRequestFactory(Anonymous.class));
        }
        catch (CredentialsRequiredException e) {
            throw new RuntimeException(CredentialsRequiredException.class.getName() + " should never be thrown on anonymous access.", e);
        }
    }
}

