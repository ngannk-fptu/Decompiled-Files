/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import com.amazonaws.services.s3.model.analytics.AnalyticsFilterPredicate;
import com.amazonaws.services.s3.model.analytics.AnalyticsNAryOperator;
import com.amazonaws.services.s3.model.analytics.AnalyticsPredicateVisitor;
import java.util.List;

public class AnalyticsAndOperator
extends AnalyticsNAryOperator {
    public AnalyticsAndOperator(List<AnalyticsFilterPredicate> operands) {
        super(operands);
    }

    @Override
    public void accept(AnalyticsPredicateVisitor analyticsPredicateVisitor) {
        analyticsPredicateVisitor.visit(this);
    }
}

