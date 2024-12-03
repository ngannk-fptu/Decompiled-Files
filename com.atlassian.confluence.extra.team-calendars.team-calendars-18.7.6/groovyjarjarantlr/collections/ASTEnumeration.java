/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections;

import groovyjarjarantlr.collections.AST;

public interface ASTEnumeration {
    public boolean hasMoreNodes();

    public AST nextNode();
}

