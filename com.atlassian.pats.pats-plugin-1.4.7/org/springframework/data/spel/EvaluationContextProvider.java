/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package org.springframework.data.spel;

import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@FunctionalInterface
public interface EvaluationContextProvider {
    public static final EvaluationContextProvider DEFAULT = rootObject -> rootObject == null ? new StandardEvaluationContext() : new StandardEvaluationContext(rootObject);

    public EvaluationContext getEvaluationContext(Object var1);

    default public EvaluationContext getEvaluationContext(Object rootObject, ExpressionDependencies dependencies) {
        return this.getEvaluationContext(rootObject);
    }
}

