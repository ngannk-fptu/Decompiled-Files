/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.concurrentlinkedhashmap;

interface Linked<T extends Linked<T>> {
    public T getPrevious();

    public void setPrevious(T var1);

    public T getNext();

    public void setNext(T var1);
}

