/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;

public class StringLiteral
extends Literal {
    private final TypedValue value;

    public StringLiteral(String payload, int pos, String value) {
        super(payload, pos);
        value = value.substring(1, value.length() - 1);
        this.value = new TypedValue(value.replaceAll("''", "'").replaceAll("\"\"", "\""));
        this.exitTypeDescriptor = "Ljava/lang/String";
    }

    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "'" + this.getLiteralValue().getValue() + "'";
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

