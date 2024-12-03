/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;

public abstract class AbstractNode
implements Serializable {
    private final CriteriaBuilderImpl criteriaBuilder;

    public AbstractNode(CriteriaBuilderImpl criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    public CriteriaBuilderImpl criteriaBuilder() {
        return this.criteriaBuilder;
    }
}

