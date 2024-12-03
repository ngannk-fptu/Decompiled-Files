/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.Queue;
import com.mchange.util.impl.CircularList;

public class CircularListQueue
implements Queue,
Cloneable {
    CircularList list;

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean hasMoreElements() {
        return this.list.size() > 0;
    }

    @Override
    public void enqueue(Object object) {
        this.list.appendElement(object);
    }

    @Override
    public Object peek() {
        return this.list.getFirstElement();
    }

    @Override
    public Object dequeue() {
        Object object = this.list.getFirstElement();
        this.list.removeFirstElement();
        return object;
    }

    @Override
    public Object clone() {
        return new CircularListQueue((CircularList)this.list.clone());
    }

    public CircularListQueue() {
        this.list = new CircularList();
    }

    private CircularListQueue(CircularList circularList) {
        this.list = circularList;
    }
}

