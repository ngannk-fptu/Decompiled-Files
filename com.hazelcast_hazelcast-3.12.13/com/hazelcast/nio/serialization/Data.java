/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

public interface Data {
    public byte[] toByteArray();

    public int getType();

    public int totalSize();

    public void copyTo(byte[] var1, int var2);

    public int dataSize();

    public int getHeapCost();

    public int getPartitionHash();

    public boolean hasPartitionHash();

    public long hash64();

    public boolean isPortable();

    public boolean isJson();
}

