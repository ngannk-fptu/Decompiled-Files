/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.util.BlobBuffer;

public class BlobImpl
implements Blob {
    private static final byte[] EMPTY_BLOB = new byte[0];
    private final BlobBuffer blobBuffer;

    BlobImpl(JtdsConnection connection) {
        this(connection, EMPTY_BLOB);
    }

    BlobImpl(JtdsConnection connection, byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes cannot be null");
        }
        this.blobBuffer = new BlobBuffer(connection.getBufferDir(), connection.getLobBuffer());
        this.blobBuffer.setBuffer(bytes, false);
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return this.blobBuffer.getBinaryStream(false);
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return this.blobBuffer.getBytes(pos, length);
    }

    @Override
    public long length() throws SQLException {
        return this.blobBuffer.getLength();
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return this.blobBuffer.position(pattern, start);
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        if (pattern == null) {
            throw new SQLException(Messages.get("error.blob.badpattern"), "HY009");
        }
        return this.blobBuffer.position(pattern.getBytes(1L, (int)pattern.length()), start);
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return this.blobBuffer.setBinaryStream(pos, false);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        if (bytes == null) {
            throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
        }
        return this.setBytes(pos, bytes, 0, bytes.length);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        if (bytes == null) {
            throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
        }
        return this.blobBuffer.setBytes(pos, bytes, offset, len, true);
    }

    @Override
    public void truncate(long len) throws SQLException {
        this.blobBuffer.truncate(len);
    }

    @Override
    public void free() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        throw new AbstractMethodError();
    }
}

