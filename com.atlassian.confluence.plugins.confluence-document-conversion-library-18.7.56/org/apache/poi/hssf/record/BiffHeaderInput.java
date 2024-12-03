/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

public interface BiffHeaderInput {
    public int readRecordSID();

    public int readDataSize();

    public int available();
}

