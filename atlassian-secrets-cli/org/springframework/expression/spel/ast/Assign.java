/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.SpelNodeImpl;

public class Assign
extends SpelNodeImpl {
    public Assign(int pos, SpelNodeImpl ... operands) {
        super(pos, operands);
    }

    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        TypedValue newValue = this.children[1].getValueInternal(state);
        this.getChild(0).setValue(state, newValue.getValue());
        return newValue;
    }

    @Override
    public String toStringAST() {
        return this.getChild(0).toStringAST() + "=" + this.getChild(1).toStringAST();
    }
}

