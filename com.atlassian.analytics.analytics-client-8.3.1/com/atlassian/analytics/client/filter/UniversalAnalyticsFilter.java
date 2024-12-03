/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.analytics.client.filter;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.filter.AbstractHttpFilter;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;

public class UniversalAnalyticsFilter
extends AbstractHttpFilter {
    private static final String KEY_EVENT_NAME = "__AA_event_name";
    private static final String KEY_PRODUCT_NAME = "__AA_product";
    private final AnalyticsConfig analyticsConfig;
    private final EventPublisher eventPublisher;
    private final String lowercaseApplicationDisplayName;

    public UniversalAnalyticsFilter(EventPublisher eventPublisher, AnalyticsPropertyService analyticsPropertyService, AnalyticsConfig analyticsConfig) {
        this.eventPublisher = eventPublisher;
        this.analyticsConfig = analyticsConfig;
        this.lowercaseApplicationDisplayName = analyticsPropertyService.getDisplayName().toLowerCase();
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (this.analyticsConfig.canCollectAnalytics()) {
            boolean shouldTrackEvent;
            Map requestParams = request.getParameterMap();
            Object[] eventNameList = (String[])requestParams.get(KEY_EVENT_NAME);
            Object[] productNameList = (String[])requestParams.get(KEY_PRODUCT_NAME);
            boolean bl = shouldTrackEvent = !ArrayUtils.isEmpty((Object[])eventNameList);
            if (shouldTrackEvent) {
                Object productName = this.lowercaseApplicationDisplayName;
                if (!ArrayUtils.isEmpty((Object[])productNameList) && !((String)productNameList[0]).isEmpty()) {
                    productName = productNameList[0];
                }
                this.eventPublisher.publish((Object)new FilteredEvent((String)eventNameList[0], (String)productName));
            }
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    public static class FilteredEvent {
        private final String name;
        private final String product;

        public FilteredEvent(String name, String product) {
            this.name = name;
            this.product = product;
        }

        @EventName
        public String getEventName() {
            return this.product + "." + this.name;
        }
    }
}

