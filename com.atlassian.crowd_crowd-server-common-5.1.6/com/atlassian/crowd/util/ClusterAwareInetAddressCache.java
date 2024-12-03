/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.service.cluster.ClusterMessageListener
 *  com.atlassian.crowd.service.cluster.ClusterMessageService
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.crowd.util;

import com.atlassian.cache.Cache;
import com.atlassian.crowd.manager.cluster.message.SingleClusterMessageListener;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.service.cluster.ClusterMessageListener;
import com.atlassian.crowd.service.cluster.ClusterMessageService;
import java.net.InetAddress;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ClusterAwareInetAddressCache {
    protected static final String CLUSTER_MESSAGE_CHANNEL = "ClusterAwareInetAddressCache";
    protected static final String CLUSTER_MESSAGE_CLEAR = "clear";
    private final Cache<String, Boolean> cache;
    private final ClusterMessageService clusterMessageService;
    private final ClusterMessageListener clusterMessageListener = new SingleClusterMessageListener("ClusterAwareInetAddressCache", "clear", this::clearLocal);

    public ClusterAwareInetAddressCache(Cache<String, Boolean> cache, ClusterMessageService clusterMessageService) {
        this.cache = cache;
        this.clusterMessageService = clusterMessageService;
    }

    @PostConstruct
    public void registerClusterListener() {
        this.clusterMessageService.registerListener(this.clusterMessageListener, CLUSTER_MESSAGE_CHANNEL);
    }

    @PreDestroy
    public void unregisterClusterListener() {
        this.clusterMessageService.unregisterListener(this.clusterMessageListener);
    }

    public void setPermitted(Application application, InetAddress address, boolean permitted) {
        this.cache.put((Object)ClusterAwareInetAddressCache.getKeyName(application, address), (Object)permitted);
    }

    @Nullable
    public Boolean getPermitted(Application application, InetAddress address) {
        return (Boolean)this.cache.get((Object)ClusterAwareInetAddressCache.getKeyName(application, address));
    }

    public void clear() {
        this.clusterMessageService.publish(CLUSTER_MESSAGE_CHANNEL, CLUSTER_MESSAGE_CLEAR);
        this.clearLocal();
    }

    private void clearLocal() {
        this.cache.removeAll();
    }

    private static String getKeyName(Application application, InetAddress address) {
        return application.getName() + "#" + address.getHostAddress();
    }
}

