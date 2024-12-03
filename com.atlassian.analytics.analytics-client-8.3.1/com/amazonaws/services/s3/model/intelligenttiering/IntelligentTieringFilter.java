/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

import com.amazonaws.services.s3.model.intelligenttiering.IntelligentTieringFilterPredicate;
import java.io.Serializable;

public class IntelligentTieringFilter
implements Serializable {
    private IntelligentTieringFilterPredicate predicate;

    public IntelligentTieringFilter() {
    }

    public IntelligentTieringFilter(IntelligentTieringFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public IntelligentTieringFilterPredicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(IntelligentTieringFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public IntelligentTieringFilter withPredicate(IntelligentTieringFilterPredicate predicate) {
        this.setPredicate(predicate);
        return this;
    }
}

