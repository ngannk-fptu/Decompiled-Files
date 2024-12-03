/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.support.ExpressionPointcut;
import org.springframework.lang.Nullable;

public abstract class AbstractExpressionPointcut
implements ExpressionPointcut,
Serializable {
    @Nullable
    private String location;
    @Nullable
    private String expression;

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public String getLocation() {
        return this.location;
    }

    public void setExpression(@Nullable String expression) {
        this.expression = expression;
        try {
            this.onSetExpression(expression);
        }
        catch (IllegalArgumentException ex) {
            if (this.location != null) {
                throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
            }
            throw ex;
        }
    }

    protected void onSetExpression(@Nullable String expression) throws IllegalArgumentException {
    }

    @Override
    @Nullable
    public String getExpression() {
        return this.expression;
    }
}

