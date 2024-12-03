/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.tree;

import java.util.ArrayDeque;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class IterativeParseTreeWalker
extends ParseTreeWalker {
    @Override
    public void walk(ParseTreeListener listener, ParseTree t) {
        ArrayDeque<ParseTree> nodeStack = new ArrayDeque<ParseTree>();
        IntegerStack indexStack = new IntegerStack();
        ParseTree currentNode = t;
        int currentIndex = 0;
        block0: while (currentNode != null) {
            if (currentNode instanceof ErrorNode) {
                listener.visitErrorNode((ErrorNode)currentNode);
            } else if (currentNode instanceof TerminalNode) {
                listener.visitTerminal((TerminalNode)currentNode);
            } else {
                RuleNode r = (RuleNode)currentNode;
                this.enterRule(listener, r);
            }
            if (currentNode.getChildCount() > 0) {
                nodeStack.push(currentNode);
                indexStack.push(currentIndex);
                currentIndex = 0;
                currentNode = currentNode.getChild(0);
                continue;
            }
            do {
                if (currentNode instanceof RuleNode) {
                    this.exitRule(listener, (RuleNode)currentNode);
                }
                if (nodeStack.isEmpty()) {
                    currentNode = null;
                    currentIndex = 0;
                    continue block0;
                }
                currentNode = ((ParseTree)nodeStack.peek()).getChild(++currentIndex);
                if (currentNode != null) continue block0;
                currentNode = (ParseTree)nodeStack.pop();
                currentIndex = indexStack.pop();
            } while (currentNode != null);
        }
    }
}

