/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.expression.EvaluationContext
 */
package org.springframework.security.authorization.method;

import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.PreAuthorizeExpressionAttributeRegistry;
import org.springframework.security.core.Authentication;

public final class PreAuthorizeAuthorizationManager
implements AuthorizationManager<MethodInvocation> {
    private PreAuthorizeExpressionAttributeRegistry registry = new PreAuthorizeExpressionAttributeRegistry();

    public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
        this.registry = new PreAuthorizeExpressionAttributeRegistry(expressionHandler);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation mi) {
        Object attribute = this.registry.getAttribute(mi);
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return null;
        }
        EvaluationContext ctx = this.registry.getExpressionHandler().createEvaluationContext(authentication, mi);
        boolean granted = ExpressionUtils.evaluateAsBoolean(((ExpressionAttribute)attribute).getExpression(), ctx);
        return new ExpressionAuthorizationDecision(granted, ((ExpressionAttribute)attribute).getExpression());
    }
}

