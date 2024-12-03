/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.Request$Builder
 *  com.atlassian.httpclient.api.Response
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.remoteservice;

import com.atlassian.confluence.plugins.mobile.exception.MobilePushNotificationException;
import com.atlassian.confluence.plugins.mobile.helper.MobileConstant;
import com.atlassian.confluence.plugins.mobile.helper.PushNotificationHelper;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationContent;
import com.atlassian.confluence.plugins.mobile.notification.PushNotificationResult;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.Response;
import com.atlassian.json.jsonorg.JSONObject;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationClient {
    private Logger LOG = LoggerFactory.getLogger(PushNotificationClient.class);
    private static final String NOTIFICATION_REST_URL = MobileConstant.PushNotification.MOBILE_SERVER_PUSH_NOTIFICATION_SERVICE + "/rest/product/confluence";
    private final PushNotificationHelper pushNotificationHelper;
    private final ObjectMapper objectMapper;

    @Autowired
    public PushNotificationClient(PushNotificationHelper pushNotificationHelper) {
        this.pushNotificationHelper = pushNotificationHelper;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }

    public String updatePushEndpoint(@Nonnull String appName, @Nonnull String token, @Nonnull String oldEndpoint) throws MobilePushNotificationException {
        try {
            String restUrl = NOTIFICATION_REST_URL + "/app/" + appName + "/notification/endpoint";
            Response response = (Response)((Request.Builder)this.pushNotificationHelper.getRequestBuilder(restUrl).setEntity(this.objectMapper.writeValueAsString((Object)ImmutableMap.of((Object)"endpoint", (Object)oldEndpoint, (Object)"token", (Object)token)))).post().get();
            if (response.isSuccessful()) {
                String endpoint = new JSONObject(response.getEntity()).getString("endpoint");
                this.LOG.debug("Get sns endpoint successful.");
                return endpoint;
            }
            throw new MobilePushNotificationException(String.format("status code: %s and entity: %s", response.getStatusCode(), response.getEntity()));
        }
        catch (Exception e) {
            String errorMessage = "Get sns endpoint is unsuccessful with error message: " + e.getMessage();
            throw new MobilePushNotificationException(errorMessage, e);
        }
    }

    public void removePushEndpoint(String appName, String endpointId) throws MobilePushNotificationException {
        try {
            String restUrl = NOTIFICATION_REST_URL + "/app/" + appName + "/notification/endpoint/" + endpointId;
            Response response = (Response)this.pushNotificationHelper.getRequestBuilder(restUrl).delete().get();
            if (!response.isNoContent()) {
                throw new MobilePushNotificationException(String.format("status code: %s and entity: %s", response.getStatusCode(), response.getEntity()));
            }
            this.LOG.debug("Remove sns endpoint successful.");
        }
        catch (Exception e) {
            String errorMessage = "Remove sns endpoint is unsuccessful with error message: " + e.getMessage();
            throw new MobilePushNotificationException(errorMessage, e);
        }
    }

    public PushNotificationResult push(List<PushNotificationContent> contents) throws MobilePushNotificationException {
        try {
            String restUrl = NOTIFICATION_REST_URL + "/notification";
            Response response = (Response)((Request.Builder)this.pushNotificationHelper.getRequestBuilder(restUrl).setEntity(this.objectMapper.writeValueAsString(contents))).post().get();
            if (response.isSuccessful()) {
                return (PushNotificationResult)this.objectMapper.readValue(response.getEntity(), PushNotificationResult.class);
            }
            throw new MobilePushNotificationException(String.format("status code: %s and entity: %s", response.getStatusCode(), response.getEntity()));
        }
        catch (Exception e) {
            String errorMessage = "Send push is unsuccessful with error message: " + e.getMessage();
            throw new MobilePushNotificationException(errorMessage, e);
        }
    }
}

