/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.collab.impl.tracking;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgress;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionProgressTracking;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class CompositeEvictionProgressTracking
implements SynchronyEvictionProgressTracking {
    private static final Logger logger = LoggerFactory.getLogger(CompositeEvictionProgressTracking.class);
    private final List<SynchronyEvictionProgressTracking> trackers;

    public CompositeEvictionProgressTracking(List<SynchronyEvictionProgressTracking> trackers) {
        this.trackers = trackers;
    }

    @Override
    public SynchronyEvictionProgress startEviction(SynchronyEvictionType type, Integer thresholdHours, Integer limit) {
        return this.safelyInvokeTracker(t -> t.startEviction(type, thresholdHours, limit));
    }

    @Override
    public void finishEviction(SynchronyEvictionProgress progress, int contentsRemoved, int rowsRemoved) {
        this.safelyExecuteTracker(t -> t.finishEviction(progress, contentsRemoved, rowsRemoved));
    }

    @Override
    public void failEviction(SynchronyEvictionProgress progress) {
        this.safelyExecuteTracker(t -> t.failEviction(progress));
    }

    @Override
    public void startSearch(SynchronyEvictionProgress progress, SynchronyEvictionSearchType type, Integer limit) {
        this.safelyExecuteTracker(t -> t.startSearch(progress, type, limit));
    }

    @Override
    public void finishSearch(SynchronyEvictionProgress progress, Integer numberOfContent) {
        this.safelyExecuteTracker(t -> t.finishSearch(progress, numberOfContent));
    }

    @Override
    public void failSearch(SynchronyEvictionProgress progress) {
        this.safelyExecuteTracker(t -> t.failSearch(progress));
    }

    @Override
    public void startRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfContent) {
        this.safelyExecuteTracker(t -> t.startRemovalUnderLock(progress, numberOfContent));
    }

    @Override
    public void finishRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfRowsRemoved) {
        this.safelyExecuteTracker(t -> t.finishRemovalUnderLock(progress, numberOfRowsRemoved));
    }

    @Override
    public void failRemovalUnderLock(SynchronyEvictionProgress progress) {
        this.safelyExecuteTracker(t -> t.failRemovalUnderLock(progress));
    }

    private SynchronyEvictionProgress safelyInvokeTracker(Function<SynchronyEvictionProgressTracking, SynchronyEvictionProgress> method) {
        SynchronyEvictionProgress totalProgress = null;
        for (SynchronyEvictionProgressTracking tracker : this.trackers) {
            try {
                SynchronyEvictionProgress specificProgress = method.apply(tracker);
                if (totalProgress == null) {
                    totalProgress = specificProgress;
                    continue;
                }
                totalProgress.mergeWith(specificProgress);
            }
            catch (Exception e) {
                logger.error("Failed to execute tracker {} with the following exception", (Object)tracker.getClass().getSimpleName(), (Object)e);
            }
        }
        return totalProgress;
    }

    private void safelyExecuteTracker(Consumer<SynchronyEvictionProgressTracking> method) {
        for (SynchronyEvictionProgressTracking tracker : this.trackers) {
            try {
                method.accept(tracker);
            }
            catch (Exception e) {
                logger.error("Failed to execute tracker {} with the following exception", (Object)tracker.getClass().getSimpleName(), (Object)e);
            }
        }
    }
}

