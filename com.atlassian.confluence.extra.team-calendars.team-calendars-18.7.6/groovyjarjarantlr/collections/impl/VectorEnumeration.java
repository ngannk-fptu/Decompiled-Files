/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections.impl;

import groovyjarjarantlr.collections.impl.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class VectorEnumeration
implements Enumeration {
    Vector vector;
    int i;

    VectorEnumeration(Vector vector) {
        this.vector = vector;
        this.i = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasMoreElements() {
        Vector vector = this.vector;
        synchronized (vector) {
            return this.i <= this.vector.lastElement;
        }
    }

    public Object nextElement() {
        Vector vector = this.vector;
        synchronized (vector) {
            if (this.i <= this.vector.lastElement) {
                return this.vector.data[this.i++];
            }
            throw new NoSuchElementException("VectorEnumerator");
        }
    }
}

