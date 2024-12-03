/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecycleFilterPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleNAryOperator;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePredicateVisitor;
import java.util.List;

public final class LifecycleAndOperator
extends LifecycleNAryOperator {
    public LifecycleAndOperator(List<LifecycleFilterPredicate> operands) {
        super(operands);
    }

    @Override
    public void accept(LifecyclePredicateVisitor lifecyclePredicateVisitor) {
        lifecyclePredicateVisitor.visit(this);
    }
}

