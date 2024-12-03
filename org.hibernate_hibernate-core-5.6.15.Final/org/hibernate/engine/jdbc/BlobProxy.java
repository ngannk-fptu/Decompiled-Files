/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.BlobImplementer;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.type.descriptor.java.DataHelper;

public final class BlobProxy
implements Blob,
BlobImplementer {
    private final BinaryStream binaryStream;
    private boolean needsReset;

    private BlobProxy(byte[] bytes) {
        this.binaryStream = new BinaryStreamImpl(bytes);
    }

    private BlobProxy(InputStream stream, long length) {
        this.binaryStream = new StreamBackedBinaryStream(stream, length);
    }

    private InputStream getStream() throws SQLException {
        return this.getUnderlyingStream().getInputStream();
    }

    @Override
    public BinaryStream getUnderlyingStream() throws SQLException {
        this.resetIfNeeded();
        return this.binaryStream;
    }

    private void resetIfNeeded() throws SQLException {
        try {
            if (this.needsReset) {
                this.binaryStream.getInputStream().reset();
            }
        }
        catch (IOException ioe) {
            throw new SQLException("could not reset reader");
        }
        this.needsReset = true;
    }

    public static Blob generateProxy(byte[] bytes) {
        return new BlobProxy(bytes);
    }

    public static Blob generateProxy(InputStream stream, long length) {
        return new BlobProxy(stream, length);
    }

    @Override
    public long length() throws SQLException {
        return this.binaryStream.getLength();
    }

    @Override
    public byte[] getBytes(long start, int length) throws SQLException {
        if (start < 1L) {
            throw new SQLException("Start position 1-based; must be 1 or more.");
        }
        if (length < 0) {
            throw new SQLException("Length must be great-than-or-equal to zero.");
        }
        return DataHelper.extractBytes(this.getStream(), start - 1L, length);
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return this.getStream();
    }

    @Override
    public long position(byte[] pattern, long start) {
        throw BlobProxy.notSupported();
    }

    @Override
    public long position(Blob pattern, long start) {
        throw BlobProxy.notSupported();
    }

    @Override
    public int setBytes(long pos, byte[] bytes) {
        throw BlobProxy.notSupported();
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) {
        throw BlobProxy.notSupported();
    }

    @Override
    public OutputStream setBinaryStream(long pos) {
        throw BlobProxy.notSupported();
    }

    @Override
    public void truncate(long len) {
        throw BlobProxy.notSupported();
    }

    @Override
    public void free() {
        this.binaryStream.release();
    }

    @Override
    public InputStream getBinaryStream(long start, long length) throws SQLException {
        if (start < 1L) {
            throw new SQLException("Start position 1-based; must be 1 or more.");
        }
        if (start > this.length()) {
            throw new SQLException("Start position [" + start + "] cannot exceed overall CLOB length [" + this.length() + "]");
        }
        if (length > Integer.MAX_VALUE) {
            throw new SQLException("Can't deal with Blobs larger than Integer.MAX_VALUE");
        }
        int intLength = (int)length;
        if (intLength < 0) {
            throw new SQLException("Length must be great-than-or-equal to zero.");
        }
        return DataHelper.subStream(this.getStream(), start - 1L, intLength);
    }

    private static UnsupportedOperationException notSupported() {
        return new UnsupportedOperationException("Blob may not be manipulated from creating session");
    }

    private static class StreamBackedBinaryStream
    implements BinaryStream {
        private final InputStream stream;
        private final long length;
        private byte[] bytes;

        private StreamBackedBinaryStream(InputStream stream, long length) {
            this.stream = stream;
            this.length = length;
        }

        @Override
        public InputStream getInputStream() {
            return this.stream;
        }

        @Override
        public byte[] getBytes() {
            if (this.bytes == null) {
                this.bytes = DataHelper.extractBytes(this.stream);
            }
            return this.bytes;
        }

        @Override
        public long getLength() {
            return (int)this.length;
        }

        @Override
        public void release() {
            try {
                this.stream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

