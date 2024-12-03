/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.ASTEnumeration;

public class ASTNULLType
implements AST {
    public void addChild(AST aST) {
    }

    public boolean equals(AST aST) {
        return false;
    }

    public boolean equalsList(AST aST) {
        return false;
    }

    public boolean equalsListPartial(AST aST) {
        return false;
    }

    public boolean equalsTree(AST aST) {
        return false;
    }

    public boolean equalsTreePartial(AST aST) {
        return false;
    }

    public ASTEnumeration findAll(AST aST) {
        return null;
    }

    public ASTEnumeration findAllPartial(AST aST) {
        return null;
    }

    public AST getFirstChild() {
        return this;
    }

    public AST getNextSibling() {
        return this;
    }

    public String getText() {
        return "<ASTNULL>";
    }

    public int getType() {
        return 3;
    }

    public int getLine() {
        return 0;
    }

    public int getColumn() {
        return 0;
    }

    public int getNumberOfChildren() {
        return 0;
    }

    public void initialize(int n, String string) {
    }

    public void initialize(AST aST) {
    }

    public void initialize(Token token) {
    }

    public void setFirstChild(AST aST) {
    }

    public void setNextSibling(AST aST) {
    }

    public void setText(String string) {
    }

    public void setType(int n) {
    }

    public String toString() {
        return this.getText();
    }

    public String toStringList() {
        return this.getText();
    }

    public String toStringTree() {
        return this.getText();
    }
}

