/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheEvaluationContext;
import org.springframework.cache.interceptor.CacheExpressionRootObject;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

class CacheOperationExpressionEvaluator
extends CachedExpressionEvaluator {
    public static final Object NO_RESULT = new Object();
    public static final Object RESULT_UNAVAILABLE = new Object();
    public static final String RESULT_VARIABLE = "result";
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> keyCache = new ConcurrentHashMap<CachedExpressionEvaluator.ExpressionKey, Expression>(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<CachedExpressionEvaluator.ExpressionKey, Expression>(64);
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> unlessCache = new ConcurrentHashMap<CachedExpressionEvaluator.ExpressionKey, Expression>(64);

    CacheOperationExpressionEvaluator() {
    }

    public EvaluationContext createEvaluationContext(Collection<? extends Cache> caches, Method method, Object[] args, Object target, Class<?> targetClass, Method targetMethod, @Nullable Object result, @Nullable BeanFactory beanFactory) {
        CacheExpressionRootObject rootObject = new CacheExpressionRootObject(caches, method, args, target, targetClass);
        CacheEvaluationContext evaluationContext = new CacheEvaluationContext(rootObject, targetMethod, args, this.getParameterNameDiscoverer());
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        } else if (result != NO_RESULT) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    @Nullable
    public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return this.getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }

    public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(this.getExpression(this.conditionCache, methodKey, conditionExpression).getValue(evalContext, Boolean.class));
    }

    public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return Boolean.TRUE.equals(this.getExpression(this.unlessCache, methodKey, unlessExpression).getValue(evalContext, Boolean.class));
    }

    void clear() {
        this.keyCache.clear();
        this.conditionCache.clear();
        this.unlessCache.clear();
    }
}

