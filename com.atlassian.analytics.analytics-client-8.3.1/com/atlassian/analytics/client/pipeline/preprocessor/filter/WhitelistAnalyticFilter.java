/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.preprocessor.filter;

import com.atlassian.analytics.client.eventfilter.BlacklistFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilter;
import com.atlassian.analytics.event.RawEvent;
import java.util.Map;

public class WhitelistAnalyticFilter
implements AnalyticsFilter {
    private final BlacklistFilter blacklistFilter;
    private final WhitelistFilter whitelistFilter;

    public WhitelistAnalyticFilter(BlacklistFilter blacklistFilter, WhitelistFilter whitelistFilter) {
        this.blacklistFilter = blacklistFilter;
        this.whitelistFilter = whitelistFilter;
    }

    @Override
    public Map<String, Object> filter(RawEvent event) {
        return this.whitelistFilter.applyWhitelistToEvent(event.getName(), event.getProperties());
    }

    @Override
    public boolean canCollect(RawEvent event) {
        return !this.blacklistFilter.isEventBlacklisted(event) && this.whitelistFilter.isEventWhitelisted(event);
    }
}

