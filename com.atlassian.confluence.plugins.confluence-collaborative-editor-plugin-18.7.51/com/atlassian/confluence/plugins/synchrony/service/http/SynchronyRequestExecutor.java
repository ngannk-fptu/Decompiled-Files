/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.events.SynchronyConnectionFailureEvent;
import com.atlassian.confluence.plugins.synchrony.events.exported.SynchronyRequestEvent;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.http.InvalidJwtTokenException;
import com.atlassian.confluence.plugins.synchrony.service.http.LockingResponseHandler;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyChangeRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyHttpClientFactory;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyLockingApiRequest;
import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyResponseHandler;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Optional;
import net.minidev.json.JSONObject;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-request-executor")
public class SynchronyRequestExecutor {
    private final CloseableHttpClient httpClient;
    private final EventPublisher eventPublisher;
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private static final Logger log = LoggerFactory.getLogger(SynchronyRequestExecutor.class);

    @Autowired
    public SynchronyRequestExecutor(SynchronyHttpClientFactory synchronyHttpClientFactory, @ComponentImport EventPublisher eventPublisher, SynchronyConfigurationManager synchronyConfigurationManager) {
        this.eventPublisher = eventPublisher;
        this.synchronyConfigurationManager = synchronyConfigurationManager;
        this.httpClient = synchronyHttpClientFactory.get();
    }

    public Either<SynchronyError, JSONObject> execute(SynchronyChangeRequest request, ContentId contentId) {
        try {
            SynchronyError error;
            log.info("Initiating request {} for {}", (Object)request.getHttpRequest(), (Object)contentId.asLong());
            Either result = (Either)this.httpClient.execute((HttpUriRequest)request.getHttpRequest(), (ResponseHandler)new SynchronyResponseHandler(contentId.asLong(), request.getData(), this.eventPublisher));
            this.eventPublisher.publish((Object)new SynchronyRequestEvent(contentId.asLong(), request, result.isRight()));
            if (result.isLeft() && (error = (SynchronyError)result.left().getOrNull()) != null && SynchronyError.Code.JWT_DECRYPTION_FAILED.equals((Object)error.getCode())) {
                this.synchronyConfigurationManager.retrievePublicKey();
                result = (Either)this.httpClient.execute((HttpUriRequest)request.getHttpRequest(), (ResponseHandler)new SynchronyResponseHandler(contentId.asLong(), request.getData(), this.eventPublisher));
            }
            return result;
        }
        catch (Exception e) {
            this.eventPublisher.publish((Object)new SynchronyConnectionFailureEvent());
            log.error("There was a problem calling Synchrony API for {}: {}", (Object)contentId.asLong(), (Object)e.getMessage());
            log.debug("", (Throwable)e);
            return Either.left((Object)SynchronyError.CONNECTION_FAILURE);
        }
    }

    public void execute(SynchronyLockingApiRequest request) {
        try {
            log.info("Initiating Locking API request: {}", (Object)request.getClass().getSimpleName());
            Optional errorCode = (Optional)this.httpClient.execute(request.getHttpRequest(), (ResponseHandler)new LockingResponseHandler());
            if (errorCode.isPresent() && SynchronyError.Code.JWT_DECRYPTION_FAILED.equals(errorCode.get())) {
                throw new InvalidJwtTokenException("Error code: " + errorCode.get());
            }
        }
        catch (Exception e) {
            this.eventPublisher.publish((Object)new SynchronyConnectionFailureEvent());
            log.error("There was a problem calling Synchrony Locking API ({}): {}", (Object)request.getClass().getSimpleName(), (Object)e.getMessage());
            log.debug("", (Throwable)e);
        }
    }

    public String getContentUrlWithStateQuery(long id) {
        return this.getContentUrl(id) + "?state-at=@head&state-format=html";
    }

    public String getContentUrl(long id) {
        String appId = this.synchronyConfigurationManager.getConfiguredAppID();
        String serviceUrl = this.synchronyConfigurationManager.getInternalServiceUrl();
        return serviceUrl + "/data/" + appId + "/confluence-" + id;
    }
}

