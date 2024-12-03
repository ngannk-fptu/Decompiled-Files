/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public interface ParseTreeListener {
    public void visitTerminal(TerminalNode var1);

    public void visitErrorNode(ErrorNode var1);

    public void enterEveryRule(ParserRuleContext var1);

    public void exitEveryRule(ParserRuleContext var1);
}

