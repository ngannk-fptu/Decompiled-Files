/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecycleFilterPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePredicateVisitor;

public final class LifecycleObjectSizeLessThanPredicate
extends LifecycleFilterPredicate {
    private final Long objectSizeLessThan;

    public LifecycleObjectSizeLessThanPredicate(Long objectSizeLessThan) {
        this.objectSizeLessThan = objectSizeLessThan;
    }

    public Long getObjectSizeLessThan() {
        return this.objectSizeLessThan;
    }

    @Override
    public void accept(LifecyclePredicateVisitor lifecyclePredicateVisitor) {
        lifecyclePredicateVisitor.visit(this);
    }
}

