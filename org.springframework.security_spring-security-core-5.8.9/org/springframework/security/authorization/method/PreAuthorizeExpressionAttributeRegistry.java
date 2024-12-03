/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.expression.Expression
 *  org.springframework.util.Assert
 *  reactor.util.annotation.NonNull
 */
package org.springframework.security.authorization.method;

import java.lang.reflect.Method;
import org.springframework.aop.support.AopUtils;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AbstractExpressionAttributeRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.util.Assert;
import reactor.util.annotation.NonNull;

final class PreAuthorizeExpressionAttributeRegistry
extends AbstractExpressionAttributeRegistry<ExpressionAttribute> {
    private final MethodSecurityExpressionHandler expressionHandler;

    PreAuthorizeExpressionAttributeRegistry() {
        this.expressionHandler = new DefaultMethodSecurityExpressionHandler();
    }

    PreAuthorizeExpressionAttributeRegistry(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.expressionHandler = expressionHandler;
    }

    MethodSecurityExpressionHandler getExpressionHandler() {
        return this.expressionHandler;
    }

    @Override
    @NonNull
    ExpressionAttribute resolveAttribute(Method method, Class<?> targetClass) {
        Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
        PreAuthorize preAuthorize = this.findPreAuthorizeAnnotation(specificMethod);
        if (preAuthorize == null) {
            return ExpressionAttribute.NULL_ATTRIBUTE;
        }
        Expression preAuthorizeExpression = this.expressionHandler.getExpressionParser().parseExpression(preAuthorize.value());
        return new ExpressionAttribute(preAuthorizeExpression);
    }

    private PreAuthorize findPreAuthorizeAnnotation(Method method) {
        PreAuthorize preAuthorize = AuthorizationAnnotationUtils.findUniqueAnnotation(method, PreAuthorize.class);
        return preAuthorize != null ? preAuthorize : AuthorizationAnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), PreAuthorize.class);
    }
}

