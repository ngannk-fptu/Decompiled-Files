/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections.impl;

import antlr.collections.impl.VectorEnumerator;
import java.util.Enumeration;

public class Vector
implements Cloneable {
    protected Object[] data;
    protected int lastElement = -1;

    public Vector() {
        this(10);
    }

    public Vector(int n) {
        this.data = new Object[n];
    }

    public synchronized void appendElement(Object object) {
        this.ensureCapacity(this.lastElement + 2);
        this.data[++this.lastElement] = object;
    }

    public int capacity() {
        return this.data.length;
    }

    public Object clone() {
        Vector vector = null;
        try {
            vector = (Vector)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            System.err.println("cannot clone Vector.super");
            return null;
        }
        vector.data = new Object[this.size()];
        System.arraycopy(this.data, 0, vector.data, 0, this.size());
        return vector;
    }

    public synchronized Object elementAt(int n) {
        if (n >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException(n + " >= " + this.data.length);
        }
        if (n < 0) {
            throw new ArrayIndexOutOfBoundsException(n + " < 0 ");
        }
        return this.data[n];
    }

    public synchronized Enumeration elements() {
        return new VectorEnumerator(this);
    }

    public synchronized void ensureCapacity(int n) {
        if (n + 1 > this.data.length) {
            Object[] objectArray = this.data;
            int n2 = this.data.length * 2;
            if (n + 1 > n2) {
                n2 = n + 1;
            }
            this.data = new Object[n2];
            System.arraycopy(objectArray, 0, this.data, 0, objectArray.length);
        }
    }

    public synchronized boolean removeElement(Object object) {
        int n;
        for (n = 0; n <= this.lastElement && this.data[n] != object; ++n) {
        }
        if (n <= this.lastElement) {
            this.data[n] = null;
            int n2 = this.lastElement - n;
            if (n2 > 0) {
                System.arraycopy(this.data, n + 1, this.data, n, n2);
            }
            --this.lastElement;
            return true;
        }
        return false;
    }

    public synchronized void setElementAt(Object object, int n) {
        if (n >= this.data.length) {
            throw new ArrayIndexOutOfBoundsException(n + " >= " + this.data.length);
        }
        this.data[n] = object;
        if (n > this.lastElement) {
            this.lastElement = n;
        }
    }

    public int size() {
        return this.lastElement + 1;
    }
}

