/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import java.util.Iterator;

public abstract class BinaryExp
extends Expression {
    public final Expression exp1;
    public final Expression exp2;
    private static final long serialVersionUID = 1L;

    public BinaryExp(Expression left, Expression right) {
        super(left.hashCode() + right.hashCode());
        this.exp1 = left;
        this.exp2 = right;
    }

    protected final int calcHashCode() {
        return this.exp1.hashCode() + this.exp2.hashCode();
    }

    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        BinaryExp rhs = (BinaryExp)o;
        return rhs.exp1 == this.exp1 && rhs.exp2 == this.exp2;
    }

    public Expression[] getChildren() {
        int cnt = 1;
        Expression exp = this;
        while (exp.getClass() == this.getClass()) {
            ++cnt;
            exp = ((BinaryExp)exp).exp1;
        }
        Expression[] r = new Expression[cnt];
        exp = this;
        while (exp.getClass() == this.getClass()) {
            r[--cnt] = ((BinaryExp)exp).exp2;
            exp = ((BinaryExp)exp).exp1;
        }
        r[0] = exp;
        return r;
    }

    public Iterator children() {
        final Expression[] items = this.getChildren();
        return new Iterator(){
            private int idx = 0;

            public Object next() {
                return items[this.idx++];
            }

            public boolean hasNext() {
                return this.idx != items.length;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

