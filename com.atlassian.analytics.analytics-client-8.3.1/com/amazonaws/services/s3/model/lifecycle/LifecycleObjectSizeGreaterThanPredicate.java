/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecycleFilterPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePredicateVisitor;

public final class LifecycleObjectSizeGreaterThanPredicate
extends LifecycleFilterPredicate {
    private final Long objectSizeGreaterThan;

    public LifecycleObjectSizeGreaterThanPredicate(Long objectSizeGreaterThan) {
        this.objectSizeGreaterThan = objectSizeGreaterThan;
    }

    public Long getObjectSizeGreaterThan() {
        return this.objectSizeGreaterThan;
    }

    @Override
    public void accept(LifecyclePredicateVisitor lifecyclePredicateVisitor) {
        lifecyclePredicateVisitor.visit(this);
    }
}

