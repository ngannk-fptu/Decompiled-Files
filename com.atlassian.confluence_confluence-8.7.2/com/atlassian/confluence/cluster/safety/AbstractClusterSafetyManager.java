/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.safety.ClusterPanicException;
import com.atlassian.confluence.cluster.safety.ClusterSafetyDao;
import com.atlassian.confluence.cluster.safety.ClusterSafetyManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

public abstract class AbstractClusterSafetyManager
implements ClusterSafetyManager {
    protected static final String NOT_FOUND_STATEMENT = "not found";
    protected static final String NON_CLUSTERED_NODE_NAME = "not clustered";
    public static final String SAFETY_NUMBER_MODIFIER = "safety-number-member";
    public static final String SAFETY_NUMBER = "safety-number";
    private final ClusterSafetyDao clusterSafetyDao;
    private final EventPublisher eventPublisher;
    private final ClusterManager clusterManager;
    private final LicenseService licenseService;
    protected final Random random = new Random();
    private final Supplier<String> nodeName = new LazyReference<String>(){

        protected String create() throws Exception {
            ClusterNodeInformation info = AbstractClusterSafetyManager.this.clusterManager.getThisNodeInformation();
            return info == null ? AbstractClusterSafetyManager.NON_CLUSTERED_NODE_NAME : info.getAnonymizedNodeIdentifier();
        }
    };

    protected AbstractClusterSafetyManager(ClusterSafetyDao clusterSafetyDao, EventPublisher eventPublisher, ClusterManager clusterManager, LicenseService licenseService) {
        this.clusterSafetyDao = Objects.requireNonNull(clusterSafetyDao);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    @Override
    public void verify(long runInterval) {
        int nextValue = this.getNextValue();
        Optional<String> lastCacheModifier = this.getLastCacheModifier();
        Optional<Integer> dbSafetyNumber = this.getDbSafetyNumber();
        Optional<Integer> cacheSafetyNumber = this.getCacheSafetyNumber();
        this.logDetails(nextValue, lastCacheModifier, dbSafetyNumber, cacheSafetyNumber);
        try {
            if (dbSafetyNumber.isPresent() && cacheSafetyNumber.isPresent()) {
                if (!dbSafetyNumber.equals(cacheSafetyNumber)) {
                    this.onNumbersAreDifferent(lastCacheModifier.orElse(NOT_FOUND_STATEMENT), dbSafetyNumber.get(), cacheSafetyNumber.get(), nextValue);
                } else {
                    this.onNumbersAreEqual(lastCacheModifier.orElse(NOT_FOUND_STATEMENT), dbSafetyNumber.get(), cacheSafetyNumber.get(), nextValue);
                }
            } else if (dbSafetyNumber.isPresent()) {
                this.getLogger().debug("Found cluster safety number in database [ {} ] but not in cache", (Object)dbSafetyNumber.get());
                this.onCacheNumberIsMissed(dbSafetyNumber.get(), nextValue);
            } else if (cacheSafetyNumber.isPresent()) {
                this.getLogger().debug("Found cluster safety number in cache [ {} ] but not in database", this.getCacheSafetyNumber());
                this.onDatabaseNumberIsMissed(lastCacheModifier.orElse(NOT_FOUND_STATEMENT), cacheSafetyNumber.get(), nextValue);
            } else {
                this.onNumbersMissed(nextValue);
            }
        }
        catch (ClusterPanicException e) {
            this.panic();
        }
    }

    protected void onDatabaseNumberIsMissed(@NonNull String lastCacheModifier, @NonNull Integer cacheSafetyNumber, int nextValue) throws ClusterPanicException {
        this.updateSafetyNumber(nextValue);
    }

    protected void onCacheNumberIsMissed(@NonNull Integer dbSafetyNumber, int nextValue) throws ClusterPanicException {
        this.updateSafetyNumber(nextValue);
    }

    protected void onNumbersAreEqual(@NonNull String lastCacheModifier, @NonNull Integer dbSafetyNumber, @NonNull Integer cacheSafetyNumber, int nextValue) throws ClusterPanicException {
        this.updateSafetyNumber(nextValue);
    }

    protected void onNumbersAreDifferent(@NonNull String lastCacheModifier, @NonNull Integer dbSafetyNumber, @NonNull Integer cacheSafetyNumber, int nextValue) throws ClusterPanicException {
        this.getLogger().warn("Detected different number in database [ {} ] and cache [ {} ]. Cache number last set by [ {} ]. Triggering panic on node [ {} ]", new Object[]{dbSafetyNumber, cacheSafetyNumber, lastCacheModifier, this.getNodeName()});
        throw new ClusterPanicException();
    }

    protected void onNumbersMissed(int nextValue) {
        this.updateSafetyNumber(nextValue);
    }

    protected void updateSafetyNumber(int nextValue) {
        this.clusterSafetyDao.setSafetyNumber(nextValue);
        this.storeCacheNumber(nextValue);
    }

    protected @NonNull String getNodeName() {
        return (String)this.nodeName.get();
    }

    @VisibleForTesting
    public boolean isLogEnabled() {
        return this.getLogger().isDebugEnabled();
    }

    protected int getNextValue() {
        return this.random.nextInt();
    }

    private void panic() {
        this.logRuntimeInfo();
        this.handlePanic();
    }

    private void storeCacheNumber(int value) {
        this.getSafetyNumberModifierMap().put(SAFETY_NUMBER_MODIFIER, this.getNodeName());
        this.getSafetyNumberMap().put(SAFETY_NUMBER, value);
    }

    private Optional<Integer> getCacheSafetyNumber() {
        return Optional.ofNullable(this.getSafetyNumberMap().get(SAFETY_NUMBER));
    }

    private Optional<String> getLastCacheModifier() {
        return Optional.ofNullable(this.getSafetyNumberModifierMap().get(SAFETY_NUMBER_MODIFIER));
    }

    private Optional<Integer> getDbSafetyNumber() {
        return Optional.ofNullable(this.clusterSafetyDao.getSafetyNumber());
    }

    private void logDetails(int nextValue, Optional<String> lastCacheModifier, Optional<Integer> dbSafetyNumber, Optional<Integer> cacheSafetyNumber) {
        if (this.isLogEnabled()) {
            String dbSafetyNumberString = dbSafetyNumber.isPresent() ? String.valueOf(dbSafetyNumber.get()) : NOT_FOUND_STATEMENT;
            this.getLogger().debug("Database number exists [ {} ] [ {} ]", (Object)dbSafetyNumber.isPresent(), (Object)dbSafetyNumberString);
            String cacheSafetyNumberString = cacheSafetyNumber.isPresent() ? String.valueOf(cacheSafetyNumber.get()) : NOT_FOUND_STATEMENT;
            this.getLogger().debug("Cached number exists [ {} ] [ {} ], last modifier: [ {} ]", new Object[]{cacheSafetyNumber.isPresent(), cacheSafetyNumberString, lastCacheModifier.orElse(NOT_FOUND_STATEMENT)});
            if (dbSafetyNumber.isPresent() && cacheSafetyNumber.isPresent()) {
                this.getLogger().debug("Database number: {} should equal cached number: {}", (Object)dbSafetyNumberString, (Object)cacheSafetyNumberString);
            }
            this.getLogger().debug("Next value: {}", (Object)nextValue);
        }
    }

    protected abstract Logger getLogger();

    protected abstract void logRuntimeInfo();

    protected abstract Map<String, String> getSafetyNumberModifierMap();

    protected abstract Map<String, Integer> getSafetyNumberMap();

    protected abstract void handlePanic();

    public ClusterManager getClusterManager() {
        return this.clusterManager;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public LicenseService getLicenseService() {
        return this.licenseService;
    }
}

