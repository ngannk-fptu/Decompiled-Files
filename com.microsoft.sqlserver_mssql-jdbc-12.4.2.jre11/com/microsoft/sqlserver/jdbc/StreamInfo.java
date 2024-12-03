/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class StreamInfo
extends StreamPacket {
    final SQLServerError msg = new SQLServerError();

    StreamInfo() {
        super(171);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (171 != tdsReader.readUnsignedByte()) assert (false);
        this.msg.setContentsFromTDS(tdsReader);
    }
}

