/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.ASTEnumeration;

public interface AST {
    public void addChild(AST var1);

    public boolean equals(AST var1);

    public boolean equalsList(AST var1);

    public boolean equalsListPartial(AST var1);

    public boolean equalsTree(AST var1);

    public boolean equalsTreePartial(AST var1);

    public ASTEnumeration findAll(AST var1);

    public ASTEnumeration findAllPartial(AST var1);

    public AST getFirstChild();

    public AST getNextSibling();

    public String getText();

    public int getType();

    public int getLine();

    public int getColumn();

    public int getNumberOfChildren();

    public void initialize(int var1, String var2);

    public void initialize(AST var1);

    public void initialize(Token var1);

    public void setFirstChild(AST var1);

    public void setNextSibling(AST var1);

    public void setText(String var1);

    public void setType(int var1);

    public String toString();

    public String toStringList();

    public String toStringTree();
}

