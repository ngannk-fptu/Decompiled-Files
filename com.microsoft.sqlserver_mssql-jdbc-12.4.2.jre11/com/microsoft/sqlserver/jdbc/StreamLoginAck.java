/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StreamPacket;
import com.microsoft.sqlserver.jdbc.TDSReader;

final class StreamLoginAck
extends StreamPacket {
    String sSQLServerVersion;
    int tdsVersion;

    StreamLoginAck() {
        super(173);
    }

    @Override
    void setFromTDS(TDSReader tdsReader) throws SQLServerException {
        if (173 != tdsReader.readUnsignedByte()) assert (false);
        tdsReader.readUnsignedShort();
        tdsReader.readUnsignedByte();
        this.tdsVersion = tdsReader.readIntBigEndian();
        tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        int serverMajorVersion = tdsReader.readUnsignedByte();
        int serverMinorVersion = tdsReader.readUnsignedByte();
        int serverBuildNumber = tdsReader.readUnsignedByte() << 8 | tdsReader.readUnsignedByte();
        this.sSQLServerVersion = serverMajorVersion + "." + (serverMinorVersion <= 9 ? "0" : "") + serverMinorVersion + "." + serverBuildNumber;
    }
}

