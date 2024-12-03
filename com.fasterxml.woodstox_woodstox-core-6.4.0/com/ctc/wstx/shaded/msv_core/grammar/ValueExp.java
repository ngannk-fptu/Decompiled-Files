/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

public final class ValueExp
extends Expression
implements DataOrValueExp {
    public final Datatype dt;
    public final Object value;
    public final StringPair name;
    private static final long serialVersionUID = 1L;

    public Datatype getType() {
        return this.dt;
    }

    public StringPair getName() {
        return this.name;
    }

    protected ValueExp(Datatype dt, StringPair typeName, Object value) {
        super(dt.hashCode() + dt.valueHashCode(value));
        this.dt = dt;
        this.name = typeName;
        this.value = value;
    }

    protected final int calcHashCode() {
        return this.dt.hashCode() + this.dt.valueHashCode(this.value);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ValueExp rhs = (ValueExp)o;
        if (!rhs.dt.equals(this.dt)) {
            return false;
        }
        return this.dt.sameValue(this.value, rhs.value);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onValue(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onValue(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onValue(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onValue(this);
    }

    protected boolean calcEpsilonReducibility() {
        return false;
    }
}

