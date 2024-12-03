/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.expression.method;

import org.springframework.security.access.expression.SecurityExpressionOperations;

public interface MethodSecurityExpressionOperations
extends SecurityExpressionOperations {
    public void setFilterObject(Object var1);

    public Object getFilterObject();

    public void setReturnObject(Object var1);

    public Object getReturnObject();

    public Object getThis();
}

