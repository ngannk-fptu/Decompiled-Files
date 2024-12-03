/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import java.util.List;

abstract class IntelligentTieringNAryOperator
extends IntelligentTieringFilterPredicate {
    private final List<IntelligentTieringFilterPredicate> operands;

    protected IntelligentTieringNAryOperator(List<IntelligentTieringFilterPredicate> operands) {
        this.operands = operands;
    }

    public List<IntelligentTieringFilterPredicate> getOperands() {
        return this.operands;
    }
}

