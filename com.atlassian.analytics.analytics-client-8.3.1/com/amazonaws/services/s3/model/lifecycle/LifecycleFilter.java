/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.lifecycle;

import com.amazonaws.services.s3.model.lifecycle.LifecycleFilterPredicate;
import java.io.Serializable;

public class LifecycleFilter
implements Serializable {
    private LifecycleFilterPredicate predicate;

    public LifecycleFilter() {
    }

    public LifecycleFilter(LifecycleFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public LifecycleFilterPredicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(LifecycleFilterPredicate predicate) {
        this.predicate = predicate;
    }

    public LifecycleFilter withPredicate(LifecycleFilterPredicate predicate) {
        this.setPredicate(predicate);
        return this;
    }
}

