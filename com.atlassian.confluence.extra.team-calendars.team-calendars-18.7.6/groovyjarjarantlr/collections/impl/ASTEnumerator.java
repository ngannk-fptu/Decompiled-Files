/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections.impl;

import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.ASTEnumeration;
import groovyjarjarantlr.collections.impl.Vector;
import groovyjarjarantlr.collections.impl.VectorEnumerator;
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

