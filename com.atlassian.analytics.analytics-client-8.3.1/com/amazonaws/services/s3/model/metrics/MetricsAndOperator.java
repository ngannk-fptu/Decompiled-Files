/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsFilterPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsNAryOperator;
import com.amazonaws.services.s3.model.metrics.MetricsPredicateVisitor;
import java.util.List;

public final class MetricsAndOperator
extends MetricsNAryOperator {
    public MetricsAndOperator(List<MetricsFilterPredicate> operands) {
        super(operands);
    }

    @Override
    public void accept(MetricsPredicateVisitor metricsPredicateVisitor) {
        metricsPredicateVisitor.visit(this);
    }
}

