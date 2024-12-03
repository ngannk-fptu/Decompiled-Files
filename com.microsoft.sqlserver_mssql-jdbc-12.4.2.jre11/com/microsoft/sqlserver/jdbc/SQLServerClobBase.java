/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerClobAsciiOutputStream;
import com.microsoft.sqlserver.jdbc.SQLServerClobWriter;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerLob;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class SQLServerClobBase
extends SQLServerLob {
    private static final long serialVersionUID = 8691072211054430124L;
    String value;
    private final SQLCollation sqlCollation;
    private boolean isClosed = false;
    final TypeInfo typeInfo;
    private ArrayList<Closeable> activeStreams = new ArrayList(1);
    transient SQLServerConnection con;
    private final transient Logger logger;
    private final String traceID = this.getClass().getName().substring(1 + this.getClass().getName().lastIndexOf(46)) + ":" + SQLServerClobBase.nextInstanceID();
    private static final AtomicInteger BASE_ID = new AtomicInteger(0);
    private transient Charset defaultCharset = null;

    public final String toString() {
        return this.traceID;
    }

    private static int nextInstanceID() {
        return BASE_ID.incrementAndGet();
    }

    abstract JDBCType getJdbcType();

    private String getDisplayClassName() {
        String fullClassName = this.getJdbcType().className();
        return fullClassName.substring(1 + fullClassName.lastIndexOf(46));
    }

    SQLServerClobBase(SQLServerConnection connection, Object data, SQLCollation collation, Logger logger, TypeInfo typeInfo) {
        this.con = connection;
        if (data instanceof BaseInputStream) {
            this.activeStreams.add((Closeable)data);
        } else {
            this.value = (String)data;
        }
        this.sqlCollation = collation;
        this.logger = logger;
        this.typeInfo = typeInfo;
        if (logger.isLoggable(Level.FINE)) {
            String loggingInfo = null != connection ? connection.toString() : "null connection";
            logger.fine(this.toString() + " created by (" + loggingInfo + ")");
        }
    }

    public void free() throws SQLException {
        if (!this.isClosed) {
            if (null != this.activeStreams) {
                for (Closeable stream : this.activeStreams) {
                    try {
                        stream.close();
                    }
                    catch (IOException ioException) {
                        this.logger.fine(this.toString() + " ignored IOException closing stream " + stream + ": " + ioException.getMessage());
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
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[]{this.getDisplayClassName()}), null, true);
        }
    }

    public InputStream getAsciiStream() throws SQLException {
        this.checkClosed();
        if (null != this.sqlCollation && !this.sqlCollation.supportsAsciiConversion()) {
            DataTypes.throwConversionError(this.getDisplayClassName(), "AsciiStream");
        }
        if (!this.delayLoadingLob && null == this.value && !this.activeStreams.isEmpty()) {
            this.getStringFromStream();
        }
        InputStream getterStream = null;
        if (null == this.value && !this.activeStreams.isEmpty()) {
            InputStream inputStream = (InputStream)this.activeStreams.get(0);
            try {
                inputStream.reset();
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            getterStream = new BufferedInputStream(inputStream);
        } else if (null != this.value) {
            getterStream = new ByteArrayInputStream(this.value.getBytes(StandardCharsets.US_ASCII));
        }
        this.activeStreams.add(getterStream);
        return getterStream;
    }

    public Reader getCharacterStream() throws SQLException {
        this.checkClosed();
        if (!this.delayLoadingLob && null == this.value && !this.activeStreams.isEmpty()) {
            this.getStringFromStream();
        }
        Reader getterStream = null;
        if (null == this.value && !this.activeStreams.isEmpty()) {
            InputStream inputStream = (InputStream)this.activeStreams.get(0);
            try {
                inputStream.reset();
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            Charset cs = this.defaultCharset == null ? this.typeInfo.getCharset() : this.defaultCharset;
            getterStream = new BufferedReader(new InputStreamReader(inputStream, cs));
        } else {
            getterStream = new StringReader(this.value);
        }
        this.activeStreams.add(getterStream);
        return getterStream;
    }

    public Reader getCharacterStream(long pos, long length) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return null;
    }

    public String getSubString(long pos, int length) throws SQLException {
        Object[] msgArgs;
        MessageFormat form;
        this.checkClosed();
        this.getStringFromStream();
        if (pos < 1L) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (length < 0) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            msgArgs = new Object[]{length};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (--pos > (long)this.value.length()) {
            pos = this.value.length();
        }
        if ((long)length > (long)this.value.length() - pos) {
            length = (int)((long)this.value.length() - pos);
        }
        return this.value.substring((int)pos, (int)pos + length);
    }

    public long length() throws SQLException {
        this.checkClosed();
        if (null == this.value && this.activeStreams.get(0) instanceof BaseInputStream) {
            int length = ((BaseInputStream)this.activeStreams.get((int)0)).payloadLength;
            if (null != this.typeInfo) {
                String columnTypeName = this.typeInfo.getSSTypeName();
                return "nvarchar".equalsIgnoreCase(columnTypeName) || "ntext".equalsIgnoreCase(columnTypeName) ? (long)(length / 2) : (long)length;
            }
            return length;
        }
        if (null == this.value) {
            return 0L;
        }
        return this.value.length();
    }

    @Override
    void fillFromStream() throws SQLException {
        if (!this.isClosed) {
            this.getStringFromStream();
        }
    }

    private void getStringFromStream() throws SQLServerException {
        if (null == this.value && !this.activeStreams.isEmpty()) {
            BaseInputStream stream = (BaseInputStream)this.activeStreams.get(0);
            try {
                stream.reset();
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(this.con, null, e.getMessage(), null, false);
            }
            Charset cs = this.defaultCharset == null ? this.typeInfo.getCharset() : this.defaultCharset;
            this.value = new String(stream.getBytes(), cs);
        }
    }

    public long position(Clob searchstr, long start) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (start < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            Object[] msgArgs = new Object[]{start};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == searchstr) {
            return -1L;
        }
        return this.position(searchstr.getSubString(1L, (int)searchstr.length()), start);
    }

    public long position(String searchstr, long start) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (start < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            Object[] msgArgs = new Object[]{start};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (null == searchstr) {
            return -1L;
        }
        int pos = this.value.indexOf(searchstr, (int)(start - 1L));
        if (-1 != pos) {
            return (long)pos + 1L;
        }
        return -1L;
    }

    public void truncate(long len) throws SQLException {
        this.checkClosed();
        this.getStringFromStream();
        if (len < 0L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            Object[] msgArgs = new Object[]{len};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len <= Integer.MAX_VALUE && (long)this.value.length() > len) {
            this.value = this.value.substring(0, (int)len);
        }
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            Object[] msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return new SQLServerClobAsciiOutputStream(this, pos);
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        this.checkClosed();
        if (pos < 1L) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            Object[] msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return new SQLServerClobWriter(this, pos);
    }

    public int setString(long pos, String s) throws SQLException {
        this.checkClosed();
        if (null == s) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        return this.setString(pos, s, 0, s.length());
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        StringBuilder sb;
        Object[] msgArgs;
        MessageFormat form;
        this.checkClosed();
        this.getStringFromStream();
        if (null == str) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        if (offset < 0 || offset > str.length()) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidOffset"));
            msgArgs = new Object[]{offset};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (len < 0 || len > str.length() - offset) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            msgArgs = new Object[]{len};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if (pos < 1L || pos > (long)(this.value.length() + 1)) {
            form = new MessageFormat(SQLServerException.getErrString("R_invalidPositionIndex"));
            msgArgs = new Object[]{pos};
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        if ((long)len >= (long)this.value.length() - --pos) {
            DataTypes.getCheckedLength(this.con, this.getJdbcType(), pos + (long)len, false);
            if (pos + (long)len > Integer.MAX_VALUE) {
                form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
                msgArgs = new Object[]{pos};
                SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            }
            sb = new StringBuilder((int)pos + len);
            sb.append(this.value.substring(0, (int)pos));
            sb.append(str.substring(offset, offset + len));
            this.value = sb.toString();
        } else {
            sb = new StringBuilder(this.value.length());
            sb.append(this.value.substring(0, (int)pos));
            sb.append(str.substring(offset, offset + len));
            sb.append(this.value.substring((int)pos + len));
            this.value = sb.toString();
        }
        return len;
    }

    protected void setDefaultCharset(Charset c) {
        this.defaultCharset = c;
    }
}

