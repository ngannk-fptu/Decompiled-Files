/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsFilterPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsPredicateVisitor;

public final class MetricsAccessPointArnPredicate
extends MetricsFilterPredicate {
    private final String accessPointArn;

    public MetricsAccessPointArnPredicate(String accessPointArn) {
        this.accessPointArn = accessPointArn;
    }

    public String getAccessPointArn() {
        return this.accessPointArn;
    }

    @Override
    public void accept(MetricsPredicateVisitor metricsPredicateVisitor) {
        metricsPredicateVisitor.visit(this);
    }
}

