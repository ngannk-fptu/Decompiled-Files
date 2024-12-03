/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPredicateVisitor;

public final class IntelligentTieringPrefixPredicate
extends IntelligentTieringFilterPredicate {
    private final String prefix;

    public IntelligentTieringPrefixPredicate(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void accept(IntelligentTieringPredicateVisitor intelligentTieringPredicateVisitor) {
        intelligentTieringPredicateVisitor.visit(this);
    }
}

