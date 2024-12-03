/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamColumns;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class StreamRetValue
extends StreamPacket {
    private String paramName;
    private int ordinalOrLength;
    private int status;

    final int getOrdinalOrLength() {
        return this.ordinalOrLength;
    }

    StreamRetValue() {
        super(172);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (172 != tdsReader.readUnsignedByte()) assert (false);
        this.ordinalOrLength = tdsReader.readUnsignedShort();
        this.paramName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.status = tdsReader.readUnsignedByte();
    }

    CryptoMetadata getCryptoMetadata(TDSReader tdsReader) throws SQLServerException {
        return new StreamColumns().readCryptoMetadata(tdsReader);
    }
}

