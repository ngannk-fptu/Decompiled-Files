/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.ExpressionParser
 */
package org.springframework.security.access.expression;

import java.util.function.Supplier;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.core.Authentication;

public interface SecurityExpressionHandler<T>
extends AopInfrastructureBean {
    public ExpressionParser getExpressionParser();

    public EvaluationContext createEvaluationContext(Authentication var1, T var2);

    default public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, T invocation) {
        return this.createEvaluationContext(authentication.get(), invocation);
    }
}

