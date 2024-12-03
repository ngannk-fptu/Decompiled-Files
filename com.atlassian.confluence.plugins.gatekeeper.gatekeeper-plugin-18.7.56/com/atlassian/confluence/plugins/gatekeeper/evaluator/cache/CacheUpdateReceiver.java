/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.DistributedQueue;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheFactory;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.event.EventCategory;
import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdateReceiver
implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CacheUpdateReceiver.class);
    private final LinkedBlockingQueue<TinyEvent> localEventQueue;
    private final DistributedQueue<TinyEvent> distributedTinyEventQueue;
    private final EvaluatorCacheFactory evaluatorCacheFactory;
    private final EvaluatorCacheHolder evaluatorCacheHolder;
    private final ClusterManager clusterManager;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private String nodeName;

    public CacheUpdateReceiver(EvaluatorCacheFactory evaluatorCacheFactory, EvaluatorCacheHolder evaluatorCacheHolder, DistributedQueue<TinyEvent> distributedQueue, ClusterManager clusterManager) {
        this.evaluatorCacheHolder = evaluatorCacheHolder;
        this.evaluatorCacheFactory = evaluatorCacheFactory;
        this.clusterManager = clusterManager;
        this.distributedTinyEventQueue = distributedQueue;
        this.localEventQueue = new LinkedBlockingQueue(100000);
    }

    @PostConstruct
    public void start() {
        ClusterNodeInformation cni = this.clusterManager.getThisNodeInformation();
        this.nodeName = cni != null ? cni.getAnonymizedNodeIdentifier() : "local";
        logger.debug("Starting cache update receiver on node {}", (Object)this.nodeName);
        this.distributedTinyEventQueue.registerReceiver(this::onAdd);
        new Thread((Runnable)this, "perm-delta-cache-receiver").start();
    }

    @PreDestroy
    public void stop() {
        logger.debug("Unregistered from Hazelcast cache listener on node {}", (Object)this.nodeName);
        try {
            this.setRunning(false);
            this.localEventQueue.put(TinyEvent.POISON_PILL);
            this.evaluatorCacheHolder.reset();
        }
        catch (Exception exception) {
            // empty catch block
        }
        logger.debug("EventQueue receiver thread stopped on node {}", (Object)this.nodeName);
    }

    private void initCache() {
        try {
            this.localEventQueue.clear();
            EvaluatorCache evaluatorCache = this.evaluatorCacheFactory.createInstance();
            this.evaluatorCacheHolder.setEvaluatorCache(evaluatorCache);
        }
        catch (Exception e) {
            logger.error("Failed to initialize evaluator cache", (Throwable)e);
        }
    }

    @Override
    public void run() {
        this.setRunning(true);
        logger.debug("EventQueue receiver thread started");
        this.evaluatorCacheHolder.requestFullUpdate(true);
        while (this.running.get()) {
            this.processAnyEvents();
        }
    }

    @VisibleForTesting
    void setRunning(boolean running) {
        this.running.set(running);
    }

    @VisibleForTesting
    void processAnyEvents() {
        ArrayList<TinyEvent> awaitingProcessing = new ArrayList<TinyEvent>(0);
        try {
            if (this.evaluatorCacheHolder.isFullUpdateRequested()) {
                this.initCache();
            } else {
                logger.trace("Waiting for new event in eventQueue");
                awaitingProcessing.add(this.localEventQueue.take());
                int c = this.localEventQueue.drainTo(awaitingProcessing);
                logger.trace("Found {} more events", (Object)c);
                if (awaitingProcessing.contains(TinyEvent.POISON_PILL)) {
                    awaitingProcessing.remove(TinyEvent.POISON_PILL);
                    logger.trace("Found poison pill");
                }
                if (this.anyEventRequiresFullReinit(awaitingProcessing)) {
                    logger.debug("Found an event which requires cache reinitialization, reinitializing the cache");
                    this.initCache();
                    awaitingProcessing.clear();
                    logger.debug("Cache initialized");
                }
                if (!awaitingProcessing.isEmpty()) {
                    logger.debug("Processing {} received events", (Object)awaitingProcessing.size());
                    logger.trace("Event: {}", awaitingProcessing);
                    EvaluatorCache evaluatorCache = this.evaluatorCacheHolder.getEvaluatorCache();
                    evaluatorCache.update(new ArrayList<TinyEvent>(awaitingProcessing));
                    logger.debug("Completed processing {} received events", (Object)awaitingProcessing.size());
                    awaitingProcessing.clear();
                }
            }
        }
        catch (InterruptedException e) {
            logger.debug("Event receiver thread interrupted. Events remaining (size {}): {} ", (Object)awaitingProcessing.size(), awaitingProcessing);
            logger.trace("", (Throwable)e);
            this.setRunning(false);
            awaitingProcessing.clear();
        }
        catch (Exception e) {
            logger.error("Event receiver failed! WARNING: Permission cache is inconsistent!");
            logger.debug("", (Throwable)e);
            this.evaluatorCacheHolder.requestFullUpdate(true);
        }
    }

    private boolean anyEventRequiresFullReinit(List<TinyEvent> awaitingProcessing) {
        return awaitingProcessing.stream().anyMatch(tinyEvent -> tinyEvent.getEventCategory() == EventCategory.USER_DIRECTORY || tinyEvent.getEventType() == EventType.GROUP_UPDATED || tinyEvent.getEventType() == EventType.APPLICATION_UPDATED);
    }

    void onAdd(TinyEvent value) {
        if (!this.running.get()) {
            logger.debug("eventQueue listener thread not running, eventQueue not updated");
            return;
        }
        if (value != null) {
            this.localEventQueue.add(value);
            logger.debug("eventQueue updated, new size=[{}]", (Object)this.localEventQueue.size());
        }
    }
}

