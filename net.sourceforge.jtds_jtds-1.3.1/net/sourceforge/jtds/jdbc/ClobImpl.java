/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.util.BlobBuffer;

public class ClobImpl
implements Clob {
    private static final String EMPTY_CLOB = "";
    private final BlobBuffer blobBuffer;

    ClobImpl(JtdsConnection connection) {
        this(connection, EMPTY_CLOB);
    }

    ClobImpl(JtdsConnection connection, String str) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        this.blobBuffer = new BlobBuffer(connection.getBufferDir(), connection.getLobBuffer());
        try {
            byte[] data = str.getBytes("UTF-16LE");
            this.blobBuffer.setBuffer(data, false);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    BlobBuffer getBlobBuffer() {
        return this.blobBuffer;
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return this.blobBuffer.getBinaryStream(true);
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        try {
            return new BufferedReader(new InputStreamReader(this.blobBuffer.getBinaryStream(false), "UTF-16LE"));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        if (length == 0) {
            return EMPTY_CLOB;
        }
        try {
            byte[] data = this.blobBuffer.getBytes((pos - 1L) * 2L + 1L, length * 2);
            return new String(data, "UTF-16LE");
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    @Override
    public long length() throws SQLException {
        return this.blobBuffer.getLength() / 2L;
    }

    @Override
    public long position(String searchStr, long start) throws SQLException {
        if (searchStr == null) {
            throw new SQLException(Messages.get("error.clob.searchnull"), "HY009");
        }
        try {
            byte[] pattern = searchStr.getBytes("UTF-16LE");
            int pos = this.blobBuffer.position(pattern, (start - 1L) * 2L + 1L);
            return pos < 0 ? (long)pos : (long)((pos - 1) / 2 + 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    @Override
    public long position(Clob searchStr, long start) throws SQLException {
        if (searchStr == null) {
            throw new SQLException(Messages.get("error.clob.searchnull"), "HY009");
        }
        BlobBuffer bbuf = ((ClobImpl)searchStr).getBlobBuffer();
        byte[] pattern = bbuf.getBytes(1L, (int)bbuf.getLength());
        int pos = this.blobBuffer.position(pattern, (start - 1L) * 2L + 1L);
        return pos < 0 ? (long)pos : (long)((pos - 1) / 2 + 1);
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return this.blobBuffer.setBinaryStream((pos - 1L) * 2L + 1L, true);
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        try {
            return new BufferedWriter(new OutputStreamWriter(this.blobBuffer.setBinaryStream((pos - 1L) * 2L + 1L, false), "UTF-16LE"));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        if (str == null) {
            throw new SQLException(Messages.get("error.clob.strnull"), "HY009");
        }
        return this.setString(pos, str, 0, str.length());
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        if (offset < 0 || offset > str.length()) {
            throw new SQLException(Messages.get("error.blobclob.badoffset"), "HY090");
        }
        if (len < 0 || offset + len > str.length()) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        }
        try {
            byte[] data = str.substring(offset, offset + len).getBytes("UTF-16LE");
            return this.blobBuffer.setBytes((pos - 1L) * 2L + 1L, data, 0, data.length, false);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-16LE encoding is not supported.");
        }
    }

    @Override
    public void truncate(long len) throws SQLException {
        this.blobBuffer.truncate(len * 2L);
    }

    @Override
    public void free() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        throw new AbstractMethodError();
    }
}

