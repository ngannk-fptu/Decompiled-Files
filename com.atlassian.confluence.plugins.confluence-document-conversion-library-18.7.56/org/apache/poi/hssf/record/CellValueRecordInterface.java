/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

public interface CellValueRecordInterface {
    public int getRow();

    public short getColumn();

    public void setRow(int var1);

    public void setColumn(short var1);

    public void setXFIndex(short var1);

    public short getXFIndex();
}

