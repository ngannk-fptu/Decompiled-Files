/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.type.Type;

public interface OperatorNode {
    public void initialize() throws SemanticException;

    public Type getDataType();
}

