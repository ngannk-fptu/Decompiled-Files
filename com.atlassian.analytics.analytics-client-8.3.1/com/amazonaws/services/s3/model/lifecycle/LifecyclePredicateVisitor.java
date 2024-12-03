/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecycleAndOperator;
import com.amazonaws.services.s3.model.lifecycle.LifecycleObjectSizeGreaterThanPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleObjectSizeLessThanPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;

public interface LifecyclePredicateVisitor {
    public void visit(LifecyclePrefixPredicate var1);

    public void visit(LifecycleTagPredicate var1);

    public void visit(LifecycleObjectSizeGreaterThanPredicate var1);

    public void visit(LifecycleObjectSizeLessThanPredicate var1);

    public void visit(LifecycleAndOperator var1);
}

