/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.httpclient.api.factory;

import com.atlassian.httpclient.api.HostResolver;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.factory.ProxyOptions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpClientOptions {
    public static final String OPTION_PROPERTY_PREFIX = "com.atlassian.httpclient.options";
    public static final String OPTION_THREAD_WORK_QUEUE_LIMIT = "com.atlassian.httpclient.options.threadWorkQueueLimit";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private List<String> blacklistedAddresses;
    private String[] supportedProtocols;
    private String threadPrefix = "httpclient";
    private boolean ignoreCookies = false;
    private int ioThreadCount = Integer.getInteger("com.atlassian.httpclient.options.ioThreadCount", 10);
    private long ioSelectInterval = Integer.getInteger("com.atlassian.httpclient.options.ioSelectInterval", 1000).intValue();
    private int threadWorkQueueLimit = Integer.getInteger("com.atlassian.httpclient.options.threadWorkQueueLimit", 256);
    private long connectionTimeout = 5000L;
    private long socketTimeout = 20000L;
    private long requestTimeout = 90000L;
    private int maxTotalConnections = 20;
    private int maxConnectionsPerHost = 20;
    private long connectionPoolTimeToLive = 30000L;
    private long maxCacheObjectSize = 102400L;
    private int maxCacheEntries = 100;
    private long maxEntitySize = 0x6400000L;
    private long leaseTimeout = 600000L;
    private int maxCallbackThreadPoolSize = 16;
    private boolean trustSelfSignedCertificates = false;
    private Consumer<Request> requestPreparer = request -> {};
    private String userAgent = "Default";
    private ExecutorService callbackExecutor;
    private ProxyOptions proxyOptions = ProxyOptions.ProxyOptionsBuilder.create().build();
    private HostResolver hostResolver;
    private int maxHeaderLineSize = 8192;

    public boolean getIgnoreCookies() {
        return this.ignoreCookies;
    }

    public String[] getSupportedProtocols() {
        return this.supportedProtocols;
    }

    public void setIgnoreCookies(boolean ignoreCookies) {
        this.ignoreCookies = ignoreCookies;
    }

    public int getIoThreadCount() {
        return this.ioThreadCount;
    }

    public void setIoThreadCount(int ioThreadCount) {
        this.ioThreadCount = ioThreadCount;
    }

    public long getIoSelectInterval() {
        return this.ioSelectInterval;
    }

    public void setIoSelectInterval(int ioSelectInterval, TimeUnit timeUnit) {
        this.ioSelectInterval = timeUnit.toMillis(ioSelectInterval);
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout, TimeUnit timeUnit) {
        this.connectionTimeout = timeUnit.toMillis(connectionTimeout);
    }

    public long getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout, TimeUnit timeUnit) {
        this.socketTimeout = timeUnit.toMillis(socketTimeout);
    }

    public long getRequestTimeout() {
        return this.requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout, TimeUnit timeUnit) {
        this.requestTimeout = timeUnit.toMillis(requestTimeout);
    }

    public void setSupportedProtocols(String ... supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getThreadPrefix() {
        return this.threadPrefix;
    }

    public void setBlacklistedAddresses(@Nonnull List<String> blacklistedAddresses) {
        this.blacklistedAddresses = Collections.unmodifiableList(blacklistedAddresses);
    }

    @Nonnull
    public List<String> getBlacklistedAddresses() {
        if (this.blacklistedAddresses == null) {
            return ImmutableList.of();
        }
        return this.blacklistedAddresses;
    }

    public void setThreadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }

    public long getConnectionPoolTimeToLive() {
        return this.connectionPoolTimeToLive;
    }

    public void setConnectionPoolTimeToLive(int connectionPoolTimeToLive, TimeUnit timeUnit) {
        this.connectionPoolTimeToLive = timeUnit.toMillis(connectionPoolTimeToLive);
    }

    public int getMaxTotalConnections() {
        return this.maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public int getMaxConnectionsPerHost() {
        return this.maxConnectionsPerHost;
    }

    public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
        this.maxConnectionsPerHost = maxConnectionsPerHost;
    }

    public long getMaxCacheObjectSize() {
        return this.maxCacheObjectSize;
    }

    public void setMaxCacheObjectSize(long maxCacheObjectSize) {
        this.maxCacheObjectSize = maxCacheObjectSize;
    }

    public int getMaxCacheEntries() {
        return this.maxCacheEntries;
    }

    public void setMaxCacheEntries(int maxCacheEntries) {
        this.maxCacheEntries = maxCacheEntries;
    }

    public Consumer<Request> getRequestPreparer() {
        return this.requestPreparer;
    }

    public void setRequestPreparer(Consumer<Request> requestPreparer) {
        this.requestPreparer = requestPreparer;
    }

    public long getMaxEntitySize() {
        return this.maxEntitySize;
    }

    public long getLeaseTimeout() {
        return this.leaseTimeout;
    }

    public void setLeaseTimeout(long leaseTimeout) {
        this.leaseTimeout = leaseTimeout;
    }

    public void setMaxEntitySize(long maxEntitySize) {
        this.maxEntitySize = maxEntitySize;
    }

    public int getMaxCallbackThreadPoolSize() {
        return this.maxCallbackThreadPoolSize;
    }

    public void setMaxCallbackThreadPoolSize(int maxCallbackThreadPoolSize) {
        this.maxCallbackThreadPoolSize = maxCallbackThreadPoolSize;
    }

    public void setCallbackExecutor(ExecutorService callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
    }

    public ExecutorService getCallbackExecutor() {
        return this.callbackExecutor != null ? this.callbackExecutor : this.defaultCallbackExecutor();
    }

    private ExecutorService defaultCallbackExecutor() {
        ThreadFactory threadFactory = ThreadFactories.namedThreadFactory((String)(this.getThreadPrefix() + "-callbacks"), (ThreadFactories.Type)ThreadFactories.Type.DAEMON);
        return new ThreadPoolExecutor(0, this.getMaxCallbackThreadPoolSize(), 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(this.threadWorkQueueLimit), threadFactory, (r, e) -> {
            this.log.warn("Exceeded the limit of requests waiting for execution.  Increase the value of the system property {} to prevent these situations in the future. Current value of {} = {}.", new Object[]{OPTION_THREAD_WORK_QUEUE_LIMIT, OPTION_THREAD_WORK_QUEUE_LIMIT, this.threadWorkQueueLimit});
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        });
    }

    public void setTrustSelfSignedCertificates(boolean trustSelfSignedCertificates) {
        this.trustSelfSignedCertificates = trustSelfSignedCertificates;
    }

    public boolean trustSelfSignedCertificates() {
        return this.trustSelfSignedCertificates;
    }

    public void setProxyOptions(@Nonnull ProxyOptions proxyOptions) {
        Preconditions.checkNotNull((Object)proxyOptions, (Object)"Proxy options cannot be null");
        this.proxyOptions = proxyOptions;
    }

    public ProxyOptions getProxyOptions() {
        return this.proxyOptions;
    }

    public int getThreadWorkQueueLimit() {
        return this.threadWorkQueueLimit;
    }

    public void setThreadWorkQueueLimit(int threadWorkQueueLimit) {
        this.threadWorkQueueLimit = threadWorkQueueLimit;
    }

    public int getMaxHeaderLineSize() {
        return this.maxHeaderLineSize;
    }

    public void setMaxHeaderLineSize(int maxHeaderLineSize) {
        this.maxHeaderLineSize = maxHeaderLineSize;
    }
}

