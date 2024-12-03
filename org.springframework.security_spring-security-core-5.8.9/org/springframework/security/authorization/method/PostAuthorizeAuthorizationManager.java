/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 */
package org.springframework.security.authorization.method;

import java.util.function.Supplier;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.authorization.method.PostAuthorizeExpressionAttributeRegistry;
import org.springframework.security.core.Authentication;

public final class PostAuthorizeAuthorizationManager
implements AuthorizationManager<MethodInvocationResult> {
    private PostAuthorizeExpressionAttributeRegistry registry = new PostAuthorizeExpressionAttributeRegistry();

    public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
        this.registry = new PostAuthorizeExpressionAttributeRegistry(expressionHandler);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocationResult mi) {
        Object attribute = this.registry.getAttribute(mi.getMethodInvocation());
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return null;
        }
        MethodSecurityExpressionHandler expressionHandler = this.registry.getExpressionHandler();
        EvaluationContext ctx = expressionHandler.createEvaluationContext(authentication, mi.getMethodInvocation());
        expressionHandler.setReturnObject(mi.getResult(), ctx);
        boolean granted = ExpressionUtils.evaluateAsBoolean(((ExpressionAttribute)attribute).getExpression(), ctx);
        return new ExpressionAuthorizationDecision(granted, ((ExpressionAttribute)attribute).getExpression());
    }
}

