/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.expression.method;

import java.util.Collection;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.PreInvocationExpressionAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

@Deprecated
public class ExpressionBasedPreInvocationAdvice
implements PreInvocationAuthorizationAdvice {
    private MethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

    @Override
    public boolean before(Authentication authentication, MethodInvocation mi, PreInvocationAttribute attr) {
        PreInvocationExpressionAttribute preAttr = (PreInvocationExpressionAttribute)attr;
        EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, mi);
        Expression preFilter = preAttr.getFilterExpression();
        Expression preAuthorize = preAttr.getAuthorizeExpression();
        if (preFilter != null) {
            Object filterTarget = this.findFilterTarget(preAttr.getFilterTarget(), ctx, mi);
            this.expressionHandler.filter(filterTarget, preFilter, ctx);
        }
        return preAuthorize != null ? ExpressionUtils.evaluateAsBoolean(preAuthorize, ctx) : true;
    }

    private Object findFilterTarget(String filterTargetName, EvaluationContext ctx, MethodInvocation invocation) {
        Object filterTarget = null;
        if (filterTargetName.length() > 0) {
            filterTarget = ctx.lookupVariable(filterTargetName);
            Assert.notNull((Object)filterTarget, () -> "Filter target was null, or no argument with name " + filterTargetName + " found in method");
        } else if (invocation.getArguments().length == 1) {
            Object arg = invocation.getArguments()[0];
            if (arg.getClass().isArray() || arg instanceof Collection) {
                filterTarget = arg;
            }
            Assert.notNull((Object)filterTarget, () -> "A PreFilter expression was set but the method argument type" + arg.getClass() + " is not filterable");
        } else if (invocation.getArguments().length > 1) {
            throw new IllegalArgumentException("Unable to determine the method argument for filtering. Specify the filter target.");
        }
        Assert.isTrue((!filterTarget.getClass().isArray() ? 1 : 0) != 0, (String)"Pre-filtering on array types is not supported. Using a Collection will solve this problem");
        return filterTarget;
    }

    public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }
}

