/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.HttpClient
 *  com.atlassian.httpclient.api.factory.HttpClientFactory
 *  com.atlassian.httpclient.api.factory.HttpClientOptions
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.remoteservice;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientFactory;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileHttpClient
implements DisposableBean {
    private Logger LOG = LoggerFactory.getLogger(MobileHttpClient.class);
    private static final String PUSH_NOTIFICATION_THREAD_PREFIX = "push-notification-http-client";
    private HttpClientFactory clientFactory;
    private HttpClient mobileClient;

    @Autowired
    public MobileHttpClient(@ComponentImport HttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        HttpClientOptions options = new HttpClientOptions();
        options.setUserAgent("AtlassianMobileApp");
        options.setThreadPrefix(PUSH_NOTIFICATION_THREAD_PREFIX);
        this.mobileClient = clientFactory.create(options);
    }

    public HttpClient getInstance() {
        return this.mobileClient;
    }

    public void destroy() {
        try {
            this.clientFactory.dispose(this.mobileClient);
        }
        catch (Exception e) {
            this.LOG.warn("Could not dispose of HttpClient", (Throwable)e);
        }
    }
}

