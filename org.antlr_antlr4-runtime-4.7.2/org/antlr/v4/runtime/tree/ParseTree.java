/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.SyntaxTree;

public interface ParseTree
extends SyntaxTree {
    @Override
    public ParseTree getParent();

    @Override
    public ParseTree getChild(int var1);

    public void setParent(RuleContext var1);

    public <T> T accept(ParseTreeVisitor<? extends T> var1);

    public String getText();

    public String toStringTree(Parser var1);
}

