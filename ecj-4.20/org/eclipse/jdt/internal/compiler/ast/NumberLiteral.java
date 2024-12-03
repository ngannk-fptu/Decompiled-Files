/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.Literal;

public abstract class NumberLiteral
extends Literal {
    char[] source;

    public NumberLiteral(char[] token, int s, int e) {
        this(s, e);
        this.source = token;
    }

    public NumberLiteral(int s, int e) {
        super(s, e);
    }

    @Override
    public boolean isValidJavaStatement() {
        return false;
    }

    @Override
    public char[] source() {
        return this.source;
    }

    protected static char[] removePrefixZerosAndUnderscores(char[] token, boolean isLong) {
        int max = token.length;
        int start = 0;
        int end = max - 1;
        if (isLong) {
            --end;
        }
        if (max > 1 && token[0] == '0') {
            start = max > 2 && (token[1] == 'x' || token[1] == 'X') ? 2 : (max > 2 && (token[1] == 'b' || token[1] == 'B') ? 2 : 1);
        }
        boolean modified = false;
        boolean ignore = true;
        int i = start;
        block8: while (i < max) {
            char currentChar = token[i];
            switch (currentChar) {
                case '0': {
                    if (!ignore || modified || i >= end) break;
                    modified = true;
                    break;
                }
                case '_': {
                    modified = true;
                    break block8;
                }
                default: {
                    ignore = false;
                }
            }
            ++i;
        }
        if (!modified) {
            return token;
        }
        ignore = true;
        StringBuffer buffer = new StringBuffer();
        buffer.append(token, 0, start);
        int i2 = start;
        while (i2 < max) {
            block13: {
                char currentChar = token[i2];
                switch (currentChar) {
                    case '0': {
                        if (!ignore || i2 >= end) break;
                        break block13;
                    }
                    case '_': {
                        break block13;
                    }
                    default: {
                        ignore = false;
                    }
                }
                buffer.append(currentChar);
            }
            ++i2;
        }
        return buffer.toString().toCharArray();
    }
}

