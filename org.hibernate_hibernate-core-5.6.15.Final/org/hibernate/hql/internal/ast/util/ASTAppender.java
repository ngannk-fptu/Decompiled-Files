/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.ASTFactory;
import antlr.collections.AST;
import org.hibernate.hql.internal.ast.util.ASTUtil;

public class ASTAppender {
    private AST parent;
    private AST last;
    private ASTFactory factory;

    public ASTAppender(ASTFactory factory, AST parent) {
        this.factory = factory;
        this.parent = parent;
        this.last = ASTUtil.getLastChild(parent);
    }

    public AST append(int type, String text, boolean appendIfEmpty) {
        if (text != null && (appendIfEmpty || text.length() > 0)) {
            return this.append(this.factory.create(type, text));
        }
        return null;
    }

    public AST append(AST child) {
        if (this.last == null) {
            this.parent.setFirstChild(child);
        } else {
            this.last.setNextSibling(child);
        }
        this.last = child;
        return this.last;
    }
}

