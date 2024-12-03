/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import com.amazonaws.services.s3.model.analytics.AnalyticsPredicateVisitor;
import java.io.Serializable;

public abstract class AnalyticsFilterPredicate
implements Serializable {
    public abstract void accept(AnalyticsPredicateVisitor var1);
}

