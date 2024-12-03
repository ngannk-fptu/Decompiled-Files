/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 */
package org.springframework.security.access.expression.method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.PostInvocationExpressionAttribute;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

@Deprecated
public class ExpressionBasedPostInvocationAdvice
implements PostInvocationAuthorizationAdvice {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final MethodSecurityExpressionHandler expressionHandler;

    public ExpressionBasedPostInvocationAdvice(MethodSecurityExpressionHandler expressionHandler) {
        this.expressionHandler = expressionHandler;
    }

    @Override
    public Object after(Authentication authentication, MethodInvocation mi, PostInvocationAttribute postAttr, Object returnedObject) throws AccessDeniedException {
        PostInvocationExpressionAttribute pia = (PostInvocationExpressionAttribute)postAttr;
        EvaluationContext ctx = this.expressionHandler.createEvaluationContext(authentication, mi);
        Expression postFilter = pia.getFilterExpression();
        Expression postAuthorize = pia.getAuthorizeExpression();
        if (postFilter != null) {
            this.logger.debug((Object)LogMessage.format((String)"Applying PostFilter expression %s", (Object)postFilter));
            if (returnedObject != null) {
                returnedObject = this.expressionHandler.filter(returnedObject, postFilter, ctx);
            } else {
                this.logger.debug((Object)"Return object is null, filtering will be skipped");
            }
        }
        this.expressionHandler.setReturnObject(returnedObject, ctx);
        if (postAuthorize != null && !ExpressionUtils.evaluateAsBoolean(postAuthorize, ctx)) {
            this.logger.debug((Object)"PostAuthorize expression rejected access");
            throw new AccessDeniedException("Access is denied");
        }
        return returnedObject;
    }
}

