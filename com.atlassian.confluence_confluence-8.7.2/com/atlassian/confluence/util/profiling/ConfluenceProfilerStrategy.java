/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.ProfilerConfiguration
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.strategy.ProfilerStrategy
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.strategy.ProfilerStrategy;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceProfilerStrategy
implements ProfilerStrategy {
    private static final String SPLITS_ATTRIBUTE_NAME = "com.atlassian.confluence.util.profiling.ConfluenceProfilerStrategy.splits";
    private static final Logger log = LoggerFactory.getLogger(ConfluenceProfilerStrategy.class);
    private final DarkFeaturesManager featuresManager;
    private final ConfluenceMonitoring confluenceMonitoring;

    public ConfluenceProfilerStrategy(DarkFeaturesManager featuresManager, ConfluenceMonitoring confluenceMonitoring) {
        this.featuresManager = Objects.requireNonNull(featuresManager);
        this.confluenceMonitoring = Objects.requireNonNull(confluenceMonitoring);
    }

    public void setConfiguration(@Nonnull ProfilerConfiguration configuration) {
    }

    public void onRequestEnd() {
    }

    public Ticker start(@Nonnull String name) {
        if (!this.isEnabled()) {
            return Ticker.NO_OP;
        }
        try {
            Deque<Split> splits = this.getSplits();
            splits.push(this.confluenceMonitoring.startSplit(name));
        }
        catch (IllegalStateException e) {
            log.debug("start: HTTP Request not found for " + name);
        }
        return () -> this.stop(name);
    }

    private void stop(String name) {
        try {
            Deque<Split> splits = this.getSplits();
            if (!splits.isEmpty()) {
                splits.pop().stop();
            }
        }
        catch (IllegalStateException e) {
            log.debug("stop: HTTP Request not found for {}", (Object)name);
        }
    }

    private boolean isEnabled() {
        return this.getRequest() != null && this.featuresManager.getDarkFeaturesAllUsers().isFeatureEnabled("confluence.performance.laas.logging");
    }

    private Deque<Split> getSplits() {
        HttpServletRequest request = this.getRequest();
        if (request == null) {
            throw new IllegalStateException("Unable to retrieve a servlet request");
        }
        LinkedList splits = (LinkedList)request.getAttribute(SPLITS_ATTRIBUTE_NAME);
        if (splits == null) {
            splits = new LinkedList();
            request.setAttribute(SPLITS_ATTRIBUTE_NAME, splits);
        }
        return splits;
    }

    @Nullable
    private HttpServletRequest getRequest() {
        return new StaticHttpContext().getRequest();
    }
}

