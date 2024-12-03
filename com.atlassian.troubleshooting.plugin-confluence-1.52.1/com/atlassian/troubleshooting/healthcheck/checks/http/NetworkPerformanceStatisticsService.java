/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkPerformanceStatisticsService {
    public static final String BROWSER_NAVIGATION_EVENT_NAME = "browser.metrics.navigation";
    public static final String SERVER_DURATION_PROPERTY = "serverDuration";
    public static final String RESPONSE_END_PROPERTY = "responseEnd";
    private static final Logger LOG = LoggerFactory.getLogger(NetworkPerformanceStatisticsService.class);
    private static final int MAX_RECENT_TRANSFER_COSTS = 1000;
    private final ArrayBlockingQueue<Integer> transferCosts = new ArrayBlockingQueue(1000);

    public void accept(String name, Map<String, Object> properties) {
        if (!StringUtils.equals((CharSequence)name, (CharSequence)BROWSER_NAVIGATION_EVENT_NAME)) {
            return;
        }
        if (properties == null) {
            return;
        }
        Long serverDuration = NetworkPerformanceStatisticsService.toLong(properties.get(SERVER_DURATION_PROPERTY));
        Long responseEnd = NetworkPerformanceStatisticsService.toLong(properties.get(RESPONSE_END_PROPERTY));
        if (serverDuration == null || responseEnd == null) {
            return;
        }
        long transferCost = responseEnd - serverDuration;
        LOG.trace("Adding transfer cost {} for event '{}' with properties {}", new Object[]{transferCost, name, properties});
        while (!this.transferCosts.offer((int)transferCost)) {
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

    public List<Integer> getRecentTransferCosts() {
        return Collections.unmodifiableList(new ArrayList<Integer>(this.transferCosts));
    }
}

