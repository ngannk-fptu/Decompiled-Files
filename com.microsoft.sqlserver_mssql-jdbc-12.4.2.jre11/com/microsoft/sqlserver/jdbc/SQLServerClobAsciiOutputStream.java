/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerClobBase;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

final class SQLServerClobAsciiOutputStream
extends OutputStream {
    private SQLServerClobBase parentClob = null;
    private long streamPos;
    private byte[] bSingleByte = new byte[1];

    SQLServerClobAsciiOutputStream(SQLServerClobBase parentClob, long streamPos) {
        this.parentClob = parentClob;
        this.streamPos = streamPos;
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (null == b) {
            return;
        }
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (null == b) {
            return;
        }
        try {
            String s = new String(b, off, len, StandardCharsets.US_ASCII);
            int charsWritten = this.parentClob.setString(this.streamPos, s);
            this.streamPos += (long)charsWritten;
        }
        catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.bSingleByte[0] = (byte)(b & 0xFF);
        this.write(this.bSingleByte, 0, this.bSingleByte.length);
    }
}

