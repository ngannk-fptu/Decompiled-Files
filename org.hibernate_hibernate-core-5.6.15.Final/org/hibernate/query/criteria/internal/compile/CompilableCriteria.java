/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.compile;

import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.RenderingContext;

public interface CompilableCriteria {
    public void validate();

    public CriteriaInterpretation interpret(RenderingContext var1);
}

