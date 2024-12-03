/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecyclePredicateVisitor;
import java.io.Serializable;

public abstract class LifecycleFilterPredicate
implements Serializable {
    public abstract void accept(LifecyclePredicateVisitor var1);
}

