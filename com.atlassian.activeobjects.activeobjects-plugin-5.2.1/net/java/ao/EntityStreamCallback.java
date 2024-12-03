/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import net.java.ao.RawEntity;

public interface EntityStreamCallback<T extends RawEntity<K>, K> {
    public void onRowRead(T var1);
}

