/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.apache.http.client.HttpResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.plugins.navlink.consumer.menu.client.capabilities;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.http.HttpRequestFactory;
import com.atlassian.plugins.navlink.consumer.menu.client.capabilities.CapabilitiesClient;
import com.atlassian.plugins.navlink.consumer.menu.client.capabilities.CapabilitiesResponseHandler;
import com.atlassian.plugins.navlink.consumer.menu.client.capabilities.CapabilitiesRestResourceUrlFactory;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilitiesBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class RestCapabilitiesClient
implements CapabilitiesClient,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RestCapabilitiesClient.class);
    private static final long KNOWN_NO_CAPABILITIES_CACHE_TIME_MILLIS = 86400000L;
    private final HttpRequestFactory requestFactory;
    private final ListeningExecutorService executor;

    public RestCapabilitiesClient(HttpRequestFactory requestFactory) {
        this(requestFactory, MoreExecutors.listeningDecorator((ExecutorService)Executors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory((String)"NavLink RestCapabilitiesClient"))));
    }

    @VisibleForTesting
    RestCapabilitiesClient(HttpRequestFactory requestFactory, ListeningExecutorService executor) {
        this.requestFactory = requestFactory;
        this.executor = executor;
    }

    private static boolean isNotFoundException(Exception e) {
        return e instanceof HttpResponseException && ((HttpResponseException)e).getStatusCode() == 404;
    }

    private static String getTypeIdString(ApplicationType applicationType) {
        try {
            return TypeId.getTypeId((ApplicationType)applicationType).get();
        }
        catch (IllegalStateException notIdentifiableType) {
            return "";
        }
    }

    @Override
    public ListenableFuture<ExpiringValue<RemoteApplicationWithCapabilities>> getCapabilities(ReadOnlyApplicationLink applicationLink) {
        String requestUrl = CapabilitiesRestResourceUrlFactory.createRequestUrl(applicationLink);
        logger.debug("Scheduling request for capabilities from '{}' (application link id: {})", (Object)requestUrl, (Object)applicationLink.getId());
        return this.scheduleRequest(requestUrl, applicationLink);
    }

    public void destroy() throws Exception {
        this.executor.shutdownNow();
    }

    private ListenableFuture<ExpiringValue<RemoteApplicationWithCapabilities>> scheduleRequest(final String requestUrl, final ReadOnlyApplicationLink applicationLink) {
        return this.executor.submit((Callable)new Callable<ExpiringValue<RemoteApplicationWithCapabilities>>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public ExpiringValue<RemoteApplicationWithCapabilities> call() throws Exception {
                long before = System.currentTimeMillis();
                try {
                    ExpiringValue<RemoteApplicationWithCapabilities> expiringValue = RestCapabilitiesClient.this.submitRequest(requestUrl, applicationLink);
                    return expiringValue;
                }
                finally {
                    logger.debug("Capabilities from '{}' have been fetched in {} ms", (Object)requestUrl, (Object)(System.currentTimeMillis() - before));
                }
            }
        });
    }

    @VisibleForTesting
    ExpiringValue<RemoteApplicationWithCapabilities> submitRequest(String requestUrl, ReadOnlyApplicationLink applicationLink) {
        CapabilitiesResponseHandler responseHandler = new CapabilitiesResponseHandler(applicationLink);
        try {
            return this.requestFactory.executeGetRequest(requestUrl, responseHandler);
        }
        catch (Exception e) {
            return this.handleException(requestUrl, applicationLink, e);
        }
    }

    private ExpiringValue<RemoteApplicationWithCapabilities> handleException(String requestUrl, ReadOnlyApplicationLink applicationLink, Exception e) {
        if (RestCapabilitiesClient.isNotFoundException(e)) {
            RemoteApplicationWithCapabilities cachedNoCapabilitiesResult = new RemoteApplicationWithCapabilitiesBuilder().setApplicationLinkId(applicationLink.getId().toString()).setType(RestCapabilitiesClient.getTypeIdString(applicationLink.getType())).setSelfUrl(requestUrl).setBuildDateTime(null).build();
            long expiredAfter = System.currentTimeMillis() + 86400000L;
            return new ExpiringValue((Object)cachedNoCapabilitiesResult, expiredAfter, expiredAfter);
        }
        logger.info("Failed to request capabilities from '{}': {}", (Object)requestUrl, (Object)e.getMessage());
        logger.debug("Stacktrace: ", (Throwable)e);
        return ExpiringValue.expiredNullValue();
    }
}

