/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.util.RefExpRemover;
import java.io.Serializable;

public abstract class Expression
implements Serializable {
    private Boolean epsilonReducibility;
    private Expression expandedExp = null;
    public transient Object verifierTag = null;
    private transient int cachedHashCode;
    public static final Expression epsilon = new EpsilonExpression();
    public static final Expression nullSet = new NullSetExpression();
    public static final Expression anyString = new AnyStringExpression();
    private static final long serialVersionUID = -569561418606215601L;

    public boolean isEpsilonReducible() {
        if (this.epsilonReducibility == null) {
            this.epsilonReducibility = this.calcEpsilonReducibility() ? Boolean.TRUE : Boolean.FALSE;
        }
        return this.epsilonReducibility;
    }

    protected abstract boolean calcEpsilonReducibility();

    public Expression getExpandedExp(ExpressionPool pool) {
        if (this.expandedExp == null) {
            this.expandedExp = this.visit(new RefExpRemover(pool, false));
        }
        return this.expandedExp;
    }

    public final Expression peelOccurence() {
        if (this instanceof ChoiceExp) {
            ChoiceExp cexp = (ChoiceExp)this;
            if (cexp.exp1 == epsilon) {
                return cexp.exp2.peelOccurence();
            }
            if (cexp.exp2 == epsilon) {
                return cexp.exp1.peelOccurence();
            }
        }
        if (this instanceof OneOrMoreExp) {
            return ((OneOrMoreExp)this).exp.peelOccurence();
        }
        return this;
    }

    protected Expression(int hashCode) {
        this.setHashCode(hashCode);
    }

    protected Expression() {
        this.cachedHashCode = System.identityHashCode(this);
    }

    public abstract Object visit(ExpressionVisitor var1);

    public abstract Expression visit(ExpressionVisitorExpression var1);

    public abstract boolean visit(ExpressionVisitorBoolean var1);

    public abstract void visit(ExpressionVisitorVoid var1);

    public Object visit(RELAXExpressionVisitor visitor) {
        return this.visit((ExpressionVisitor)visitor);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return this.visit((ExpressionVisitorExpression)visitor);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return this.visit((ExpressionVisitorBoolean)visitor);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        this.visit((ExpressionVisitorVoid)visitor);
    }

    public final int hashCode() {
        return this.cachedHashCode;
    }

    private final void setHashCode(int hashCode) {
        this.cachedHashCode = hashCode ^ this.getClass().hashCode();
    }

    protected abstract int calcHashCode();

    public abstract boolean equals(Object var1);

    protected static int hashCode(Object o1, Object o2, int hashKey) {
        return o1.hashCode() + o2.hashCode() + hashKey;
    }

    protected static int hashCode(Object o, int hashKey) {
        return o.hashCode() + hashKey;
    }

    protected Object readResolve() {
        this.setHashCode(this.calcHashCode());
        return this;
    }

    private static class AnyStringExpression
    extends Expression {
        private static final long serialVersionUID = 1L;

        AnyStringExpression() {
        }

        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        public Object visit(ExpressionVisitor visitor) {
            return visitor.onAnyString();
        }

        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onAnyString();
        }

        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onAnyString();
        }

        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onAnyString();
        }

        protected boolean calcEpsilonReducibility() {
            return true;
        }

        public boolean equals(Object o) {
            return this == o;
        }

        protected Object readResolve() {
            return anyString;
        }
    }

    private static class NullSetExpression
    extends Expression {
        private static final long serialVersionUID = 1L;

        NullSetExpression() {
        }

        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        public Object visit(ExpressionVisitor visitor) {
            return visitor.onNullSet();
        }

        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onNullSet();
        }

        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onNullSet();
        }

        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onNullSet();
        }

        protected boolean calcEpsilonReducibility() {
            return false;
        }

        public boolean equals(Object o) {
            return this == o;
        }

        protected Object readResolve() {
            return nullSet;
        }
    }

    private static class EpsilonExpression
    extends Expression {
        private static final long serialVersionUID = 1L;

        EpsilonExpression() {
        }

        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }

        public Object visit(ExpressionVisitor visitor) {
            return visitor.onEpsilon();
        }

        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onEpsilon();
        }

        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onEpsilon();
        }

        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onEpsilon();
        }

        protected boolean calcEpsilonReducibility() {
            return true;
        }

        public boolean equals(Object o) {
            return this == o;
        }

        protected Object readResolve() {
            return epsilon;
        }
    }
}

