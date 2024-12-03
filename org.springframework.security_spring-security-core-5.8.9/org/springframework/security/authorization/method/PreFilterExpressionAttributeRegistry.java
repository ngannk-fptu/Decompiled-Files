/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.expression.Expression
 *  org.springframework.lang.NonNull
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization.method;

import java.lang.reflect.Method;
import org.springframework.aop.support.AopUtils;
import org.springframework.expression.Expression;
import org.springframework.lang.NonNull;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.authorization.method.AbstractExpressionAttributeRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.util.Assert;

final class PreFilterExpressionAttributeRegistry
extends AbstractExpressionAttributeRegistry<PreFilterExpressionAttribute> {
    private final MethodSecurityExpressionHandler expressionHandler;

    PreFilterExpressionAttributeRegistry() {
        this.expressionHandler = new DefaultMethodSecurityExpressionHandler();
    }

    PreFilterExpressionAttributeRegistry(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.expressionHandler = expressionHandler;
    }

    MethodSecurityExpressionHandler getExpressionHandler() {
        return this.expressionHandler;
    }

    @Override
    @NonNull
    PreFilterExpressionAttribute resolveAttribute(Method method, Class<?> targetClass) {
        Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
        PreFilter preFilter = this.findPreFilterAnnotation(specificMethod);
        if (preFilter == null) {
            return PreFilterExpressionAttribute.NULL_ATTRIBUTE;
        }
        Expression preFilterExpression = this.expressionHandler.getExpressionParser().parseExpression(preFilter.value());
        return new PreFilterExpressionAttribute(preFilterExpression, preFilter.filterTarget());
    }

    private PreFilter findPreFilterAnnotation(Method method) {
        PreFilter preFilter = AuthorizationAnnotationUtils.findUniqueAnnotation(method, PreFilter.class);
        return preFilter != null ? preFilter : AuthorizationAnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), PreFilter.class);
    }

    static final class PreFilterExpressionAttribute
    extends ExpressionAttribute {
        static final PreFilterExpressionAttribute NULL_ATTRIBUTE = new PreFilterExpressionAttribute(null, null);
        private final String filterTarget;

        private PreFilterExpressionAttribute(Expression expression, String filterTarget) {
            super(expression);
            this.filterTarget = filterTarget;
        }

        String getFilterTarget() {
            return this.filterTarget;
        }
    }
}

