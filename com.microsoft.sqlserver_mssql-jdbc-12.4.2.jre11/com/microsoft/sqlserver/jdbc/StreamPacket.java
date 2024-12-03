/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDSReader;

abstract class StreamPacket {
    int packetType;

    final int getTokenType() {
        return this.packetType;
    }

    StreamPacket() {
        this.packetType = 0;
    }

    StreamPacket(int packetType) {
        this.packetType = packetType;
    }

    abstract void setFromTDS(TDSReader var1) throws SQLServerException;
}

