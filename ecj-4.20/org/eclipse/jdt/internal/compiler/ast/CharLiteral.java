/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.NumberLiteral;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public class CharLiteral
extends NumberLiteral {
    char value;

    public CharLiteral(char[] token, int s, int e) {
        super(token, s, e);
        this.computeValue();
    }

    @Override
    public void computeConstant() {
        this.constant = CharConstant.fromValue(this.value);
    }

    private void computeValue() {
        this.value = this.source[1];
        if (this.value != '\\') {
            return;
        }
        char digit = this.source[2];
        switch (digit) {
            case 's': {
                this.value = (char)32;
                break;
            }
            case 'b': {
                this.value = (char)8;
                break;
            }
            case 't': {
                this.value = (char)9;
                break;
            }
            case 'n': {
                this.value = (char)10;
                break;
            }
            case 'f': {
                this.value = (char)12;
                break;
            }
            case 'r': {
                this.value = (char)13;
                break;
            }
            case '\"': {
                this.value = (char)34;
                break;
            }
            case '\'': {
                this.value = (char)39;
                break;
            }
            case '\\': {
                this.value = (char)92;
                break;
            }
            default: {
                int number = ScannerHelper.getNumericValue(digit);
                digit = this.source[3];
                if (digit == '\'') {
                    this.value = (char)number;
                    this.constant = CharConstant.fromValue(this.value);
                    break;
                }
                number = number * 8 + ScannerHelper.getNumericValue(digit);
                digit = this.source[4];
                if (digit != '\'') {
                    number = number * 8 + ScannerHelper.getNumericValue(digit);
                }
                this.value = (char)number;
            }
        }
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
        return TypeBinding.CHAR;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}

