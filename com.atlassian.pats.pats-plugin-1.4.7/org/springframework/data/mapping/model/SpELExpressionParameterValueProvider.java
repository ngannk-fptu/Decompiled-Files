/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.SpELExpressionEvaluator;
import org.springframework.lang.Nullable;

public class SpELExpressionParameterValueProvider<P extends PersistentProperty<P>>
implements ParameterValueProvider<P> {
    private final SpELExpressionEvaluator evaluator;
    private final ConversionService conversionService;
    private final ParameterValueProvider<P> delegate;

    public SpELExpressionParameterValueProvider(SpELExpressionEvaluator evaluator, ConversionService conversionService, ParameterValueProvider<P> delegate) {
        this.evaluator = evaluator;
        this.conversionService = conversionService;
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public <T> T getParameterValue(PreferredConstructor.Parameter<T, P> parameter) {
        if (!parameter.hasSpelExpression()) {
            return this.delegate == null ? null : (T)this.delegate.getParameterValue(parameter);
        }
        Object object = this.evaluator.evaluate(parameter.getSpelExpression());
        return object == null ? null : (T)this.potentiallyConvertSpelValue(object, parameter);
    }

    @Nullable
    protected <T> T potentiallyConvertSpelValue(Object object, PreferredConstructor.Parameter<T, P> parameter) {
        return (T)this.conversionService.convert(object, parameter.getRawType());
    }
}

