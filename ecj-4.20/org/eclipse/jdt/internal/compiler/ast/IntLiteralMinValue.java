/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;

public class IntLiteralMinValue
extends IntLiteral {
    static final char[] CharValue = new char[]{'-', '2', '1', '4', '7', '4', '8', '3', '6', '4', '8'};

    public IntLiteralMinValue(char[] token, char[] reducedToken, int start, int end) {
        super(token, reducedToken, start, end, Integer.MIN_VALUE, IntConstant.fromValue(Integer.MIN_VALUE));
    }

    @Override
    public void computeConstant() {
    }
}

