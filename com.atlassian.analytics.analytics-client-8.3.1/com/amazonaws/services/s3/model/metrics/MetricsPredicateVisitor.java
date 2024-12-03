/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsAccessPointArnPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsAndOperator;
import com.amazonaws.services.s3.model.metrics.MetricsPrefixPredicate;
import com.amazonaws.services.s3.model.metrics.MetricsTagPredicate;

public interface MetricsPredicateVisitor {
    public void visit(MetricsPrefixPredicate var1);

    public void visit(MetricsTagPredicate var1);

    public void visit(MetricsAndOperator var1);

    public void visit(MetricsAccessPointArnPredicate var1);
}

