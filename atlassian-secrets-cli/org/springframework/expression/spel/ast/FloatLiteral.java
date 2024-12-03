/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;

public class FloatLiteral
extends Literal {
    private final TypedValue value;

    public FloatLiteral(String payload, int pos, float value) {
        super(payload, pos);
        this.value = new TypedValue(Float.valueOf(value));
        this.exitTypeDescriptor = "F";
    }

    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override
    public boolean isCompilable() {
        return true;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        mv.visitLdcInsn(this.value.getValue());
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}

