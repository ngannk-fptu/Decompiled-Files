/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections.impl;

import antlr.collections.AST;
import antlr.collections.ASTEnumeration;
import antlr.collections.impl.Vector;
import antlr.collections.impl.VectorEnumerator;
import java.util.NoSuchElementException;

public class ASTEnumerator
implements ASTEnumeration {
    VectorEnumerator nodes;
    int i = 0;

    public ASTEnumerator(Vector vector) {
        this.nodes = new VectorEnumerator(vector);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasMoreNodes() {
        VectorEnumerator vectorEnumerator = this.nodes;
        synchronized (vectorEnumerator) {
            return this.i <= this.nodes.vector.lastElement;
        }
    }

    public AST nextNode() {
        VectorEnumerator vectorEnumerator = this.nodes;
        synchronized (vectorEnumerator) {
            if (this.i <= this.nodes.vector.lastElement) {
                return (AST)this.nodes.vector.data[this.i++];
            }
            throw new NoSuchElementException("ASTEnumerator");
        }
    }
}

