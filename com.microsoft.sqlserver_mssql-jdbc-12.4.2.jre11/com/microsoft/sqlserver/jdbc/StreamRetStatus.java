/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class StreamRetStatus
extends StreamPacket {
    private int status;

    final int getStatus() {
        return this.status;
    }

    StreamRetStatus() {
        super(121);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (121 != tdsReader.readUnsignedByte()) assert (false);
        this.status = tdsReader.readInt();
    }
}

