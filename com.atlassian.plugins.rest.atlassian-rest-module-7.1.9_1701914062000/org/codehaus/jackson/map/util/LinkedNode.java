/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LinkedNode<T> {
    final T _value;
    final LinkedNode<T> _next;

    public LinkedNode(T value, LinkedNode<T> next) {
        this._value = value;
        this._next = next;
    }

    public LinkedNode<T> next() {
        return this._next;
    }

    public T value() {
        return this._value;
    }

    public static <ST> boolean contains(LinkedNode<ST> node, ST value) {
        while (node != null) {
            if (node.value() == value) {
                return true;
            }
            node = node.next();
        }
        return false;
    }
}

