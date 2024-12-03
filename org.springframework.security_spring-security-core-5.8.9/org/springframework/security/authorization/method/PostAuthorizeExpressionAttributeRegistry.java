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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authorization.method.AbstractExpressionAttributeRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.util.Assert;
import reactor.util.annotation.NonNull;

final class PostAuthorizeExpressionAttributeRegistry
extends AbstractExpressionAttributeRegistry<ExpressionAttribute> {
    private final MethodSecurityExpressionHandler expressionHandler;

    PostAuthorizeExpressionAttributeRegistry() {
        this(new DefaultMethodSecurityExpressionHandler());
    }

    PostAuthorizeExpressionAttributeRegistry(MethodSecurityExpressionHandler expressionHandler) {
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
        PostAuthorize postAuthorize = this.findPostAuthorizeAnnotation(specificMethod);
        if (postAuthorize == null) {
            return ExpressionAttribute.NULL_ATTRIBUTE;
        }
        Expression postAuthorizeExpression = this.expressionHandler.getExpressionParser().parseExpression(postAuthorize.value());
        return new ExpressionAttribute(postAuthorizeExpression);
    }

    private PostAuthorize findPostAuthorizeAnnotation(Method method) {
        PostAuthorize postAuthorize = AuthorizationAnnotationUtils.findUniqueAnnotation(method, PostAuthorize.class);
        return postAuthorize != null ? postAuthorize : AuthorizationAnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), PostAuthorize.class);
    }
}

