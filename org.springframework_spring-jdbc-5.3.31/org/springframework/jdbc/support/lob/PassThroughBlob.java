/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StreamUtils
 */
package org.springframework.jdbc.support.lob;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

class PassThroughBlob
implements Blob {
    @Nullable
    private byte[] content;
    @Nullable
    private InputStream binaryStream;
    private long contentLength;

    public PassThroughBlob(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    public PassThroughBlob(InputStream binaryStream, long contentLength) {
        this.binaryStream = binaryStream;
        this.contentLength = contentLength;
    }

    @Override
    public long length() throws SQLException {
        return this.contentLength;
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        if (this.content != null) {
            return new ByteArrayInputStream(this.content);
        }
        return this.binaryStream != null ? this.binaryStream : StreamUtils.emptyInput();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void free() throws SQLException {
    }
}

