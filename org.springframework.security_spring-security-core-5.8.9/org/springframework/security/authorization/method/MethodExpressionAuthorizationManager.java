/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization.method;

import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.ExpressionAuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public final class MethodExpressionAuthorizationManager
implements AuthorizationManager<MethodInvocation> {
    private SecurityExpressionHandler<MethodInvocation> expressionHandler = new DefaultMethodSecurityExpressionHandler();
    private Expression expression;

    public MethodExpressionAuthorizationManager(String expressionString) {
        Assert.hasText((String)expressionString, (String)"expressionString cannot be empty");
        this.expression = this.expressionHandler.getExpressionParser().parseExpression(expressionString);
    }

    public void setExpressionHandler(SecurityExpressionHandler<MethodInvocation> expressionHandler) {
        Assert.notNull(expressionHandler, (String)"expressionHandler cannot be null");
        this.expressionHandler = expressionHandler;
        this.expression = expressionHandler.getExpressionParser().parseExpression(this.expression.getExpressionString());
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation context) {
        EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, context);
        boolean granted = ExpressionUtils.evaluateAsBoolean(this.expression, ctx);
        return new ExpressionAuthorizationDecision(granted, this.expression);
    }

    public String toString() {
        return "WebExpressionAuthorizationManager[expression='" + this.expression + "']";
    }
}

