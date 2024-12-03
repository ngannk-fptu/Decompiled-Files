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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class LoggingEvictionProgressTracking
implements SynchronyEvictionProgressTracking {
    private static final Logger logger = LoggerFactory.getLogger(LoggingEvictionProgressTracking.class);

    LoggingEvictionProgressTracking() {
    }

    @Override
    public SynchronyEvictionProgress startEviction(SynchronyEvictionType type, Integer thresholdHours, Integer limit) {
        if (type == SynchronyEvictionType.SOFT) {
            logger.info("Starting soft data eviction of old data ({} hours). Rows limit is {}", (Object)thresholdHours, (Object)limit);
        } else {
            logger.info("Starting hard data eviction of old data ({} hours)", (Object)thresholdHours);
        }
        return new SynchronyEvictionProgress().setEvictionType(type).startEvictionTimer();
    }

    @Override
    public void finishEviction(SynchronyEvictionProgress progress, int contentsRemoved, int rowsRemoved) {
        logger.info("Finished {} data eviction which took in total {} ms. Contents removed: {}, Rows removed: {}.", new Object[]{progress.getEvictionType(), progress.millisPassedFromEvictionStart(), contentsRemoved, rowsRemoved});
    }

    @Override
    public void failEviction(SynchronyEvictionProgress progress) {
        logger.error("Synchrony eviction ({}) has failed after {} ms", (Object)progress.getEvictionType(), (Object)progress.millisPassedFromEvictionStart());
    }

    @Override
    public void startSearch(SynchronyEvictionProgress progress, SynchronyEvictionSearchType type, Integer limit) {
        progress.setSearchType(type).startSearchTimer();
        logger.info("Starting search with {} query for {} records", (Object)type, (Object)limit);
    }

    @Override
    public void finishSearch(SynchronyEvictionProgress progress, Integer numberOfContent) {
        logger.info("Finished searching with {} query. Found {} content records. The search took {} ms.", new Object[]{progress.getSearchType(), numberOfContent, progress.millisPassedFromSearchStart()});
    }

    @Override
    public void failSearch(SynchronyEvictionProgress progress) {
        logger.error("Failed {} search query after {} ms", (Object)progress.getSearchType(), (Object)progress.millisPassedFromSearchStart());
    }

    @Override
    public void startRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfContent) {
        progress.setRemovalNumberOfContent(numberOfContent).startRemovalTimer();
        logger.info("Starting removal of {} records under synchrony lock", (Object)numberOfContent);
    }

    @Override
    public void finishRemovalUnderLock(SynchronyEvictionProgress progress, Integer numberOfRowsRemoved) {
        logger.info("Removed {} records under lock which consisted of {} rows in the DB. Time taken: {} ms", new Object[]{progress.getRemovalNumberOfContent(), numberOfRowsRemoved, progress.millisPassedFromRemovalStart()});
    }

    @Override
    public void failRemovalUnderLock(SynchronyEvictionProgress progress) {
        logger.info("Failed to remove {} content records under lock. Time elapsed: {} ms", (Object)progress.getRemovalNumberOfContent(), (Object)progress.millisPassedFromRemovalStart());
    }
}

