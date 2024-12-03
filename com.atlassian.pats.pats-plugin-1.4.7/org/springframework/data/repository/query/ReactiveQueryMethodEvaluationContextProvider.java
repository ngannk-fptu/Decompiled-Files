/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.repository.query;

import java.util.Collections;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.ReactiveExtensionAwareQueryMethodEvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import reactor.core.publisher.Mono;

public interface ReactiveQueryMethodEvaluationContextProvider
extends QueryMethodEvaluationContextProvider {
    public static final ReactiveQueryMethodEvaluationContextProvider DEFAULT = new ReactiveExtensionAwareQueryMethodEvaluationContextProvider(Collections.emptyList());

    public <T extends Parameters<?, ?>> Mono<EvaluationContext> getEvaluationContextLater(T var1, Object[] var2);

    public <T extends Parameters<?, ?>> Mono<EvaluationContext> getEvaluationContextLater(T var1, Object[] var2, ExpressionDependencies var3);
}

