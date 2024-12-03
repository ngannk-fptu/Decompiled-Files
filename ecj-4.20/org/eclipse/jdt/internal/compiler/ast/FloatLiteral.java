/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.NumberLiteral;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.util.FloatUtil;

public class FloatLiteral
extends NumberLiteral {
    float value;

    public FloatLiteral(char[] token, int s, int e) {
        super(token, s, e);
    }

    @Override
    public void computeConstant() {
        Float computedValue;
        boolean containsUnderscores;
        boolean bl = containsUnderscores = CharOperation.indexOf('_', this.source) > 0;
        if (containsUnderscores) {
            this.source = CharOperation.remove(this.source, '_');
        }
        try {
            computedValue = Float.valueOf(String.valueOf(this.source));
        }
        catch (NumberFormatException numberFormatException) {
            try {
                float v = FloatUtil.valueOfHexFloatLiteral(this.source);
                if (v == Float.POSITIVE_INFINITY) {
                    return;
                }
                if (Float.isNaN(v)) {
                    return;
                }
                this.value = v;
                this.constant = FloatConstant.fromValue(v);
            }
            catch (NumberFormatException numberFormatException2) {}
            return;
        }
        float floatValue = computedValue.floatValue();
        if (floatValue > Float.MAX_VALUE) {
            return;
        }
        if (floatValue < Float.MIN_VALUE) {
            boolean isHexaDecimal = false;
            int i = 0;
            block10: while (i < this.source.length) {
                switch (this.source[i]) {
                    case '.': 
                    case '0': {
                        break;
                    }
                    case 'X': 
                    case 'x': {
                        isHexaDecimal = true;
                        break;
                    }
                    case 'D': 
                    case 'E': 
                    case 'F': 
                    case 'd': 
                    case 'e': 
                    case 'f': {
                        if (!isHexaDecimal) break block10;
                        return;
                    }
                    case 'P': 
                    case 'p': {
                        break block10;
                    }
                    default: {
                        return;
                    }
                }
                ++i;
            }
        }
        this.value = floatValue;
        this.constant = FloatConstant.fromValue(this.value);
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        if (valueRequired) {
            codeStream.generateConstant(this.constant, this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public TypeBinding literalType(BlockScope scope) {
        return TypeBinding.FLOAT;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}

