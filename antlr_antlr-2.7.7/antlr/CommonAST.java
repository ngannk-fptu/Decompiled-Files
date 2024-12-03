/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.BaseAST;
import antlr.Token;
import antlr.collections.AST;

public class CommonAST
extends BaseAST {
    int ttype = 0;
    String text;

    public String getText() {
        return this.text;
    }

    public int getType() {
        return this.ttype;
    }

    public void initialize(int n, String string) {
        this.setType(n);
        this.setText(string);
    }

    public void initialize(AST aST) {
        this.setText(aST.getText());
        this.setType(aST.getType());
    }

    public CommonAST() {
    }

    public CommonAST(Token token) {
        this.initialize(token);
    }

    public void initialize(Token token) {
        this.setText(token.getText());
        this.setType(token.getType());
    }

    public void setText(String string) {
        this.text = string;
    }

    public void setType(int n) {
        this.ttype = n;
    }
}

