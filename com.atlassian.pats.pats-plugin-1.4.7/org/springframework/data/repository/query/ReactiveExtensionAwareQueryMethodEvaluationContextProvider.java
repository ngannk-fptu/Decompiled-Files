/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.repository.query;

import java.util.List;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.query.ExtensionAwareQueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ReactiveQueryMethodEvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.spel.ReactiveExtensionAwareEvaluationContextProvider;
import org.springframework.data.spel.spi.ExtensionIdAware;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class ReactiveExtensionAwareQueryMethodEvaluationContextProvider
implements ReactiveQueryMethodEvaluationContextProvider {
    private final ReactiveExtensionAwareEvaluationContextProvider delegate;

    public ReactiveExtensionAwareQueryMethodEvaluationContextProvider(ListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"ListableBeanFactory must not be null!");
        this.delegate = new ReactiveExtensionAwareEvaluationContextProvider(beanFactory);
    }

    public ReactiveExtensionAwareQueryMethodEvaluationContextProvider(List<? extends ExtensionIdAware> extensions) {
        Assert.notNull(extensions, (String)"EvaluationContextExtensions must not be null!");
        this.delegate = new ReactiveExtensionAwareEvaluationContextProvider(extensions);
    }

    @Override
    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues) {
        EvaluationContext evaluationContext = this.delegate.getEvaluationContext(parameterValues);
        if (evaluationContext instanceof StandardEvaluationContext) {
            ((StandardEvaluationContext)evaluationContext).setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues));
        }
        return evaluationContext;
    }

    @Override
    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues, ExpressionDependencies dependencies) {
        EvaluationContext evaluationContext = this.delegate.getEvaluationContext(parameterValues, dependencies);
        if (evaluationContext instanceof StandardEvaluationContext) {
            ((StandardEvaluationContext)evaluationContext).setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues));
        }
        return evaluationContext;
    }

    @Override
    public <T extends Parameters<?, ?>> Mono<EvaluationContext> getEvaluationContextLater(T parameters, Object[] parameterValues) {
        Mono<StandardEvaluationContext> evaluationContext = this.delegate.getEvaluationContextLater(parameterValues);
        return evaluationContext.doOnNext(it -> it.setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues))).cast(EvaluationContext.class);
    }

    @Override
    public <T extends Parameters<?, ?>> Mono<EvaluationContext> getEvaluationContextLater(T parameters, Object[] parameterValues, ExpressionDependencies dependencies) {
        Mono<StandardEvaluationContext> evaluationContext = this.delegate.getEvaluationContextLater(parameterValues, dependencies);
        return evaluationContext.doOnNext(it -> it.setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues))).cast(EvaluationContext.class);
    }
}

