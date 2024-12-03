/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

public final class DataExp
extends Expression
implements DataOrValueExp {
    public final Datatype dt;
    public final StringPair name;
    public final Expression except;
    private static final long serialVersionUID = 1L;

    public Datatype getType() {
        return this.dt;
    }

    public StringPair getName() {
        return this.name;
    }

    protected DataExp(Datatype dt, StringPair typeName, Expression except) {
        super(dt.hashCode() + except.hashCode());
        this.dt = dt;
        this.name = typeName;
        this.except = except;
    }

    protected final int calcHashCode() {
        return this.dt.hashCode() + this.except.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        DataExp rhs = (DataExp)o;
        if (this.except != rhs.except) {
            return false;
        }
        return rhs.dt.equals(this.dt);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onData(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onData(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onData(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onData(this);
    }

    protected boolean calcEpsilonReducibility() {
        XSDatatype xdt = (XSDatatype)this.dt;
        return this.except == Expression.nullSet && xdt.isAlwaysValid();
    }
}

