/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import com.amazonaws.services.s3.model.analytics.AnalyticsAndOperator;
import com.amazonaws.services.s3.model.analytics.AnalyticsPrefixPredicate;
import com.amazonaws.services.s3.model.analytics.AnalyticsTagPredicate;

public interface AnalyticsPredicateVisitor {
    public void visit(AnalyticsPrefixPredicate var1);

    public void visit(AnalyticsTagPredicate var1);

    public void visit(AnalyticsAndOperator var1);
}

