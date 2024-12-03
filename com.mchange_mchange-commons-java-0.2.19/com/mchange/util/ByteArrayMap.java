/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.io.IOByteArrayEnumeration;
import com.mchange.io.IOByteArrayMap;
import com.mchange.util.ByteArrayEnumeration;

public interface ByteArrayMap
extends IOByteArrayMap {
    @Override
    public byte[] get(byte[] var1);

    @Override
    public void put(byte[] var1, byte[] var2);

    @Override
    public boolean putNoReplace(byte[] var1, byte[] var2);

    @Override
    public boolean remove(byte[] var1);

    @Override
    public boolean containsKey(byte[] var1);

    @Override
    public IOByteArrayEnumeration keys();

    public ByteArrayEnumeration mkeys();
}

