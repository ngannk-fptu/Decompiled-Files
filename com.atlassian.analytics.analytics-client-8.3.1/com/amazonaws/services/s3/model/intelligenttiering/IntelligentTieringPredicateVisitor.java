/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringAndOperator;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPrefixPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringTagPredicate;

public interface IntelligentTieringPredicateVisitor {
    public void visit(IntelligentTieringPrefixPredicate var1);

    public void visit(IntelligentTieringTagPredicate var1);

    public void visit(IntelligentTieringAndOperator var1);
}

