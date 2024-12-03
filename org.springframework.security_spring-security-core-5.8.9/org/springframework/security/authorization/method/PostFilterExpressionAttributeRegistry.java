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
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.authorization.method.AbstractExpressionAttributeRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.util.Assert;

final class PostFilterExpressionAttributeRegistry
extends AbstractExpressionAttributeRegistry<ExpressionAttribute> {
    private final MethodSecurityExpressionHandler expressionHandler;

    PostFilterExpressionAttributeRegistry() {
        this.expressionHandler = new DefaultMethodSecurityExpressionHandler();
    }

    PostFilterExpressionAttributeRegistry(MethodSecurityExpressionHandler expressionHandler) {
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
        PostFilter postFilter = this.findPostFilterAnnotation(specificMethod);
        if (postFilter == null) {
            return ExpressionAttribute.NULL_ATTRIBUTE;
        }
        Expression postFilterExpression = this.expressionHandler.getExpressionParser().parseExpression(postFilter.value());
        return new ExpressionAttribute(postFilterExpression);
    }

    private PostFilter findPostFilterAnnotation(Method method) {
        PostFilter postFilter = AuthorizationAnnotationUtils.findUniqueAnnotation(method, PostFilter.class);
        return postFilter != null ? postFilter : AuthorizationAnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), PostFilter.class);
    }
}

