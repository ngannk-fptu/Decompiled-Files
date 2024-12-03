/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ParseException
 */
package org.springframework.security.access.expression.method;

import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.method.AbstractExpressionBasedMethodConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

@Deprecated
class PreInvocationExpressionAttribute
extends AbstractExpressionBasedMethodConfigAttribute
implements PreInvocationAttribute {
    private final String filterTarget;

    PreInvocationExpressionAttribute(String filterExpression, String filterTarget, String authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);
        this.filterTarget = filterTarget;
    }

    PreInvocationExpressionAttribute(Expression filterExpression, String filterTarget, Expression authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);
        this.filterTarget = filterTarget;
    }

    String getFilterTarget() {
        return this.filterTarget;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Expression authorize = this.getAuthorizeExpression();
        Expression filter = this.getFilterExpression();
        sb.append("[authorize: '").append(authorize != null ? authorize.getExpressionString() : "null");
        sb.append("', filter: '").append(filter != null ? filter.getExpressionString() : "null");
        sb.append("', filterTarget: '").append(this.filterTarget).append("']");
        return sb.toString();
    }
}

