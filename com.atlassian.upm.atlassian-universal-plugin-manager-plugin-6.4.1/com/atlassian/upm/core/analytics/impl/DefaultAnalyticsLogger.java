/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.analytics.impl;

import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.AnalyticsPublisher;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAnalyticsLogger
implements AnalyticsLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultAnalyticsLogger.class);
    private final List<AnalyticsPublisher> publishers = new CopyOnWriteArrayList<AnalyticsPublisher>();

    @Override
    public void log(AnalyticsEvent event) {
        for (AnalyticsPublisher publisher : this.publishers) {
            try {
                publisher.publish(event);
            }
            catch (Exception e) {
                log.debug("Exception thrown while logging analytics", (Throwable)e);
            }
        }
    }

    @Override
    public void register(AnalyticsPublisher publisher) {
        this.publishers.add(publisher);
    }

    @Override
    public void unregister(AnalyticsPublisher publisher) {
        this.publishers.remove(publisher);
    }
}

