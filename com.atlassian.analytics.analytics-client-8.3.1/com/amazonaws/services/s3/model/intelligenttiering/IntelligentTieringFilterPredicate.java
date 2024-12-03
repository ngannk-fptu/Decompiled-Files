/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPredicateVisitor;
import java.io.Serializable;

public abstract class IntelligentTieringFilterPredicate
implements Serializable {
    public abstract void accept(IntelligentTieringPredicateVisitor var1);
}

