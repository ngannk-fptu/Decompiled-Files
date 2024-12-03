/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.data.spel.ExtensionAwareEvaluationContextProvider;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ExtensionAwareQueryMethodEvaluationContextProvider
implements QueryMethodEvaluationContextProvider {
    private final ExtensionAwareEvaluationContextProvider delegate;

    public ExtensionAwareQueryMethodEvaluationContextProvider(ListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"ListableBeanFactory must not be null!");
        this.delegate = new ExtensionAwareEvaluationContextProvider(beanFactory);
    }

    public ExtensionAwareQueryMethodEvaluationContextProvider(List<? extends EvaluationContextExtension> extensions) {
        Assert.notNull(extensions, (String)"EvaluationContextExtensions must not be null!");
        this.delegate = new ExtensionAwareEvaluationContextProvider(extensions);
    }

    @Override
    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues) {
        StandardEvaluationContext evaluationContext = this.delegate.getEvaluationContext(parameterValues);
        evaluationContext.setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues));
        return evaluationContext;
    }

    @Override
    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues, ExpressionDependencies dependencies) {
        StandardEvaluationContext evaluationContext = this.delegate.getEvaluationContext(parameterValues, dependencies);
        evaluationContext.setVariables(ExtensionAwareQueryMethodEvaluationContextProvider.collectVariables(parameters, parameterValues));
        return evaluationContext;
    }

    static Map<String, Object> collectVariables(Parameters<?, ?> parameters, Object[] arguments) {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        parameters.stream().filter(Parameter::isSpecialParameter).forEach(it -> variables.put(StringUtils.uncapitalize((String)it.getType().getSimpleName()), arguments[it.getIndex()]));
        parameters.stream().filter(Parameter::isNamedParameter).forEach(it -> variables.put(it.getName().orElseThrow(() -> new IllegalStateException("Should never occur!")), arguments[it.getIndex()]));
        return variables;
    }
}

