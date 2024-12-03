/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.ExpressionAttributeAuthorizationDecision;
import org.springframework.security.authorization.method.PreAuthorizeExpressionAttributeRegistry;
import org.springframework.security.authorization.method.ReactiveExpressionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public final class PreAuthorizeReactiveAuthorizationManager
implements ReactiveAuthorizationManager<MethodInvocation> {
    private final PreAuthorizeExpressionAttributeRegistry registry;

    public PreAuthorizeReactiveAuthorizationManager() {
        this(new DefaultMethodSecurityExpressionHandler());
    }

    public PreAuthorizeReactiveAuthorizationManager(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.registry = new PreAuthorizeExpressionAttributeRegistry(expressionHandler);
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, MethodInvocation mi) {
        Object attribute = this.registry.getAttribute(mi);
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return Mono.empty();
        }
        return authentication.map(auth -> this.registry.getExpressionHandler().createEvaluationContext((Authentication)auth, mi)).flatMap(ctx -> ReactiveExpressionUtils.evaluateAsBoolean(attribute.getExpression(), ctx)).map(granted -> new ExpressionAttributeAuthorizationDecision((boolean)granted, (ExpressionAttribute)attribute));
    }
}

