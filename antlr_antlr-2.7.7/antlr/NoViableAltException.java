/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.RecognitionException;
import antlr.Token;
import antlr.TreeParser;
import antlr.collections.AST;

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

