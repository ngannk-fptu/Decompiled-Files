/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import com.amazonaws.services.s3.model.analytics.AnalyticsFilterPredicate;
import java.io.Serializable;

public class AnalyticsFilter
implements Serializable {
    private AnalyticsFilterPredicate predicate;

    public AnalyticsFilter() {
    }

    public AnalyticsFilter(AnalyticsFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public AnalyticsFilterPredicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(AnalyticsFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public AnalyticsFilter withPredicate(AnalyticsFilterPredicate predicate) {
        this.setPredicate(predicate);
        return this;
    }
}

