/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.lang.Nullable;

public class QualifiedIdentifier
extends SpelNodeImpl {
    @Nullable
    private TypedValue value;

    public QualifiedIdentifier(int startPos, int endPos, SpelNodeImpl ... operands) {
        super(startPos, endPos, operands);
    }

    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        if (this.value == null) {
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < this.getChildCount(); ++i2) {
                Object value = this.children[i2].getValueInternal(state).getValue();
                if (!(i2 <= 0 || value != null && value.toString().startsWith("$"))) {
                    sb.append('.');
                }
                sb.append(value);
            }
            this.value = new TypedValue(sb.toString());
        }
        return this.value;
    }

    @Override
    public String toStringAST() {
        StringBuilder sb = new StringBuilder();
        if (this.value != null) {
            sb.append(this.value.getValue());
        } else {
            for (int i2 = 0; i2 < this.getChildCount(); ++i2) {
                if (i2 > 0) {
                    sb.append('.');
                }
                sb.append(this.getChild(i2).toStringAST());
            }
        }
        return sb.toString();
    }
}

