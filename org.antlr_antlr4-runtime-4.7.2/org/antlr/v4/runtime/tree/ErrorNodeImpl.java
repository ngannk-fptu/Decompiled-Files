/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class ErrorNodeImpl
extends TerminalNodeImpl
implements ErrorNode {
    public ErrorNodeImpl(Token token) {
        super(token);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitErrorNode(this);
    }
}

