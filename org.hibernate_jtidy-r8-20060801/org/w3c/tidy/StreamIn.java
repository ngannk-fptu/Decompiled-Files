/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.Lexer;

public interface StreamIn {
    public static final int END_OF_STREAM = -1;

    public int getCurcol();

    public int getCurline();

    public int readCharFromStream();

    public int readChar();

    public void ungetChar(int var1);

    public boolean isEndOfStream();

    public void setLexer(Lexer var1);
}

