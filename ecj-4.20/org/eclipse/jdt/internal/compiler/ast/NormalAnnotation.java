/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;

public class NormalAnnotation
extends Annotation {
    public MemberValuePair[] memberValuePairs;

    public NormalAnnotation(TypeReference type, int sourceStart) {
        this.type = type;
        this.sourceStart = sourceStart;
        this.sourceEnd = type.sourceEnd;
    }

    @Override
    public ElementValuePair[] computeElementValuePairs() {
        int numberOfPairs;
        int n = numberOfPairs = this.memberValuePairs == null ? 0 : this.memberValuePairs.length;
        if (numberOfPairs == 0) {
            return Binding.NO_ELEMENT_VALUE_PAIRS;
        }
        ElementValuePair[] pairs = new ElementValuePair[numberOfPairs];
        int i = 0;
        while (i < numberOfPairs) {
            pairs[i] = this.memberValuePairs[i].compilerElementPair;
            ++i;
        }
        return pairs;
    }

    @Override
    public MemberValuePair[] memberValuePairs() {
        return this.memberValuePairs == null ? NoValuePairs : this.memberValuePairs;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        super.printExpression(indent, output);
        output.append('(');
        if (this.memberValuePairs != null) {
            int i = 0;
            int max = this.memberValuePairs.length;
            while (i < max) {
                if (i > 0) {
                    output.append(',');
                }
                this.memberValuePairs[i].print(indent, output);
                ++i;
            }
        }
        output.append(')');
        return output;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.memberValuePairs != null) {
                int memberValuePairsLength = this.memberValuePairs.length;
                int i = 0;
                while (i < memberValuePairsLength) {
                    this.memberValuePairs[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.memberValuePairs != null) {
                int memberValuePairsLength = this.memberValuePairs.length;
                int i = 0;
                while (i < memberValuePairsLength) {
                    this.memberValuePairs[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}

