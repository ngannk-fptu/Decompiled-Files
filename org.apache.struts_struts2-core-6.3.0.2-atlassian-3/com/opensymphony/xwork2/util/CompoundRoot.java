/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CompoundRoot
extends CopyOnWriteArrayList<Object> {
    private static final long serialVersionUID = 8563229069192473995L;

    public CompoundRoot() {
    }

    public CompoundRoot(List<?> list) {
        super(list);
    }

    public CompoundRoot cutStack(int index) {
        return new CompoundRoot(this.subList(index, this.size()));
    }

    public Object peek() {
        return this.get(0);
    }

    public Object pop() {
        return this.remove(0);
    }

    public void push(Object o) {
        this.add(0, o);
    }
}

