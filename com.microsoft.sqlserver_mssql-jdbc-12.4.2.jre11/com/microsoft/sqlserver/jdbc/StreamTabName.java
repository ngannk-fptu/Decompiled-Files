/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Column;
import com.microsoft.sqlserver.jdbc.SQLIdentifier;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;

final class StreamTabName
extends StreamPacket {
    private TDSReader tdsReader;
    private TDSReaderMark tableNamesMark;

    StreamTabName() {
        super(164);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (164 != tdsReader.readUnsignedByte()) assert (false) : "Not a TABNAME token";
        this.tdsReader = tdsReader;
        int tokenLength = tdsReader.readUnsignedShort();
        this.tableNamesMark = tdsReader.mark();
        tdsReader.skip(tokenLength);
    }

    void applyTo(Column[] columns, int numTables) throws SQLServerException {
        TDSReaderMark currentMark = this.tdsReader.mark();
        this.tdsReader.reset(this.tableNamesMark);
        SQLIdentifier[] tableNames = new SQLIdentifier[numTables];
        for (int i = 0; i < numTables; ++i) {
            tableNames[i] = this.tdsReader.readSQLIdentifier();
        }
        for (Column col : columns) {
            if (col.getTableNum() <= 0) continue;
            col.setTableName(tableNames[col.getTableNum() - 1]);
        }
        this.tdsReader.reset(currentMark);
    }
}

