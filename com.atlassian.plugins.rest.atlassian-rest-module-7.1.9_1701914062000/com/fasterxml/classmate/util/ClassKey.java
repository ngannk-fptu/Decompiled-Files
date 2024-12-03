/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.util;

import java.io.Serializable;

public class ClassKey
implements Comparable<ClassKey>,
Serializable {
    private final String _className;
    private final Class<?> _class;
    private final int _hashCode;

    public ClassKey(Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }

    @Override
    public int compareTo(ClassKey other) {
        return this._className.compareTo(other._className);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ClassKey other = (ClassKey)o;
        return other._class == this._class;
    }

    public int hashCode() {
        return this._hashCode;
    }

    public String toString() {
        return this._className;
    }
}

