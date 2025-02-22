/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.util.Assert
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.util.Assert;

public class IntLiteral
extends Literal {
    private final TypedValue value;

    public IntLiteral(String payload, int startPos, int endPos, int value) {
        super(payload, startPos, endPos);
        this.value = new TypedValue(value);
        this.exitTypeDescriptor = "I";
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
        Integer intValue = (Integer)this.value.getValue();
        Assert.state((intValue != null ? 1 : 0) != 0, (String)"No int value");
        if (intValue == -1) {
            mv.visitInsn(2);
        } else if (intValue >= 0 && intValue < 6) {
            mv.visitInsn(3 + intValue);
        } else {
            mv.visitLdcInsn((Object)intValue);
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}

