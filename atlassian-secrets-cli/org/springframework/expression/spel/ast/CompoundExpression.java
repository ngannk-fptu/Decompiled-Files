/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.ValueRef;
import org.springframework.lang.Nullable;

public class CompoundExpression
extends SpelNodeImpl {
    public CompoundExpression(int pos, SpelNodeImpl ... expressionComponents) {
        super(pos, expressionComponents);
        if (expressionComponents.length < 2) {
            throw new IllegalStateException("Do not build compound expressions with less than two entries: " + expressionComponents.length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        if (this.getChildCount() == 1) {
            return this.children[0].getValueRef(state);
        }
        SpelNodeImpl nextNode = this.children[0];
        TypedValue result = nextNode.getValueInternal(state);
        int cc = this.getChildCount();
        for (int i = 1; i < cc - 1; ++i) {
            try {
                state.pushActiveContextObject(result);
                nextNode = this.children[i];
                result = nextNode.getValueInternal(state);
                continue;
            }
            finally {
                state.popActiveContextObject();
            }
        }
        try {
            state.pushActiveContextObject(result);
            nextNode = this.children[cc - 1];
            ValueRef valueRef = nextNode.getValueRef(state);
            state.popActiveContextObject();
            return valueRef;
        }
        catch (Throwable throwable) {
            try {
                state.popActiveContextObject();
                throw throwable;
            }
            catch (SpelEvaluationException ex) {
                ex.setPosition(nextNode.getStartPosition());
                throw ex;
            }
        }
    }

    @Override
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        ValueRef ref = this.getValueRef(state);
        TypedValue result = ref.getValue();
        this.exitTypeDescriptor = this.children[this.children.length - 1].exitTypeDescriptor;
        return result;
    }

    @Override
    public void setValue(ExpressionState state, @Nullable Object value) throws EvaluationException {
        this.getValueRef(state).setValue(value);
    }

    @Override
    public boolean isWritable(ExpressionState state) throws EvaluationException {
        return this.getValueRef(state).isWritable();
    }

    @Override
    public String toStringAST() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(this.getChild(i).toStringAST());
        }
        return sb.toString();
    }

    @Override
    public boolean isCompilable() {
        for (SpelNodeImpl child : this.children) {
            if (child.isCompilable()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].generateCode(mv, cf);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}

