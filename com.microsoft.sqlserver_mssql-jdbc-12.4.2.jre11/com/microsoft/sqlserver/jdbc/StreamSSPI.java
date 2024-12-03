/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class StreamSSPI
extends StreamPacket {
    byte[] sspiBlob;

    StreamSSPI() {
        super(237);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (237 != tdsReader.readUnsignedByte()) assert (false);
        int blobLength = tdsReader.readUnsignedShort();
        this.sspiBlob = new byte[blobLength];
        tdsReader.readBytes(this.sspiBlob, 0, blobLength);
    }
}

