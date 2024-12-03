/*
 * Decompiled with CFR 0.152.
 */
package antlr.collections;

import java.util.NoSuchElementException;

public interface Stack {
    public int height();

    public Object pop() throws NoSuchElementException;

    public void push(Object var1);

    public Object top() throws NoSuchElementException;
}

