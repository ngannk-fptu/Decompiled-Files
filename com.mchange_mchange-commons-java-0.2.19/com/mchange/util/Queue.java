/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import java.util.NoSuchElementException;

public interface Queue
extends Cloneable {
    public void enqueue(Object var1);

    public Object dequeue() throws NoSuchElementException;

    public Object peek() throws NoSuchElementException;

    public boolean hasMoreElements();

    public int size();

    public Object clone();
}

