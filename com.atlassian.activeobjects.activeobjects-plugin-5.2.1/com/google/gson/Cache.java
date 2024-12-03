/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface Cache<K, V> {
    public void addElement(K var1, V var2);

    public V getElement(K var1);

    public V removeElement(K var1);
}

