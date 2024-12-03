/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression;

public class AttributeExp
extends Expression
implements NameClassAndExpression {
    public final NameClass nameClass;
    public final Expression exp;
    private static final long serialVersionUID = 1L;

    public final NameClass getNameClass() {
        return this.nameClass;
    }

    public final Expression getContentModel() {
        return this.exp;
    }

    public AttributeExp(NameClass nameClass, Expression exp) {
        super(nameClass.hashCode() + exp.hashCode());
        this.nameClass = nameClass;
        this.exp = exp;
    }

    protected final int calcHashCode() {
        return this.nameClass.hashCode() + this.exp.hashCode();
    }

    public boolean equals(Object o) {
        if (o.getClass() != AttributeExp.class) {
            return false;
        }
        AttributeExp rhs = (AttributeExp)o;
        return rhs.nameClass.equals(this.nameClass) && rhs.exp.equals(this.exp);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onAttribute(this);
    }

    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onAttribute(this);
    }

    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onAttribute(this);
    }

    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onAttribute(this);
    }

    protected boolean calcEpsilonReducibility() {
        return false;
    }
}

