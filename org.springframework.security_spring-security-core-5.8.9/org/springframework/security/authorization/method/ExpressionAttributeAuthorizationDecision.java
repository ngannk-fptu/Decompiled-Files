/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization.method;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.method.ExpressionAttribute;

@Deprecated
public class ExpressionAttributeAuthorizationDecision
extends AuthorizationDecision {
    private final ExpressionAttribute expressionAttribute;

    public ExpressionAttributeAuthorizationDecision(boolean granted, ExpressionAttribute expressionAttribute) {
        super(granted);
        this.expressionAttribute = expressionAttribute;
    }

    public ExpressionAttribute getExpressionAttribute() {
        return this.expressionAttribute;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [granted=" + this.isGranted() + ", expressionAttribute=" + this.expressionAttribute + ']';
    }
}

