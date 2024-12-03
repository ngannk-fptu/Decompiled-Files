/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.plugins.navlink.consumer.menu.client.navigation;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.http.HttpRequestFactory;
import com.atlassian.plugins.navlink.consumer.menu.client.navigation.NavigationClient;
import com.atlassian.plugins.navlink.consumer.menu.client.navigation.NavigationLinkResponseHandler;
import com.atlassian.plugins.navlink.consumer.menu.client.navigation.NavigationRestResourceUrlFactory;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.navigation.ApplicationNavigationLinks;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class RestNavigationClient
implements NavigationClient,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RestNavigationClient.class);
    private final HttpRequestFactory httpRequestFactory;
    private final ListeningExecutorService executor;

    public RestNavigationClient(HttpRequestFactory httpRequestFactory) {
        this(httpRequestFactory, MoreExecutors.listeningDecorator((ExecutorService)Executors.newSingleThreadExecutor()));
    }

    @VisibleForTesting
    RestNavigationClient(HttpRequestFactory httpRequestFactory, ListeningExecutorService executor) {
        this.httpRequestFactory = httpRequestFactory;
        this.executor = executor;
    }

    @Override
    public ListenableFuture<ExpiringValue<ApplicationNavigationLinks>> getNavigationLinks(RemoteApplicationWithCapabilities application, Locale locale) {
        String requestUrl = NavigationRestResourceUrlFactory.createRequestUrl(application, locale);
        if (requestUrl == null) {
            logger.debug("Remote application with link id {} doesn't support the navigation capability. Skipping ...", (Object)application.getApplicationLinkId());
            return Futures.immediateFuture((Object)ExpiringValue.expiredNullValue());
        }
        logger.debug("Scheduling request for navigation links from '{}' (application link id {})", (Object)requestUrl, (Object)application.getApplicationLinkId());
        return this.scheduleRequest(requestUrl, new NavigationLinkResponseHandler(application, locale));
    }

    public void destroy() throws Exception {
        this.executor.shutdownNow();
    }

    private ListenableFuture<ExpiringValue<ApplicationNavigationLinks>> scheduleRequest(final String requestUrl, final NavigationLinkResponseHandler responseHandler) {
        return this.executor.submit((Callable)new Callable<ExpiringValue<ApplicationNavigationLinks>>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public ExpiringValue<ApplicationNavigationLinks> call() throws Exception {
                long before = System.currentTimeMillis();
                try {
                    ExpiringValue<ApplicationNavigationLinks> expiringValue = RestNavigationClient.this.submitRequest(requestUrl, responseHandler);
                    return expiringValue;
                }
                finally {
                    logger.debug("Navigation links from '{}' have been fetched in {} ms.", (Object)requestUrl, (Object)(System.currentTimeMillis() - before));
                }
            }
        });
    }

    @VisibleForTesting
    ExpiringValue<ApplicationNavigationLinks> submitRequest(String requestUrl, NavigationLinkResponseHandler responseHandler) {
        try {
            return this.httpRequestFactory.executeGetRequest(requestUrl, responseHandler);
        }
        catch (Exception e) {
            return this.handleException(requestUrl, e);
        }
    }

    private ExpiringValue<ApplicationNavigationLinks> handleException(String requestUrl, Exception e) {
        logger.info("Failed to request navigation links from '{}': {}", (Object)requestUrl, (Object)e.getMessage());
        logger.debug("Stacktrace: ", (Throwable)e);
        return ExpiringValue.expiredNullValue();
    }
}

