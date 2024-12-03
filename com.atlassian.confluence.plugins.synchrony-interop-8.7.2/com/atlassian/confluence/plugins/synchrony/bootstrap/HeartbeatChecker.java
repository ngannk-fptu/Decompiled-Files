/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HeartbeatChecker {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatChecker.class);
    private final SynchronyConfigurationManager configurationManager;
    private final RequestFactory<?> requestFactory;

    HeartbeatChecker(SynchronyConfigurationManager configurationManager, RequestFactory<?> requestFactory) {
        this.configurationManager = configurationManager;
        this.requestFactory = requestFactory;
    }

    boolean isSynchronyUp() {
        String synchronyHeartbeatUrl = this.createHeartbeatUrl();
        log.debug("Checking Synchrony heartbeat on: {}", (Object)synchronyHeartbeatUrl);
        try {
            return (Boolean)this.requestFactory.createRequest(Request.MethodType.GET, synchronyHeartbeatUrl).executeAndReturn(response -> {
                log.debug("Response received from Synchrony heartbeat: status {}", (Object)response.getStatusCode());
                return response.getStatusCode() == 200;
            });
        }
        catch (ResponseException e) {
            log.debug("No response from Synchrony.");
            return false;
        }
    }

    private String createHeartbeatUrl() {
        String heartBeatUrl = this.configurationManager.isUsingLocalSynchrony() ? this.configurationManager.getInternalServiceUrl().replace("v1", "heartbeat") : String.format("%s/heartbeat", this.configurationManager.getExternalBaseUrl());
        return heartBeatUrl;
    }
}

