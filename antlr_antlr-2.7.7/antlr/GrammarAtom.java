/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeElement;
import antlr.Grammar;
import antlr.Token;

abstract class GrammarAtom
extends AlternativeElement {
    protected String label;
    protected String atomText;
    protected int tokenType = 0;
    protected boolean not = false;
    protected String ASTNodeType = null;

    public GrammarAtom(Grammar grammar, Token token, int n) {
        super(grammar, token, n);
        this.atomText = token.getText();
    }

    public String getLabel() {
        return this.label;
    }

    public String getText() {
        return this.atomText;
    }

    public int getType() {
        return this.tokenType;
    }

    public void setLabel(String string) {
        this.label = string;
    }

    public String getASTNodeType() {
        return this.ASTNodeType;
    }

    public void setASTNodeType(String string) {
        this.ASTNodeType = string;
    }

    public void setOption(Token token, Token token2) {
        if (token.getText().equals("AST")) {
            this.setASTNodeType(token2.getText());
        } else {
            this.grammar.antlrTool.error("Invalid element option:" + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }

    public String toString() {
        String string = " ";
        if (this.label != null) {
            string = string + this.label + ":";
        }
        if (this.not) {
            string = string + "~";
        }
        return string + this.atomText;
    }
}

