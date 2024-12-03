/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.BaseErrorListener
 *  org.antlr.v4.runtime.RecognitionException
 *  org.antlr.v4.runtime.Recognizer
 */
package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

class SQLServerErrorListener
extends BaseErrorListener {
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerFMTQuery");

    SQLServerErrorListener() {
    }

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Error occured during token parsing: " + msg);
            logger.fine("line " + line + ":" + charPositionInLine + " token recognition error at: " + offendingSymbol.toString());
        }
    }
}

