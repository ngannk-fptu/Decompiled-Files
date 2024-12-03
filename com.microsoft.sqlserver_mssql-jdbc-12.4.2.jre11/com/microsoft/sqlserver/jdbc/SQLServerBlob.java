/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerBlobOutputStream;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerLob;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SQLServerBlob
extends SQLServerLob
implements Blob,
Serializable {
    private static final long serialVersionUID = -3526170228097889085L;
    private static final String R_CANT_SET_NULL = "R_cantSetNull";
    private static final String R_INVALID_POSITION_INDEX = "R_invalidPositionIndex";
    private static final String R_INVALID_LENGTH = "R_invalidLength";
    private static final Logger _LOGGER = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerBlob");
    private static final AtomicInteger BASE_ID = new AtomicInteger(0);
    private byte[] value;
    private transient SQLServerConnection con;
    private boolean isClosed = false;
    ArrayList<Closeable> activeStreams = new ArrayList(1);
    private final String traceID = this.getClass().getSimpleName() + SQLServerBlob.nextInstanceID();

    public final String toString() {
        return this.traceID;
    }

    private static int nextInstanceID() {
        return BASE_ID.incrementAndGet();
    }

    @Deprecated
    public SQLServerBlob(SQLServerConnection connection, byte[] data) {
        this.con = connection;
        if (null == data) {
            throw new NullPointerException(SQLServerException.getErrString(R_CANT_SET_NULL));
        }
        this.value = data;
        if (_LOGGER.isLoggable(Level.FINE)) {
            String loggingInfo = null != connection ? connection.toString() : "null connection";
            _LOGGER.fine(this.toString() + " created by (" + loggingInfo + ")");
        }
    }

    SQLServerBlob(SQLServerConnection connection) {
        this.con = connection;
        this.value = new byte[0];
        if (_LOGGER.isLoggable(Level.FINE)) {
            _LOGGER.fine(this.toString() + " created by (" + connection.toString() + ")");
        }
    }

    SQLServerBlob(BaseInputStream stream) {
        this.activeStreams.add(stream);
        if (_LOGGER.isLoggable(Level.FINE)) {
            _LOGGER.fine(this.toString() + " created by (null connection)");
        }
    }

    @Override
    public void free() throws SQLException {
        if (!this.isClosed) {
            if (null != this.activeStreams) {
                for (Closeable stream : this.activeStreams) {
                    try {
                        stream.close();
                    }
                    catch (IOException ioException) {
                        _LOGGER.fine(this.toString() + " ignored IOException closing stream " + stream + ": " + ioException.getMessage());
                    }
                }
                this.activeStreams = null;
            }
            this.value = null;
            this.isClosed = true;
        }
    }

    private void checkClosed() throws SQLServerException {
        if (this.isClosed) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[]{"Blob"}), null, true);
        }
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        if (!this.delayLoadingLob && null == this.value && !this.activeStreams.isEmpty()) {
            this.getBytesFromStream();
        }
        if (null == this.value && !this.activeStreams.isEmpty()) {
            InputStream stream = (InputStream)this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (IOException e) {
                throw new SQLServerException(e.getMessage(), null, 0, (Throwable)e);
            }
            return (InputStream)this.activeStreams.get(0);
        }
        if (this.value == null) {
            throw new SQLServerException("Unexpected Error: blob value is null while all streams are closed.", null);
        }
        return this.getBinaryStreamInternal(0, this.value.length);
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return null;
    }

    private InputStream getBinaryStreamInternal(int pos, int length) {
        assert (null != this.value);
        assert (pos >= 0);
        assert (0 <= length && length <= this.value.length - pos);
        assert (null != this.activeStreams);
        ByteArrayInputStream getterStream = new ByteArrayInputStream(this.value, pos, length);
        this.activeStreams.add(getterStream);
        return getterStream;
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        Object[] msgArgs;
        MessageFormat form;
        this.checkClosed();
        this.getBytesFromStream();
        if (pos < 1L) {
            form = new MessageFormat(SQLServerException.getErrString(R_INVALID_POSITION_INDEX));
            msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (length < 0) {
            form = new MessageFormat(SQLServerException.getErrString(R_INVALID_LENGTH));
            msgArgs = new Object[]{length};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (--pos > (long)this.value.length) {
            pos = this.value.length;
        }
        if ((long)length > (long)this.value.length - pos) {
            length = (int)((long)this.value.length - pos);
        }
        byte[] bTemp = new byte[length];
        System.arraycopy(this.value, (int)pos, bTemp, 0, length);
        return bTemp;
    }

    @Override
    public long length() throws SQLException {
        this.checkClosed();
        if (this.value == null && this.activeStreams.get(0) instanceof BaseInputStream) {
            return ((BaseInputStream)this.activeStreams.get((int)0)).payloadLength;
        }
        this.getBytesFromStream();
        return this.value.length;
    }

    @Override
    void fillFromStream() throws SQLException {
        if (!this.isClosed) {
            this.getBytesFromStream();
        }
    }

    private void getBytesFromStream() throws SQLServerException {
        if (null == this.value) {
            BaseInputStream stream = (BaseInputStream)this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (IOException e) {
                throw new SQLServerException(e.getMessage(), null, 0, (Throwable)e);
            }
            this.value = stream.getBytes();
        }
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (start < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(R_INVALID_POSITION_INDEX));
            Object[] msgArgs = new Object[]{start};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == pattern) {
            return -1L;
        }
        return this.position(pattern.getBytes(1L, (int)pattern.length()), start);
    }

    @Override
    public long position(byte[] bPattern, long start) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (start < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(R_INVALID_POSITION_INDEX));
            Object[] msgArgs = new Object[]{start};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == bPattern) {
            return -1L;
        }
        for (int pos = (int)(--start); pos <= this.value.length - bPattern.length; ++pos) {
            boolean match = true;
            for (int i = 0; i < bPattern.length; ++i) {
                if (this.value[pos + i] == bPattern[i]) continue;
                match = false;
                break;
            }
            if (!match) continue;
            return (long)pos + 1L;
        }
        return -1L;
    }

    @Override
    public void truncate(long len) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (len < 0L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(R_INVALID_LENGTH));
            Object[] msgArgs = new Object[]{len};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if ((long)this.value.length > len) {
            byte[] bNew = new byte[(int)len];
            System.arraycopy(this.value, 0, bNew, 0, (int)len);
            this.value = bNew;
        }
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString(R_INVALID_POSITION_INDEX));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[]{pos}), null, true);
        }
        return new SQLServerBlobOutputStream(this, pos);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        this.checkClosed();
        this.getBytesFromStream();
        if (null == bytes) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString(R_CANT_SET_NULL), null, true);
        }
        return this.setBytes(pos, bytes, 0, bytes.length);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        Object[] msgArgs;
        MessageFormat form;
        this.checkClosed();
        this.getBytesFromStream();
        if (null == bytes) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString(R_CANT_SET_NULL), null, true);
        }
        if (offset < 0 || offset > bytes.length) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidOffset"));
            msgArgs = new Object[]{offset};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len < 0 || len > bytes.length - offset) {
            form = new MessageFormat(SQLServerException.getErrString(R_INVALID_LENGTH));
            msgArgs = new Object[]{len};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (pos <= 0L || pos > (long)(this.value.length + 1)) {
            form = new MessageFormat(SQLServerException.getErrString(R_INVALID_POSITION_INDEX));
            msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if ((long)len >= (long)this.value.length - --pos) {
            DataTypes.getCheckedLength(this.con, JDBCType.BLOB, pos + (long)len, false);
            byte[] combinedValue = new byte[(int)pos + len];
            System.arraycopy(this.value, 0, combinedValue, 0, (int)pos);
            System.arraycopy(bytes, offset, combinedValue, (int)pos, len);
            this.value = combinedValue;
        } else {
            System.arraycopy(bytes, offset, this.value, (int)pos, len);
        }
        return len;
    }
}

