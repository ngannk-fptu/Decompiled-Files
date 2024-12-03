/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.spel;

import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import reactor.core.publisher.Mono;

public interface ReactiveEvaluationContextProvider
extends EvaluationContextProvider {
    public Mono<? extends EvaluationContext> getEvaluationContextLater(Object var1);

    default public Mono<? extends EvaluationContext> getEvaluationContextLater(Object rootObject, ExpressionDependencies dependencies) {
        return this.getEvaluationContextLater(rootObject);
    }
}

