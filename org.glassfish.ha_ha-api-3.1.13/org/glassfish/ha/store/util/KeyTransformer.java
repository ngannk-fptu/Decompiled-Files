/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.util;

public interface KeyTransformer<K> {
    public byte[] keyToByteArray(K var1);

    public K byteArrayToKey(byte[] var1, int var2, int var3);
}

