/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;

public class TokenException
extends SyntaxException {
    public TokenException(String message, Token token) {
        super(token == null ? message + ". No token" : message, TokenException.getLine(token), TokenException.getColumn(token));
    }

    public TokenException(String message, Throwable cause, int line, int column) {
        super(message, cause, line, column);
    }

    public TokenException(String message, Throwable cause, int line, int column, int endLine, int endColumn) {
        super(message, cause, line, column, endLine, endColumn);
    }

    private static int getColumn(Token token) {
        return token != null ? token.getStartColumn() : -1;
    }

    private static int getLine(Token token) {
        return token != null ? token.getStartLine() : -1;
    }
}

