/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import com.amazonaws.services.s3.model.analytics.AnalyticsFilterPredicate;
import java.util.List;

abstract class AnalyticsNAryOperator
extends AnalyticsFilterPredicate {
    private final List<AnalyticsFilterPredicate> operands;

    public AnalyticsNAryOperator(List<AnalyticsFilterPredicate> operands) {
        this.operands = operands;
    }

    public List<AnalyticsFilterPredicate> getOperands() {
        return this.operands;
    }
}

