/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsFilterPredicate;
import java.io.Serializable;

public class MetricsFilter
implements Serializable {
    private MetricsFilterPredicate predicate;

    public MetricsFilter() {
    }

    public MetricsFilter(MetricsFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public MetricsFilterPredicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(MetricsFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public MetricsFilter withPredicate(MetricsFilterPredicate predicate) {
        this.setPredicate(predicate);
        return this;
    }
}

