/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.LongLiteralMinValue;
import org.eclipse.jdt.internal.compiler.ast.NumberLiteral;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public class LongLiteral
extends NumberLiteral {
    private static final char[] HEXA_MIN_VALUE = "0x8000000000000000L".toCharArray();
    private static final char[] HEXA_MINUS_ONE_VALUE = "0xffffffffffffffffL".toCharArray();
    private static final char[] OCTAL_MIN_VALUE = "01000000000000000000000L".toCharArray();
    private static final char[] OCTAL_MINUS_ONE_VALUE = "01777777777777777777777L".toCharArray();
    private static final char[] DECIMAL_MIN_VALUE = "9223372036854775808L".toCharArray();
    private static final char[] DECIMAL_MAX_VALUE = "9223372036854775807L".toCharArray();
    private char[] reducedForm;

    public static LongLiteral buildLongLiteral(char[] token, int s, int e) {
        char[] longReducedToken = LongLiteral.removePrefixZerosAndUnderscores(token, true);
        switch (longReducedToken.length) {
            case 19: {
                if (!CharOperation.equals(longReducedToken, HEXA_MIN_VALUE)) break;
                return new LongLiteralMinValue(token, (char[])(longReducedToken != token ? longReducedToken : null), s, e);
            }
            case 24: {
                if (!CharOperation.equals(longReducedToken, OCTAL_MIN_VALUE)) break;
                return new LongLiteralMinValue(token, (char[])(longReducedToken != token ? longReducedToken : null), s, e);
            }
        }
        return new LongLiteral(token, (char[])(longReducedToken != token ? longReducedToken : null), s, e);
    }

    LongLiteral(char[] token, char[] reducedForm, int start, int end) {
        super(token, start, end);
        this.reducedForm = reducedForm;
    }

    public LongLiteral convertToMinValue() {
        if ((this.bits & 0x1FE00000) >> 21 != 0) {
            return this;
        }
        char[] token = this.reducedForm != null ? this.reducedForm : this.source;
        switch (token.length) {
            case 20: {
                if (!CharOperation.equals(token, DECIMAL_MIN_VALUE, false)) break;
                return new LongLiteralMinValue(this.source, this.reducedForm, this.sourceStart, this.sourceEnd);
            }
        }
        return this;
    }

    @Override
    public void computeConstant() {
        char[] token = this.reducedForm != null ? this.reducedForm : this.source;
        int tokenLength = token.length;
        int length = tokenLength - 1;
        int radix = 10;
        int j = 0;
        if (token[0] == '0') {
            if (length == 1) {
                this.constant = LongConstant.fromValue(0L);
                return;
            }
            if (token[1] == 'x' || token[1] == 'X') {
                radix = 16;
                j = 2;
            } else if (token[1] == 'b' || token[1] == 'B') {
                radix = 2;
                j = 2;
            } else {
                radix = 8;
                j = 1;
            }
        }
        switch (radix) {
            case 2: {
                if (length - 2 > 64) {
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 16: {
                if (tokenLength > 19) break;
                if (CharOperation.equals(token, HEXA_MINUS_ONE_VALUE)) {
                    this.constant = LongConstant.fromValue(-1L);
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 10: {
                if (tokenLength > DECIMAL_MAX_VALUE.length || tokenLength == DECIMAL_MAX_VALUE.length && CharOperation.compareTo(token, DECIMAL_MAX_VALUE, 0, length) > 0) {
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 8: {
                if (tokenLength > 24) break;
                if (tokenLength == 24 && token[j] > '1') {
                    return;
                }
                if (CharOperation.equals(token, OCTAL_MINUS_ONE_VALUE)) {
                    this.constant = LongConstant.fromValue(-1L);
                    return;
                }
                this.computeValue(token, length, radix, j);
            }
        }
    }

    private void computeValue(char[] token, int tokenLength, int radix, int j) {
        long computedValue = 0L;
        while (j < tokenLength) {
            int digitValue;
            if ((digitValue = ScannerHelper.digit(token[j++], radix)) < 0) {
                return;
            }
            computedValue = computedValue * (long)radix + (long)digitValue;
        }
        this.constant = LongConstant.fromValue(computedValue);
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
        return TypeBinding.LONG;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}

