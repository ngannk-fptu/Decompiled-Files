/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.CharsetInfo;
import net.sourceforge.jtds.jdbc.ColInfo;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.TdsData;

class ParamInfo
implements Cloneable {
    static final int INPUT = 0;
    static final int OUTPUT = 1;
    static final int RETVAL = 2;
    static final int UNICODE = 4;
    int tdsType;
    int jdbcType;
    String name;
    String sqlType;
    int markerPos = -1;
    Object value;
    int precision = -1;
    int scale = -1;
    int length = -1;
    boolean isOutput;
    boolean isRetVal;
    boolean isSet;
    boolean isUnicode;
    byte[] collation;
    CharsetInfo charsetInfo;
    boolean isSetOut;
    Object outValue;

    ParamInfo(int pos, boolean isUnicode) {
        this.markerPos = pos;
        this.isUnicode = isUnicode;
    }

    ParamInfo(String name, int pos, boolean isRetVal, boolean isUnicode) {
        this.name = name;
        this.markerPos = pos;
        this.isRetVal = isRetVal;
        this.isUnicode = isUnicode;
    }

    ParamInfo(int jdbcType, Object value, int flags) {
        this.jdbcType = jdbcType;
        this.value = value;
        this.isSet = true;
        this.isOutput = (flags & 1) > 0 || (flags & 2) > 0;
        this.isRetVal = (flags & 2) > 0;
        boolean bl = this.isUnicode = (flags & 4) > 0;
        if (value instanceof String) {
            this.length = ((String)value).length();
        } else if (value instanceof byte[]) {
            this.length = ((byte[])value).length;
        }
    }

    ParamInfo(ColInfo ci, String name, Object value, int length) {
        this.name = name;
        this.tdsType = ci.tdsType;
        this.scale = ci.scale;
        this.precision = ci.precision;
        this.jdbcType = ci.jdbcType;
        this.sqlType = ci.sqlType;
        this.collation = ci.collation;
        this.charsetInfo = ci.charsetInfo;
        this.isUnicode = TdsData.isUnicode(ci);
        this.isSet = true;
        this.value = value;
        this.length = length;
    }

    Object getOutValue() throws SQLException {
        if (!this.isSetOut) {
            throw new SQLException(Messages.get("error.callable.outparamnotset"), "HY010");
        }
        return this.outValue;
    }

    void setOutValue(Object value) {
        this.outValue = value;
        this.isSetOut = true;
    }

    void clearOutValue() {
        this.outValue = null;
        this.isSetOut = false;
    }

    void clearInValue() {
        this.value = null;
        this.isSet = false;
    }

    String getString(String charset) throws IOException {
        if (this.value == null || this.value instanceof String) {
            return (String)this.value;
        }
        if (this.value instanceof InputStream) {
            try {
                this.value = ParamInfo.loadFromReader(new InputStreamReader((InputStream)this.value, charset), this.length);
                this.length = ((String)this.value).length();
                return (String)this.value;
            }
            catch (UnsupportedEncodingException e) {
                throw new IOException("I/O Error: UnsupportedEncodingException: " + e.getMessage());
            }
        }
        if (this.value instanceof Reader) {
            this.value = ParamInfo.loadFromReader((Reader)this.value, this.length);
            return (String)this.value;
        }
        return this.value.toString();
    }

    byte[] getBytes(String charset) throws IOException {
        if (this.value == null || this.value instanceof byte[]) {
            return (byte[])this.value;
        }
        if (this.value instanceof InputStream) {
            this.value = ParamInfo.loadFromStream((InputStream)this.value, this.length);
            return (byte[])this.value;
        }
        if (this.value instanceof Reader) {
            String tmp = ParamInfo.loadFromReader((Reader)this.value, this.length);
            this.value = Support.encodeString(charset, tmp);
            return (byte[])this.value;
        }
        if (this.value instanceof String) {
            return Support.encodeString(charset, (String)this.value);
        }
        return new byte[0];
    }

    private static byte[] loadFromStream(InputStream in, int length) throws IOException {
        int pos;
        int res;
        byte[] buf = new byte[length];
        for (pos = 0; pos != length && (res = in.read(buf, pos, length - pos)) != -1; pos += res) {
        }
        if (pos != length) {
            throw new IOException("Data in stream less than specified by length");
        }
        if (in.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
        return buf;
    }

    private static String loadFromReader(Reader in, int length) throws IOException {
        int pos;
        int res;
        char[] buf = new char[length];
        for (pos = 0; pos != length && (res = in.read(buf, pos, length - pos)) != -1; pos += res) {
        }
        if (pos != length) {
            throw new IOException("Data in stream less than specified by length");
        }
        if (in.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
        return new String(buf);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}

