/*
 * Decompiled with CFR 0.152.
 */
package com.graphbuilder.struc;

import com.graphbuilder.struc.LinkedList;

public class Stack
extends LinkedList {
    public Object peek() {
        if (this.head == null) {
            return null;
        }
        return this.head.userObject;
    }

    public Object pop() {
        return this.removeHead();
    }

    public void push(Object o) {
        this.addToHead(o);
    }
}

