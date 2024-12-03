/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.ExpressionAttributeAuthorizationDecision;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.authorization.method.PostAuthorizeExpressionAttributeRegistry;
import org.springframework.security.authorization.method.ReactiveExpressionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public final class PostAuthorizeReactiveAuthorizationManager
implements ReactiveAuthorizationManager<MethodInvocationResult> {
    private final PostAuthorizeExpressionAttributeRegistry registry;

    public PostAuthorizeReactiveAuthorizationManager() {
        this(new DefaultMethodSecurityExpressionHandler());
    }

    public PostAuthorizeReactiveAuthorizationManager(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.registry = new PostAuthorizeExpressionAttributeRegistry(expressionHandler);
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, MethodInvocationResult result) {
        MethodInvocation mi = result.getMethodInvocation();
        Object attribute = this.registry.getAttribute(mi);
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return Mono.empty();
        }
        MethodSecurityExpressionHandler expressionHandler = this.registry.getExpressionHandler();
        return authentication.map(auth -> expressionHandler.createEvaluationContext((Authentication)auth, mi)).doOnNext(ctx -> expressionHandler.setReturnObject(result.getResult(), (EvaluationContext)ctx)).flatMap(ctx -> ReactiveExpressionUtils.evaluateAsBoolean(attribute.getExpression(), ctx)).map(granted -> new ExpressionAttributeAuthorizationDecision((boolean)granted, (ExpressionAttribute)attribute));
    }
}

