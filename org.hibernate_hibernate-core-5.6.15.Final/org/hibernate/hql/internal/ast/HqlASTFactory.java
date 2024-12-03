/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 */
package org.hibernate.hql.internal.ast;

import antlr.ASTFactory;
import org.hibernate.hql.internal.ast.tree.Node;

public class HqlASTFactory
extends ASTFactory {
    public Class getASTNodeType(int tokenType) {
        return Node.class;
    }
}

