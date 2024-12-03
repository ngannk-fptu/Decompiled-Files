/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections;

import antlr.collections.AST;

public interface ASTEnumeration {
    public boolean hasMoreNodes();

    public AST nextNode();
}

