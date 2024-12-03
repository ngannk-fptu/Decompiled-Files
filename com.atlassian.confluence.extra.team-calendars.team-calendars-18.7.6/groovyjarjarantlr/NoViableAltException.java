/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TreeParser;
import groovyjarjarantlr.collections.AST;

public class NoViableAltException
extends RecognitionException {
    public Token token;
    public AST node;

    public NoViableAltException(AST aST) {
        super("NoViableAlt", "<AST>", aST.getLine(), aST.getColumn());
        this.node = aST;
    }

    public NoViableAltException(Token token, String string) {
        super("NoViableAlt", string, token.getLine(), token.getColumn());
        this.token = token;
    }

    public String getMessage() {
        if (this.token != null) {
            return "unexpected token: " + this.token.getText();
        }
        if (this.node == TreeParser.ASTNULL) {
            return "unexpected end of subtree";
        }
        return "unexpected AST node: " + ((Object)this.node).toString();
    }
}

