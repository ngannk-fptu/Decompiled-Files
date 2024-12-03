/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerBlob;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

final class SQLServerBlobOutputStream
extends OutputStream {
    private SQLServerBlob parentBlob = null;
    private long currentPos;

    SQLServerBlobOutputStream(SQLServerBlob parentBlob, long startPos) {
        this.parentBlob = parentBlob;
        this.currentPos = startPos;
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
        try {
            int bytesWritten = this.parentBlob.setBytes(this.currentPos, b, off, len);
            this.currentPos += (long)bytesWritten;
        }
        catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void write(int b) throws IOException {
        byte[] bTemp = new byte[]{(byte)(b & 0xFF)};
        this.write(bTemp, 0, bTemp.length);
    }
}

