/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.v2.AnalyticsEvent
 */
package com.atlassian.analytics.client.pipeline.predicate;

import com.atlassian.analytics.api.events.v2.AnalyticsEvent;
import com.atlassian.analytics.client.pipeline.predicate.CanHandleEventPredicate;

public class AnalyticsEventInterfacePredicate
implements CanHandleEventPredicate {
    @Override
    public boolean canHandleEvent(Object o) {
        return o instanceof AnalyticsEvent;
    }
}

