/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.support.BooleanTypedValue;

public class BooleanLiteral
extends Literal {
    private final BooleanTypedValue value;

    public BooleanLiteral(String payload, int startPos, int endPos, boolean value) {
        super(payload, startPos, endPos);
        this.value = BooleanTypedValue.forValue(value);
        this.exitTypeDescriptor = "Z";
    }

    @Override
    public BooleanTypedValue getLiteralValue() {
        return this.value;
    }

    @Override
    public boolean isCompilable() {
        return true;
    }

    @Override
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        if (this.value == BooleanTypedValue.TRUE) {
            mv.visitLdcInsn((Object)1);
        } else {
            mv.visitLdcInsn((Object)0);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}

