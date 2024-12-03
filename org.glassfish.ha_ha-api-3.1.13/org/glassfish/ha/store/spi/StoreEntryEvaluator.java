/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.io.Serializable;

public interface StoreEntryEvaluator<K, V>
extends Serializable {
    public Object eval(K var1, V var2);
}

