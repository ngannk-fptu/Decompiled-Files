/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class StringLiteralConcatenation
extends StringLiteral {
    private static final int INITIAL_SIZE = 5;
    public Expression[] literals;
    public int counter;

    public StringLiteralConcatenation(StringLiteral str1, StringLiteral str2) {
        super(str1.sourceStart, str1.sourceEnd);
        this.source = str1.source;
        this.literals = new StringLiteral[5];
        this.counter = 0;
        this.literals[this.counter++] = str1;
        this.extendsWith(str2);
    }

    @Override
    public StringLiteralConcatenation extendsWith(StringLiteral lit) {
        this.sourceEnd = lit.sourceEnd;
        int literalsLength = this.literals.length;
        if (this.counter == literalsLength) {
            this.literals = new StringLiteral[literalsLength + 5];
            System.arraycopy(this.literals, 0, this.literals, 0, literalsLength);
        }
        int length = this.source.length;
        this.source = new char[length + lit.source.length];
        System.arraycopy(this.source, 0, this.source, 0, length);
        System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
        this.literals[this.counter++] = lit;
        return this;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        output.append("StringLiteralConcatenation{");
        int i = 0;
        int max = this.counter;
        while (i < max) {
            this.literals[i].printExpression(indent, output);
            output.append("+\n");
            ++i;
        }
        return output.append('}');
    }

    @Override
    public char[] source() {
        return this.source;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int i = 0;
            int max = this.counter;
            while (i < max) {
                this.literals[i].traverse(visitor, scope);
                ++i;
            }
        }
        visitor.endVisit(this, scope);
    }
}

