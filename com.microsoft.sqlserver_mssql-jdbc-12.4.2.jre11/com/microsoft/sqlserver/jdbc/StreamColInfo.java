/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Column;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;

final class StreamColInfo
extends StreamPacket {
    private TDSReader tdsReader;
    private TDSReaderMark colInfoMark;

    StreamColInfo() {
        super(165);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (165 != tdsReader.readUnsignedByte()) assert (false) : "Not a COLINFO token";
        this.tdsReader = tdsReader;
        int tokenLength = tdsReader.readUnsignedShort();
        this.colInfoMark = tdsReader.mark();
        tdsReader.skip(tokenLength);
    }

    int applyTo(Column[] columns) throws SQLServerException {
        int numTables = 0;
        TDSReaderMark currentMark = this.tdsReader.mark();
        this.tdsReader.reset(this.colInfoMark);
        for (Column col : columns) {
            this.tdsReader.readUnsignedByte();
            col.setTableNum(this.tdsReader.readUnsignedByte());
            if (col.getTableNum() > numTables) {
                numTables = col.getTableNum();
            }
            col.setInfoStatus(this.tdsReader.readUnsignedByte());
            if (!col.hasDifferentName()) continue;
            col.setBaseColumnName(this.tdsReader.readUnicodeString(this.tdsReader.readUnsignedByte()));
        }
        this.tdsReader.reset(currentMark);
        return numTables;
    }
}

