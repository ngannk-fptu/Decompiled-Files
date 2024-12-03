/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventExpressionRootObject;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

class EventExpressionEvaluator
extends CachedExpressionEvaluator {
    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<CachedExpressionEvaluator.ExpressionKey, Expression>(64);

    EventExpressionEvaluator() {
    }

    public boolean condition(String conditionExpression, ApplicationEvent event, Method targetMethod, AnnotatedElementKey methodKey, Object[] args, @Nullable BeanFactory beanFactory) {
        EventExpressionRootObject root = new EventExpressionRootObject(event, args);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(root, targetMethod, args, this.getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return Boolean.TRUE.equals(this.getExpression(this.conditionCache, methodKey, conditionExpression).getValue((EvaluationContext)evaluationContext, Boolean.class));
    }
}

