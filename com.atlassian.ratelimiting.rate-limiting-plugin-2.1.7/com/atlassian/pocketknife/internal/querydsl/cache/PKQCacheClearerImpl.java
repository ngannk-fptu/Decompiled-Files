/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.cache;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.pocketknife.internal.querydsl.cache.PKQCacheClearer;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PKQCacheClearerImpl
implements PKQCacheClearer {
    private static final Logger log = LoggerFactory.getLogger(PKQCacheClearerImpl.class);
    private static final String PKQDSL_REACT_TO_CLEAR_CACHE = "pkqdsl.react.to.clear.cache";
    private final EventPublisher eventPublisher;
    private final List<Runnable> cacheClearingSideEffects = new ArrayList<Runnable>();

    @Autowired
    public PKQCacheClearerImpl(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    private void postConstruction() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    @EventListener
    public void onClearCache(Object event) {
        if (this.isClearCacheEvent(event) && this.reactToClearCache()) {
            log.warn("Clearing the PKQDSL caches");
            this.clearAllCaches();
        }
    }

    private boolean reactToClearCache() {
        return Boolean.parseBoolean(System.getProperty(PKQDSL_REACT_TO_CLEAR_CACHE, "true"));
    }

    @VisibleForTesting
    boolean isClearCacheEvent(Object event) {
        return "com.atlassian.jira.event.ClearCacheEvent".equals(event.getClass().getName());
    }

    @Override
    public void registerCacheClearing(Runnable runnable) {
        this.cacheClearingSideEffects.add(Objects.requireNonNull(runnable));
    }

    @Override
    public void clearAllCaches() {
        this.cacheClearingSideEffects.forEach(Runnable::run);
    }
}

