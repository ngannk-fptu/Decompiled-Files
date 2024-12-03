/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.cluster.safety.AbstractClusterSafetyManager
 *  com.atlassian.confluence.cluster.safety.ClusterPanicAnalyticsEvent
 *  com.atlassian.confluence.cluster.safety.ClusterPanicEvent
 *  com.atlassian.confluence.cluster.safety.ClusterPanicException
 *  com.atlassian.confluence.cluster.safety.ClusterSafetyDao
 *  com.atlassian.confluence.impl.logging.admin.LoggingConfigService
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.util.profiling.ActivityMonitor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.safety.AbstractClusterSafetyManager;
import com.atlassian.confluence.cluster.safety.ClusterPanicAnalyticsEvent;
import com.atlassian.confluence.cluster.safety.ClusterPanicEvent;
import com.atlassian.confluence.cluster.safety.ClusterPanicException;
import com.atlassian.confluence.cluster.safety.ClusterSafetyDao;
import com.atlassian.confluence.impl.logging.admin.LoggingConfigService;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastClusterSafetyManager
extends AbstractClusterSafetyManager {
    static final String HZ_NOT_ACTIVE_EXCEPTION_FQCN = "com.hazelcast.core.HazelcastInstanceNotActiveException";
    private static final Logger log = LoggerFactory.getLogger(HazelcastClusterSafetyManager.class);
    private static final int CLUSTER_SIZE_MASK = 255;
    public static final String SAFETY_MAP_PREFIX = HazelcastClusterSafetyManager.class.getSimpleName();
    @VisibleForTesting
    static final String SAFETY_NUMBER_MAP_NAME = SAFETY_MAP_PREFIX + ".safetyNumber";
    @VisibleForTesting
    static final String SAFETY_MODIFIER_MAP_NAME = SAFETY_MAP_PREFIX + ".safetyNumberModifier";
    static final long JOB_RUN_INTERVAL_BUFFER = 4L;
    private static final Duration HZ_LOGGING_BURST_DURATION = Duration.ofMinutes(5L);
    private static final int HZ_LOGGING_BURST_MAX = 1;
    private final HazelcastInstance instance;
    private final ActivityMonitor activityMonitor;
    private final ScheduledExecutorService executor;
    private final LoggingConfigService loggingConfigService;
    private final long timeToLiveAfterSplitBrain;
    private final AtomicBoolean firstRun = new AtomicBoolean(true);
    private final AtomicLong lastSuccessfulRun = new AtomicLong(0L);

    public HazelcastClusterSafetyManager(ClusterSafetyDao clusterSafetyDao, EventPublisher eventPublisher, ClusterManager clusterManager, HazelcastInstance hazelcastInstance, ActivityMonitor activityMonitor, ScheduledExecutorService executor, LicenseService licenseService, LoggingConfigService loggingConfigService) {
        super(clusterSafetyDao, eventPublisher, clusterManager, licenseService);
        this.instance = hazelcastInstance;
        this.activityMonitor = Objects.requireNonNull(activityMonitor);
        this.executor = Objects.requireNonNull(executor);
        this.loggingConfigService = loggingConfigService;
        this.timeToLiveAfterSplitBrain = Long.parseLong(System.getProperty("cluster.safety.time.to.live.split.ms", "60000"));
    }

    public void verify(long runIntervalMs) {
        ArrayList members = Lists.newArrayList((Iterable)Collections2.transform((Collection)this.getClusterManager().getAllNodesInformation(), ClusterNodeInformation::getAnonymizedNodeIdentifier));
        ClusterNodeInformation nodeInfo = this.getClusterManager().getThisNodeInformation();
        String thisNode = nodeInfo == null ? null : nodeInfo.getAnonymizedNodeIdentifier();
        Collections.sort(members);
        if (this.shouldRun(members, thisNode, runIntervalMs)) {
            log.debug("{} performing cluster safety verification", (Object)thisNode);
            super.verify(runIntervalMs);
        }
        this.firstRun.set(false);
    }

    protected boolean shouldRun(List<String> members, String thisNode, long runIntervalMs) {
        if (runIntervalMs == 0L) {
            return true;
        }
        int nodeToRun = this.choseNodeToRun(members, thisNode, runIntervalMs);
        return thisNode.equals(members.get(nodeToRun));
    }

    private int choseNodeToRun(List<String> members, String thisNode, long runIntervalMs) {
        long currentSyncTimeMs = this.getSyncClusterTime();
        long seed = this.getSeedFromTime(currentSyncTimeMs, runIntervalMs);
        int memberCount = members.size();
        Random rnd = new Random(seed);
        int nodeToRun = rnd.nextInt(memberCount);
        log.debug("{} at {} with seed {}", new Object[]{thisNode, currentSyncTimeMs, seed});
        log.debug("{} generated {} out of {} nodes to run", new Object[]{thisNode, nodeToRun + 1, memberCount});
        return nodeToRun;
    }

    protected long getSyncClusterTime() {
        return null == this.instance ? System.currentTimeMillis() : this.instance.getCluster().getClusterTime();
    }

    protected long getSeedFromTime(long timeMs, long runIntervalMs) {
        long conversion = runIntervalMs * 4L;
        return timeMs / conversion * conversion;
    }

    protected void onDatabaseNumberIsMissed(@NonNull String lastCacheModifier, @NonNull Integer cacheSafetyNumber, int nextValue) {
        this.lastSuccessfulRun.set(0L);
        super.onDatabaseNumberIsMissed(lastCacheModifier, cacheSafetyNumber, nextValue);
    }

    protected void onCacheNumberIsMissed(@NonNull Integer dbSafetyNumber, int nextValue) {
        if (!this.firstRun.compareAndSet(true, false)) {
            this.getLogger().warn("This is not the first run of the cluster safety job, but the cluster safety number can not be found in the cache. This happens when a node is disconnected and is missing a required part of the cache.");
            long now = System.currentTimeMillis();
            this.lastSuccessfulRun.compareAndSet(0L, now);
            long lastRun = this.lastSuccessfulRun.get();
            if (now - lastRun >= this.timeToLiveAfterSplitBrain) {
                this.getLogger().warn("Time to live after split brain is exceeded. Proceeding with cluster safety check.");
            } else {
                this.getLogger().warn("Time to live after split brain [ {}ms ] is in progress. Skipping cluster safety check to allow split nodes to rejoin.", (Object)this.timeToLiveAfterSplitBrain);
                return;
            }
        }
        super.onCacheNumberIsMissed(dbSafetyNumber, nextValue);
    }

    protected void onNumbersAreDifferent(@NonNull String lastCacheModifier, @NonNull Integer dbSafetyNumber, @NonNull Integer cacheSafetyNumber, int nextValue) throws ClusterPanicException {
        this.lastSuccessfulRun.set(0L);
        log.warn("Detected different number in database [ {} ] and cache [ {} ]. Cache number last set by [ {} ]", new Object[]{dbSafetyNumber, cacheSafetyNumber, lastCacheModifier});
        int currentClusterSize = this.getClusterManager().getClusterInformation().getMemberCount();
        int clusterSizeFromDb = this.decodeClusterSize(dbSafetyNumber);
        if (currentClusterSize <= clusterSizeFromDb) {
            log.warn("Triggering panic on current node. This cluster size [ {} ], cluster size from db: [ {} ]", (Object)currentClusterSize, (Object)clusterSizeFromDb);
            throw new ClusterPanicException();
        }
        log.warn("We're the bigger part of the cluster. Proceeding with cluster safety update.");
        this.updateSafetyNumber(nextValue);
    }

    protected void onNumbersAreEqual(@NonNull String lastCacheModifier, @NonNull Integer dbSafetyNumber, @NonNull Integer cacheSafetyNumber, int nextValue) {
        this.lastSuccessfulRun.set(0L);
        super.onNumbersAreEqual(lastCacheModifier, dbSafetyNumber, cacheSafetyNumber, nextValue);
    }

    protected void onNumbersMissed(int nextValue) {
        this.lastSuccessfulRun.set(0L);
        super.onNumbersMissed(nextValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handlePanic() {
        ConfluenceLicense currentLicense = this.getLicenseService().retrieve();
        try {
            ClusterNodeInformation info = this.getClusterManager().getThisNodeInformation();
            String nodeId = info == null ? null : info.getAnonymizedNodeIdentifier();
            InetSocketAddress localSocketAddr = info == null ? null : info.getLocalSocketAddress();
            this.getEventPublisher().publish((Object)new ClusterPanicAnalyticsEvent(true, this.getClusterManager().getClusterInformation().getMemberCount(), currentLicense.getMaximumNumberOfClusterNodes(), currentLicense.getMaximumNumberOfUsers()));
            this.getEventPublisher().publish((Object)new ClusterPanicEvent((Object)this, "[Origin node: " + nodeId + " listening on " + localSocketAddr + "] Clustered Confluence: Database is being updated by an instance which is not part of the current cluster. You should check network connections between cluster nodes, especially multicast traffic."));
        }
        finally {
            this.getClusterManager().stopCluster();
            this.rateLimitLogging();
        }
    }

    void rateLimitLogging() {
        log.warn("Rate limiting logging caused by HazelcastInstanceNotActiveException");
        this.loggingConfigService.rateLimit(HZ_NOT_ACTIVE_EXCEPTION_FQCN, HZ_LOGGING_BURST_DURATION, 1);
    }

    protected void logRuntimeInfo() {
        try {
            Future<String> runtimeDetails = this.executor.submit(this.getRuntimeInfo());
            String debugInfo = runtimeDetails.get(10L, TimeUnit.SECONDS);
            log.error(debugInfo);
        }
        catch (Exception e) {
            log.error("Unable to get debug dump before triggering cluster panic", (Throwable)e);
        }
    }

    protected Logger getLogger() {
        return log;
    }

    protected IMap<String, Integer> getSafetyNumberMap() {
        return this.instance.getMap(SAFETY_NUMBER_MAP_NAME);
    }

    protected IMap<String, String> getSafetyNumberModifierMap() {
        return this.instance.getMap(SAFETY_MODIFIER_MAP_NAME);
    }

    private Callable<String> getRuntimeInfo() {
        return () -> {
            Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
            String totalThreadDump = stackTraces.entrySet().stream().map(e -> ((Thread)e.getKey()).toString() + Joiner.on((String)"\n\t").join((Object[])e.getValue())).collect(Collectors.toList()).stream().collect(Collectors.joining("\n"));
            Collection activities = this.activityMonitor.snapshotCurrent();
            String totalActivityDump = activities.stream().map(Object::toString).collect(Collectors.joining("\n"));
            return Stream.of("====DEBUG DUMP START", "THREADS", totalThreadDump, "ACTIVITIES", totalActivityDump, "====DEBUG DUMP END====").collect(Collectors.joining("\n"));
        };
    }

    protected int getNextValue() {
        return this.encodeClusterSize(this.random.nextInt(0xFFFFFF));
    }

    @VisibleForTesting
    protected int encodeClusterSize(int nextValue) {
        return nextValue & 0xFFFFFF00 | this.getClusterManager().getClusterInformation().getMemberCount() & 0xFF;
    }

    @VisibleForTesting
    protected int decodeClusterSize(int dbSafetyNumber) {
        return dbSafetyNumber & 0xFF;
    }
}

