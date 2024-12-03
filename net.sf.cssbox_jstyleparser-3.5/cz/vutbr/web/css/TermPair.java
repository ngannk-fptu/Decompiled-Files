/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Term;

public interface TermPair<K, V>
extends Term<V> {
    public K getKey();

    public TermPair<K, V> setKey(K var1);
}

