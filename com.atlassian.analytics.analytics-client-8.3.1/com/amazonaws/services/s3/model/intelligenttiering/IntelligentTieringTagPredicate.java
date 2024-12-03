/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringPredicateVisitor;

public final class IntelligentTieringTagPredicate
extends IntelligentTieringFilterPredicate {
    private final Tag tag;

    public IntelligentTieringTagPredicate(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return this.tag;
    }

    @Override
    public void accept(IntelligentTieringPredicateVisitor intelligentTieringPredicateVisitor) {
        intelligentTieringPredicateVisitor.visit(this);
    }
}

