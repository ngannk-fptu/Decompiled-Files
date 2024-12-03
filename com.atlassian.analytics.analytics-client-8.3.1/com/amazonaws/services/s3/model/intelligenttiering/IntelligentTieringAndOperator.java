/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringNAryOperator;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPredicateVisitor;
import java.util.List;

public class IntelligentTieringAndOperator
extends IntelligentTieringNAryOperator {
    public IntelligentTieringAndOperator(List<IntelligentTieringFilterPredicate> operands) {
        super(operands);
    }

    @Override
    public void accept(IntelligentTieringPredicateVisitor intelligentTieringPredicateVisitor) {
        intelligentTieringPredicateVisitor.visit(this);
    }
}

