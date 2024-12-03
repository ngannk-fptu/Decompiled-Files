/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SpELContext {
    private final SpelExpressionParser parser;
    private final PropertyAccessor accessor;
    @Nullable
    private final BeanFactory factory;

    public SpELContext(PropertyAccessor accessor) {
        this(accessor, null, null);
    }

    public SpELContext(SpelExpressionParser parser, PropertyAccessor accessor) {
        this(accessor, parser, null);
    }

    public SpELContext(SpELContext source, BeanFactory factory) {
        this(source.accessor, source.parser, factory);
    }

    private SpELContext(PropertyAccessor accessor, @Nullable SpelExpressionParser parser, @Nullable BeanFactory factory) {
        Assert.notNull((Object)accessor, (String)"PropertyAccessor must not be null!");
        this.parser = parser == null ? new SpelExpressionParser() : parser;
        this.accessor = accessor;
        this.factory = factory;
    }

    public ExpressionParser getParser() {
        return this.parser;
    }

    public EvaluationContext getEvaluationContext(Object source) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(source);
        evaluationContext.addPropertyAccessor(this.accessor);
        if (this.factory != null) {
            evaluationContext.setBeanResolver((BeanResolver)new BeanFactoryResolver(this.factory));
        }
        return evaluationContext;
    }
}

