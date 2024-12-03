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
import org.springframework.security.access.prepost.PostInvocationAttribute;

@Deprecated
class PostInvocationExpressionAttribute
extends AbstractExpressionBasedMethodConfigAttribute
implements PostInvocationAttribute {
    PostInvocationExpressionAttribute(String filterExpression, String authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);
    }

    PostInvocationExpressionAttribute(Expression filterExpression, Expression authorizeExpression) throws ParseException {
        super(filterExpression, authorizeExpression);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Expression authorize = this.getAuthorizeExpression();
        Expression filter = this.getFilterExpression();
        sb.append("[authorize: '").append(authorize != null ? authorize.getExpressionString() : "null");
        sb.append("', filter: '").append(filter != null ? filter.getExpressionString() : "null").append("']");
        return sb.toString();
    }
}

