/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsFilterPredicate;
import java.util.List;

abstract class MetricsNAryOperator
extends MetricsFilterPredicate {
    private final List<MetricsFilterPredicate> operands;

    public MetricsNAryOperator(List<MetricsFilterPredicate> operands) {
        this.operands = operands;
    }

    public List<MetricsFilterPredicate> getOperands() {
        return this.operands;
    }
}

