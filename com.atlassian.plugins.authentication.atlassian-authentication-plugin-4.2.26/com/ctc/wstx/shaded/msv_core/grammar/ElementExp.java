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

public abstract class ElementExp
extends Expression
implements NameClassAndExpression {
    public Expression contentModel;
    public boolean ignoreUndeclaredAttributes;
    private static final long serialVersionUID = 1L;

    public final Expression getContentModel() {
        return this.contentModel;
    }

    public abstract NameClass getNameClass();

    public ElementExp(Expression contentModel, boolean ignoreUndeclaredAttributes) {
        super(contentModel.hashCode());
        this.contentModel = contentModel;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }

    protected final int calcHashCode() {
        return this.contentModel.hashCode();
    }

    public final boolean equals(Object o) {
        return this == o;
    }

    public final Object visit(ExpressionVisitor visitor) {
        return visitor.onElement(this);
    }

    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onElement(this);
    }

    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onElement(this);
    }

    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onElement(this);
    }

    protected final boolean calcEpsilonReducibility() {
        return false;
    }
}

