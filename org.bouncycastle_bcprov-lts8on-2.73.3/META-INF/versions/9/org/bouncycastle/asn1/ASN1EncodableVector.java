/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import org.bouncycastle.asn1.ASN1Encodable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1EncodableVector {
    static final ASN1Encodable[] EMPTY_ELEMENTS = new ASN1Encodable[0];
    private static final int DEFAULT_CAPACITY = 10;
    private ASN1Encodable[] elements;
    private int elementCount;
    private boolean copyOnWrite;

    public ASN1EncodableVector() {
        this(10);
    }

    public ASN1EncodableVector(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("'initialCapacity' must not be negative");
        }
        this.elements = initialCapacity == 0 ? EMPTY_ELEMENTS : new ASN1Encodable[initialCapacity];
        this.elementCount = 0;
        this.copyOnWrite = false;
    }

    public void add(ASN1Encodable element) {
        if (null == element) {
            throw new NullPointerException("'element' cannot be null");
        }
        int minCapacity = this.elementCount + 1;
        int capacity = this.elements.length;
        if (minCapacity > capacity | this.copyOnWrite) {
            this.reallocate(minCapacity);
        }
        this.elements[this.elementCount] = element;
        this.elementCount = minCapacity;
    }

    public void addAll(ASN1Encodable[] others) {
        if (null == others) {
            throw new NullPointerException("'others' cannot be null");
        }
        this.doAddAll(others, "'others' elements cannot be null");
    }

    public void addAll(ASN1EncodableVector other) {
        if (null == other) {
            throw new NullPointerException("'other' cannot be null");
        }
        this.doAddAll(other.elements, "'other' elements cannot be null");
    }

    private void doAddAll(ASN1Encodable[] others, String nullMsg) {
        int otherElementCount = others.length;
        if (otherElementCount < 1) {
            return;
        }
        int minCapacity = this.elementCount + otherElementCount;
        int capacity = this.elements.length;
        if (minCapacity > capacity | this.copyOnWrite) {
            this.reallocate(minCapacity);
        }
        int i = 0;
        do {
            ASN1Encodable otherElement;
            if (null == (otherElement = others[i])) {
                throw new NullPointerException(nullMsg);
            }
            this.elements[this.elementCount + i] = otherElement;
        } while (++i < otherElementCount);
        this.elementCount = minCapacity;
    }

    public ASN1Encodable get(int i) {
        if (i >= this.elementCount) {
            throw new ArrayIndexOutOfBoundsException(i + " >= " + this.elementCount);
        }
        return this.elements[i];
    }

    public int size() {
        return this.elementCount;
    }

    ASN1Encodable[] copyElements() {
        if (0 == this.elementCount) {
            return EMPTY_ELEMENTS;
        }
        ASN1Encodable[] copy = new ASN1Encodable[this.elementCount];
        System.arraycopy(this.elements, 0, copy, 0, this.elementCount);
        return copy;
    }

    ASN1Encodable[] takeElements() {
        if (0 == this.elementCount) {
            return EMPTY_ELEMENTS;
        }
        if (this.elements.length == this.elementCount) {
            this.copyOnWrite = true;
            return this.elements;
        }
        ASN1Encodable[] copy = new ASN1Encodable[this.elementCount];
        System.arraycopy(this.elements, 0, copy, 0, this.elementCount);
        return copy;
    }

    private void reallocate(int minCapacity) {
        int oldCapacity = this.elements.length;
        int newCapacity = Math.max(oldCapacity, minCapacity + (minCapacity >> 1));
        ASN1Encodable[] copy = new ASN1Encodable[newCapacity];
        System.arraycopy(this.elements, 0, copy, 0, this.elementCount);
        this.elements = copy;
        this.copyOnWrite = false;
    }

    static ASN1Encodable[] cloneElements(ASN1Encodable[] elements) {
        return elements.length < 1 ? EMPTY_ELEMENTS : (ASN1Encodable[])elements.clone();
    }
}

