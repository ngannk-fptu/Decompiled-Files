/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;

public interface ResolvableNode {
    public void resolve(boolean var1, boolean var2, String var3, AST var4, AST var5) throws SemanticException;

    public void resolve(boolean var1, boolean var2, String var3, AST var4) throws SemanticException;

    public void resolve(boolean var1, boolean var2, String var3) throws SemanticException;

    public void resolve(boolean var1, boolean var2) throws SemanticException;

    public void resolveInFunctionCall(boolean var1, boolean var2) throws SemanticException;

    public void resolveIndex(AST var1) throws SemanticException;
}

