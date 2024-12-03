/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public interface ParseTreeVisitor<T> {
    public T visit(ParseTree var1);

    public T visitChildren(RuleNode var1);

    public T visitTerminal(TerminalNode var1);

    public T visitErrorNode(ErrorNode var1);
}

