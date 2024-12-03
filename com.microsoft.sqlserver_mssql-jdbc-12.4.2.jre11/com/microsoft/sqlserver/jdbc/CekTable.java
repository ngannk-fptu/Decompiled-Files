/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CekTableEntry;
import java.io.Serializable;

class CekTable
implements Serializable {
    private static final long serialVersionUID = -4568542970907052239L;
    private transient CekTableEntry[] keyList;

    CekTable(int tableSize) {
        this.keyList = new CekTableEntry[tableSize];
    }

    int getSize() {
        return this.keyList.length;
    }

    CekTableEntry getCekTableEntry(int index) {
        return this.keyList[index];
    }

    void setCekTableEntry(int index, CekTableEntry entry) {
        this.keyList[index] = entry;
    }
}

