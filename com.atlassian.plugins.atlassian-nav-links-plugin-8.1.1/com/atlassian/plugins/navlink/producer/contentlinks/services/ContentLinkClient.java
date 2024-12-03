/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.IdentifiableType
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.type.CollectionType
 *  org.codehaus.jackson.map.type.TypeFactory
 *  org.codehaus.jackson.type.JavaType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.contentlinks.services;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.spi.application.IdentifiableType;
import com.atlassian.plugins.navlink.consumer.http.UserAgentProperty;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinksEnvelope;
import com.atlassian.plugins.navlink.producer.contentlinks.services.ContentLinkCapability;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentLinkClient {
    private static final Logger log = LoggerFactory.getLogger(ContentLinkClient.class);
    private UserAgentProperty userAgentProperty;

    public ContentLinkClient(@Nonnull UserAgentProperty userAgentProperty) {
        this.userAgentProperty = (UserAgentProperty)Preconditions.checkNotNull((Object)userAgentProperty);
    }

    @Nonnull
    public List<ContentLinkEntity> getContentLinks(@Nonnull ContentLinkCapability contentLink) throws CredentialsRequiredException {
        ApplicationLink applicationLink = contentLink.getEntityLink().getApplicationLink();
        EntityLink entityLink = contentLink.getEntityLink();
        ApplicationLinkRequestFactory authenticatedRequestFactory = applicationLink.createAuthenticatedRequestFactory();
        try {
            StringBuilder url = new StringBuilder(contentLink.getContentLinkUrl());
            if (!contentLink.getContentLinkUrl().endsWith("/")) {
                url.append("/");
            }
            url.append(entityLink.getKey());
            EntityType type = entityLink.getType();
            if (type instanceof IdentifiableType) {
                url.append("?entityType=").append(((IdentifiableType)type).getId());
            }
            ApplicationLinkRequest request = authenticatedRequestFactory.createRequest(Request.MethodType.GET, url.toString());
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("User-Agent", this.userAgentProperty.get());
            return (List)request.execute((ApplicationLinkResponseHandler)new ResponseHandler(applicationLink));
        }
        catch (ResponseException e) {
            log.error("Error processing response to project shortcuts request: " + e.getMessage());
            log.debug("Stacktrace: ", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private static class ResponseHandler
    implements ApplicationLinkResponseHandler<List<ContentLinkEntity>> {
        private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
        private final ApplicationLink applicationLink;

        public ResponseHandler(ApplicationLink applicationLink) {
            this.applicationLink = applicationLink;
        }

        public List<ContentLinkEntity> credentialsRequired(Response response) throws ResponseException {
            log.debug("Project Shortcuts API is only supported on trusted apps and 2LO connections. Skipping link for: {}", (Object)this.applicationLink);
            return Collections.emptyList();
        }

        public List<ContentLinkEntity> handle(Response response) throws ResponseException {
            if (response.getStatusCode() != 200) {
                log.debug("Got non-successful response \"{}\" from project shortcuts request from: {}", (Object)response.getStatusText(), (Object)this.applicationLink);
                return Collections.emptyList();
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String responseBody = response.getResponseBodyAsString();
            try {
                if (responseBody.startsWith("[")) {
                    CollectionType expectedType = TypeFactory.defaultInstance().constructCollectionType(List.class, ContentLinkEntity.class);
                    return (List)mapper.readValue(responseBody, (JavaType)expectedType);
                }
                ContentLinksEnvelope envelope = (ContentLinksEnvelope)mapper.readValue(responseBody, ContentLinksEnvelope.class);
                return envelope.getContentLinks();
            }
            catch (IOException e) {
                throw new ResponseException("Could not parse response: " + responseBody, (Throwable)e);
            }
        }
    }
}

