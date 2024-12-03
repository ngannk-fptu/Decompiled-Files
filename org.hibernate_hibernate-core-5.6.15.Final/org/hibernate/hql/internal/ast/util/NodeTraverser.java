/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.util.ArrayDeque;

public class NodeTraverser {
    private final VisitationStrategy strategy;

    public NodeTraverser(VisitationStrategy strategy) {
        this.strategy = strategy;
    }

    public void traverseDepthFirst(AST ast) {
        if (ast == null) {
            throw new IllegalArgumentException("node to traverse cannot be null!");
        }
        this.visitDepthFirst(ast.getFirstChild());
    }

    private void visitDepthFirst(AST ast) {
        if (ast == null) {
            return;
        }
        ArrayDeque<AST> stack = new ArrayDeque<AST>();
        stack.addLast(ast);
        while (!stack.isEmpty()) {
            ast = (AST)stack.removeLast();
            this.strategy.visit(ast);
            if (ast.getNextSibling() != null) {
                stack.addLast(ast.getNextSibling());
            }
            if (ast.getFirstChild() == null) continue;
            stack.addLast(ast.getFirstChild());
        }
    }

    public static interface VisitationStrategy {
        public void visit(AST var1);
    }
}

