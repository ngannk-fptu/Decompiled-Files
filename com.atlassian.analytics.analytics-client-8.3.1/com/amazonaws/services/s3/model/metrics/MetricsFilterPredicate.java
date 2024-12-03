/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.metrics;

import com.amazonaws.services.s3.model.metrics.MetricsPredicateVisitor;
import java.io.Serializable;

public abstract class MetricsFilterPredicate
implements Serializable {
    public abstract void accept(MetricsPredicateVisitor var1);
}

