/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ConsoleErrorListener
extends BaseErrorListener {
    public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
    }
}

