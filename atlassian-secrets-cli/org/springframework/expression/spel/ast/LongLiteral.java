/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;

public class LongLiteral
extends Literal {
    private final TypedValue value;

    public LongLiteral(String payload, int pos, long value) {
        super(payload, pos);
        this.value = new TypedValue(value);
        this.exitTypeDescriptor = "J";
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

