/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections.impl;

import groovyjarjarantlr.collections.AST;

public class ASTArray {
    public int size = 0;
    public AST[] array;

    public ASTArray(int n) {
        this.array = new AST[n];
    }

    public ASTArray add(AST aST) {
        this.array[this.size++] = aST;
        return this;
    }
}

