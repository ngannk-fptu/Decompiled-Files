/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.ParseException
 */
package org.springframework.security.access.expression.method;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.PostInvocationExpressionAttribute;
import org.springframework.security.access.expression.method.PreInvocationExpressionAttribute;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PrePostInvocationAttributeFactory;

@Deprecated
public class ExpressionBasedAnnotationAttributeFactory
implements PrePostInvocationAttributeFactory {
    private final Object parserLock = new Object();
    private ExpressionParser parser;
    private MethodSecurityExpressionHandler handler;

    public ExpressionBasedAnnotationAttributeFactory(MethodSecurityExpressionHandler handler) {
        this.handler = handler;
    }

    @Override
    public PreInvocationAttribute createPreInvocationAttribute(String preFilterAttribute, String filterObject, String preAuthorizeAttribute) {
        try {
            ExpressionParser parser = this.getParser();
            Expression preAuthorizeExpression = preAuthorizeAttribute != null ? parser.parseExpression(preAuthorizeAttribute) : parser.parseExpression("permitAll");
            Expression preFilterExpression = preFilterAttribute != null ? parser.parseExpression(preFilterAttribute) : null;
            return new PreInvocationExpressionAttribute(preFilterExpression, filterObject, preAuthorizeExpression);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Failed to parse expression '" + ex.getExpressionString() + "'", ex);
        }
    }

    @Override
    public PostInvocationAttribute createPostInvocationAttribute(String postFilterAttribute, String postAuthorizeAttribute) {
        try {
            Expression postFilterExpression;
            ExpressionParser parser = this.getParser();
            Expression postAuthorizeExpression = postAuthorizeAttribute != null ? parser.parseExpression(postAuthorizeAttribute) : null;
            Expression expression = postFilterExpression = postFilterAttribute != null ? parser.parseExpression(postFilterAttribute) : null;
            if (postFilterExpression != null || postAuthorizeExpression != null) {
                return new PostInvocationExpressionAttribute(postFilterExpression, postAuthorizeExpression);
            }
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Failed to parse expression '" + ex.getExpressionString() + "'", ex);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ExpressionParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        Object object = this.parserLock;
        synchronized (object) {
            this.parser = this.handler.getExpressionParser();
            this.handler = null;
        }
        return this.parser;
    }
}

