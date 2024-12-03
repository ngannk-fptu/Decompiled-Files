/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.client.api.browser.BrowserEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.impl;

import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class NetworkPerformanceStatisticsService {
    public static final String BROWSER_NAVIGATION_EVENT_NAME = "browser.metrics.navigation";
    public static final String SERVER_DURATION_PROPERTY = "serverDuration";
    public static final String RESPONSE_END_PROPERTY = "responseEnd";
    private static final int MAX_RECENT_TRANSFER_COSTS = 1000;
    private final ArrayBlockingQueue<Long> transferCosts = new ArrayBlockingQueue(1000);
    private final EventPublisher eventPublisher;

    @Inject
    public NetworkPerformanceStatisticsService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onEvent(BrowserEvent browserEvent) {
        if (!StringUtils.equals((CharSequence)browserEvent.getName(), (CharSequence)BROWSER_NAVIGATION_EVENT_NAME)) {
            return;
        }
        Map properties = browserEvent.getProperties();
        if (properties == null) {
            return;
        }
        Long serverDuration = NetworkPerformanceStatisticsService.toLong(properties.get(SERVER_DURATION_PROPERTY));
        Long responseEnd = NetworkPerformanceStatisticsService.toLong(properties.get(RESPONSE_END_PROPERTY));
        if (serverDuration == null || responseEnd == null) {
            return;
        }
        long transferCost = responseEnd - serverDuration;
        while (!this.transferCosts.offer(transferCost)) {
            this.transferCosts.poll();
        }
    }

    private static Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        if (value instanceof String && !StringUtils.isBlank((CharSequence)((String)value))) {
            return Long.valueOf((String)value);
        }
        return null;
    }

    public List<Long> getRecentTransferCosts() {
        return Collections.unmodifiableList(new ArrayList<Long>(this.transferCosts));
    }
}

