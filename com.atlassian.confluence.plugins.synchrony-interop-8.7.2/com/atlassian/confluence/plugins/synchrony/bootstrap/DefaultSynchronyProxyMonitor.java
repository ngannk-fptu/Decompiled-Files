/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.PreDestroy
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProxyMonitor;
import com.atlassian.confluence.plugins.synchrony.bootstrap.SynchronyScheduledExecutorServiceProvider;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSynchronyProxyMonitor
implements SynchronyProxyMonitor {
    private static final Logger log = LoggerFactory.getLogger(DefaultSynchronyProxyMonitor.class);
    static final Duration SYNCHRONY_PROXY_HEALTHCHECK_INITIAL_DELAY = Duration.ofSeconds(15L);
    static final Duration SYNCHRONY_PROXY_HEALTHCHECK_INTERVAL = Duration.ofSeconds(Integer.getInteger("confluence.synchrony.proxy.healthcheck.interval.seconds", 30).intValue());
    private final RequestFactory<?> requestFactory;
    private final ScheduledExecutorService executorService;
    private final String proxyCheckUrl;
    private volatile boolean isSynchronyProxyUp;

    @Autowired
    public DefaultSynchronyProxyMonitor(SynchronyScheduledExecutorServiceProvider executorServiceProvider, RequestFactory<?> requestFactory, @ComponentImport TomcatConfigHelper tomcatConfigHelper) {
        this(executorServiceProvider.getExecutorService(), requestFactory, tomcatConfigHelper);
        log.info("synchrony-proxy healthcheck url: {}", (Object)this.proxyCheckUrl);
    }

    DefaultSynchronyProxyMonitor(ScheduledExecutorService executorService, RequestFactory<?> requestFactory, TomcatConfigHelper tomcatConfigHelper) {
        this.executorService = executorService;
        this.requestFactory = requestFactory;
        this.proxyCheckUrl = DefaultSynchronyProxyMonitor.getProxyUri(tomcatConfigHelper);
    }

    private static String getProxyUri(TomcatConfigHelper tomcatConfigHelper) {
        return UriBuilder.fromUri((String)"http://localhost").host(DefaultSynchronyProxyMonitor.getHost()).port(NumberUtils.toInt((String)tomcatConfigHelper.getConnectorPort().orElse(null))).path("synchrony-proxy").path("healthcheck").build(new Object[0]).toASCIIString();
    }

    @Override
    public void startHealthcheck() {
        this.pollHealthcheck();
        if (!this.isSynchronyProxyUp) {
            log.warn("The synchrony-proxy has not been started yet. Another healthcheck will happen in {} seconds.", (Object)SYNCHRONY_PROXY_HEALTHCHECK_INTERVAL);
        }
        this.executorService.scheduleWithFixedDelay(this::pollHealthcheck, SYNCHRONY_PROXY_HEALTHCHECK_INITIAL_DELAY.toMillis(), SYNCHRONY_PROXY_HEALTHCHECK_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static String getHost() {
        String host = System.getProperties().getProperty(SynchronyEnv.Host.getEnvName());
        if (host == null) {
            host = (String)SynchronyEnv.getDefaultProperties().get(SynchronyEnv.Host.getEnvName());
        }
        return host != null ? host : "127.0.0.1";
    }

    private void pollHealthcheck() {
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, this.proxyCheckUrl);
        try {
            this.isSynchronyProxyUp = (Boolean)request.executeAndReturn(response -> {
                log.debug("synchrony-proxy healthcheck status code: {}", (Object)response.getStatusCode());
                return response.getStatusCode() == 200;
            });
        }
        catch (ResponseException | RuntimeException e) {
            log.warn("Could not ping the synchrony-proxy [{}]: {}", (Object)this.proxyCheckUrl, (Object)e.getMessage());
            log.debug("", e);
            this.isSynchronyProxyUp = false;
        }
    }

    @Override
    public boolean isSynchronyProxyUp() {
        return this.isSynchronyProxyUp;
    }

    @PreDestroy
    void dispose() {
        this.executorService.shutdownNow();
    }
}

