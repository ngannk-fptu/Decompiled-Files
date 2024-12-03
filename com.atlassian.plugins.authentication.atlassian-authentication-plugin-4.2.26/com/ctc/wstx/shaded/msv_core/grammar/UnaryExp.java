/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

public abstract class UnaryExp
extends Expression {
    public final Expression exp;
    private static final long serialVersionUID = 1L;

    protected UnaryExp(Expression exp) {
        super(exp.hashCode());
        this.exp = exp;
    }

    protected final int calcHashCode() {
        return this.exp.hashCode();
    }

    public boolean equals(Object o) {
        if (!this.getClass().equals(o.getClass())) {
            return false;
        }
        return ((UnaryExp)o).exp == this.exp;
    }
}

