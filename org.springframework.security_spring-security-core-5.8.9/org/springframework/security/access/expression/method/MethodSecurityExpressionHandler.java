/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 */
package org.springframework.security.access.expression.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.SecurityExpressionHandler;

public interface MethodSecurityExpressionHandler
extends SecurityExpressionHandler<MethodInvocation> {
    public Object filter(Object var1, Expression var2, EvaluationContext var3);

    public void setReturnObject(Object var1, EvaluationContext var2);
}

