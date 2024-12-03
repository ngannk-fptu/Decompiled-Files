/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.util.internal;

interface Linked<T extends Linked<T>> {
    public T getPrevious();

    public void setPrevious(T var1);

    public T getNext();

    public void setNext(T var1);
}

