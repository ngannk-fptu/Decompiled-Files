/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.util.StringUtils
 */
package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.util.StringUtils;

public class StringLiteral
extends Literal {
    private final TypedValue value;

    public StringLiteral(String payload, int startPos, int endPos, String value) {
        super(payload, startPos, endPos);
        char quoteCharacter = value.charAt(0);
        String valueWithinQuotes = value.substring(1, value.length() - 1);
        valueWithinQuotes = quoteCharacter == '\'' ? StringUtils.replace((String)valueWithinQuotes, (String)"''", (String)"'") : StringUtils.replace((String)valueWithinQuotes, (String)"\"\"", (String)"\"");
        this.value = new TypedValue(valueWithinQuotes);
        this.exitTypeDescriptor = "Ljava/lang/String";
    }

    @Override
    public TypedValue getLiteralValue() {
        return this.value;
    }

    @Override
    public String toString() {
        String ast = String.valueOf(this.getLiteralValue().getValue());
        ast = StringUtils.replace((String)ast, (String)"'", (String)"''");
        return "'" + ast + "'";
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

