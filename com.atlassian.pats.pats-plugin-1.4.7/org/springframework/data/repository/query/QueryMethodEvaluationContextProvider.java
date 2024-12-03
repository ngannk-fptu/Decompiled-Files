/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 */
package org.springframework.data.repository.query;

import java.util.Collections;
import org.springframework.data.repository.query.ExtensionAwareQueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;

public interface QueryMethodEvaluationContextProvider {
    public static final QueryMethodEvaluationContextProvider DEFAULT = new ExtensionAwareQueryMethodEvaluationContextProvider(Collections.emptyList());

    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T var1, Object[] var2);

    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T var1, Object[] var2, ExpressionDependencies var3);
}

