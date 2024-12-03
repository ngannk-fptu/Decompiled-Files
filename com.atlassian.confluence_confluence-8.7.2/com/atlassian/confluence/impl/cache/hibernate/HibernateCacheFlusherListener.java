/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache.hibernate;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.cache.ClusterCacheFlushEvent;
import com.atlassian.event.api.EventListener;
import java.util.Objects;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateCacheFlusherListener {
    private static final Logger log = LoggerFactory.getLogger(HibernateCacheFlusherListener.class);
    private final SessionFactory sessionFactory;

    public HibernateCacheFlusherListener(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
    }

    @EventListener
    public void onClusterCacheFlush(ClusterEventWrapper wrapper) {
        if (wrapper.getEvent() instanceof ClusterCacheFlushEvent) {
            log.warn("Flush local Hibernate query cache at the request of {}", wrapper.getSource());
            this.sessionFactory.getCache().evictQueryRegions();
        }
    }

    @EventListener
    public void onCacheFlush(ClusterCacheFlushEvent event) {
        log.warn("Flushing all Hibernate cache regions");
        this.sessionFactory.getCache().evictAllRegions();
    }
}

