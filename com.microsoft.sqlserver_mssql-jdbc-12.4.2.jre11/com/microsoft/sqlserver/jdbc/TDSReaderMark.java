/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.TDSPacket;

final class TDSReaderMark {
    final TDSPacket packet;
    final int payloadOffset;

    TDSReaderMark(TDSPacket packet, int payloadOffset) {
        this.packet = packet;
        this.payloadOffset = payloadOffset;
    }
}

