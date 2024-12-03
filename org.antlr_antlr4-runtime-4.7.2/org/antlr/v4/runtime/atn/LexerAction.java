/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.LexerActionType;

public interface LexerAction {
    public LexerActionType getActionType();

    public boolean isPositionDependent();

    public void execute(Lexer var1);
}

