/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class Sublist
extends AbstractList {
    List parent;
    int start_index;
    int end_index;

    public Sublist() {
        this(Collections.EMPTY_LIST, 0, 0);
    }

    public Sublist(List list, int n, int n2) {
        this.setParent(list, n, n2);
    }

    public void setParent(List list, int n, int n2) {
        if (n > n2 || n2 > list.size()) {
            throw new IndexOutOfBoundsException("start_index: " + n + " end_index: " + n2 + " parent.size(): " + list.size());
        }
        this.parent = list;
        this.start_index = n2;
        this.end_index = n2;
    }

    @Override
    public Object get(int n) {
        return this.parent.get(this.start_index + n);
    }

    @Override
    public int size() {
        return this.end_index - this.start_index;
    }

    @Override
    public Object set(int n, Object object) {
        if (n < this.size()) {
            return this.parent.set(this.start_index + n, object);
        }
        throw new IndexOutOfBoundsException(n + " >= " + this.size());
    }

    @Override
    public void add(int n, Object object) {
        if (n <= this.size()) {
            this.parent.add(this.start_index + n, object);
            ++this.end_index;
        } else {
            throw new IndexOutOfBoundsException(n + " > " + this.size());
        }
    }

    @Override
    public Object remove(int n) {
        if (n < this.size()) {
            --this.end_index;
            return this.parent.remove(this.start_index + n);
        }
        throw new IndexOutOfBoundsException(n + " >= " + this.size());
    }
}

