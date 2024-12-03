/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerClobBase;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

final class SQLServerClobWriter
extends Writer {
    private SQLServerClobBase parentClob = null;
    private long streamPos;

    SQLServerClobWriter(SQLServerClobBase parentClob, long streamPos) {
        this.parentClob = parentClob;
        this.streamPos = streamPos;
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        if (null == cbuf) {
            return;
        }
        this.write(new String(cbuf));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (null == cbuf) {
            return;
        }
        this.write(new String(cbuf, off, len));
    }

    @Override
    public void write(int b) throws IOException {
        char[] c = new char[]{(char)b};
        this.write(new String(c));
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        this.checkClosed();
        try {
            int charsWritten = this.parentClob.setString(this.streamPos, str, off, len);
            this.streamPos += (long)charsWritten;
        }
        catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void write(String str) throws IOException {
        if (null == str) {
            return;
        }
        this.write(str, 0, str.length());
    }

    @Override
    public void flush() throws IOException {
        this.checkClosed();
    }

    @Override
    public void close() throws IOException {
        this.checkClosed();
        this.parentClob = null;
    }

    private void checkClosed() throws IOException {
        if (null == this.parentClob) {
            throw new IOException(SQLServerException.getErrString("R_streamIsClosed"));
        }
    }
}

