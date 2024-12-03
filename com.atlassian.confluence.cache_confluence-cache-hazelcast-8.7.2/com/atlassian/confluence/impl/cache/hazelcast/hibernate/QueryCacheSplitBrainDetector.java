/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.hazelcast.core.Cluster
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MembershipAdapter
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache.hazelcast.hibernate;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QueryCacheSplitBrainDetector {
    private static final Logger log = LoggerFactory.getLogger(QueryCacheSplitBrainDetector.class);
    private static final String NODE_STARTED = "confluence.node.tenant.arrived";
    private final ClusterManager clusterManager;
    private final HazelcastInstance hazelcastInstance;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final SessionFactory sessionFactory;
    private volatile String clusterMembershipListenerId;

    QueryCacheSplitBrainDetector(ClusterManager clusterManager, HazelcastInstance hazelcastInstance, EventListenerRegistrar eventListenerRegistrar, SessionFactory sessionFactory) {
        this.clusterManager = clusterManager;
        this.hazelcastInstance = hazelcastInstance;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.sessionFactory = sessionFactory;
    }

    @PostConstruct
    void registerTenancyListener() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PostConstruct
    void initClusterListener() {
        if (this.clusterManager.isClustered()) {
            this.clusterMembershipListenerId = this.getCluster().addMembershipListener((MembershipListener)new ClusterListener());
        }
    }

    @PreDestroy
    void unregisterClusterListener() {
        if (this.clusterMembershipListenerId != null) {
            this.getCluster().removeMembershipListener(this.clusterMembershipListenerId);
        }
    }

    @PreDestroy
    void unregisterTenancyListener() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onTenantArriveEvent(TenantArrivedEvent event) {
        if (this.clusterManager.isClustered()) {
            this.getCluster().getLocalMember().setBooleanAttribute(NODE_STARTED, true);
        }
    }

    private Cluster getCluster() {
        return this.hazelcastInstance.getCluster();
    }

    private class ClusterListener
    extends MembershipAdapter {
        private ClusterListener() {
        }

        public void memberAdded(MembershipEvent membershipEvent) {
            Member joiningMember = membershipEvent.getMember();
            Member currentMember = QueryCacheSplitBrainDetector.this.getCluster().getLocalMember();
            if (Boolean.TRUE.equals(currentMember.getBooleanAttribute(QueryCacheSplitBrainDetector.NODE_STARTED)) && Boolean.TRUE.equals(joiningMember.getBooleanAttribute(QueryCacheSplitBrainDetector.NODE_STARTED))) {
                log.info("Split brain detected, flushing contents of Hibernate query cache");
                QueryCacheSplitBrainDetector.this.sessionFactory.getCache().evictQueryRegions();
            }
        }
    }
}

