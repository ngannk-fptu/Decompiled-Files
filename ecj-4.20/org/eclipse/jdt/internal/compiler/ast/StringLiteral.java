/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class StringLiteral
extends Literal {
    char[] source;
    int lineNumber;

    public StringLiteral(char[] token, int start, int end, int lineNumber) {
        this(start, end);
        this.source = token;
        this.lineNumber = lineNumber - 1;
    }

    public StringLiteral(int s, int e) {
        super(s, e);
    }

    @Override
    public void computeConstant() {
        this.constant = StringConstant.fromValue(String.valueOf(this.source));
    }

    public ExtendedStringLiteral extendWith(CharLiteral lit) {
        return new ExtendedStringLiteral(this, lit);
    }

    public ExtendedStringLiteral extendWith(StringLiteral lit) {
        return new ExtendedStringLiteral(this, lit);
    }

    public StringLiteralConcatenation extendsWith(StringLiteral lit) {
        return new StringLiteralConcatenation(this, lit);
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        if (valueRequired) {
            codeStream.ldc(this.constant.stringValue());
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public TypeBinding literalType(BlockScope scope) {
        return scope.getJavaLangString();
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        output.append('\"');
        int i = 0;
        while (i < this.source.length) {
            Util.appendEscapedChar(output, this.source[i], true);
            ++i;
        }
        output.append('\"');
        return output;
    }

    @Override
    public char[] source() {
        return this.source;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}

