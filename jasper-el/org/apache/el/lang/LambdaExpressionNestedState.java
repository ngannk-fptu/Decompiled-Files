/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.lang;

public final class LambdaExpressionNestedState {
    private int nestingCount = 0;
    private boolean hasFormalParameters = false;

    public void incrementNestingCount() {
        ++this.nestingCount;
    }

    public int getNestingCount() {
        return this.nestingCount;
    }

    public void setHasFormalParameters() {
        this.hasFormalParameters = true;
    }

    public boolean getHasFormalParameters() {
        return this.hasFormalParameters;
    }
}

