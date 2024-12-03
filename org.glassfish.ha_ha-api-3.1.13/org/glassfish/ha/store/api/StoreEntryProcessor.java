/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

import java.io.Serializable;

public interface StoreEntryProcessor<K, V extends Serializable>
extends Serializable {
    public Serializable process(K var1, V var2);
}

