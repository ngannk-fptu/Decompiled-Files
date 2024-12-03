/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.jdbc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;
import org.postgresql.Driver;
import org.postgresql.core.BaseConnection;
import org.postgresql.jdbc.AbstractBlobClob;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.largeobject.LargeObject;

public class PgClob
extends AbstractBlobClob
implements Clob {
    public PgClob(BaseConnection conn, long oid) throws SQLException {
        super(conn, oid);
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "getCharacterStream(long, long)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "setString(long,str)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "setString(long,String,int,int)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "setAsciiStream(long)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "setCharacterStream(long)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return this.getBinaryStream();
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            Charset connectionCharset = Charset.forName(this.conn.getEncoding().name());
            InputStreamReader inputStreamReader = new InputStreamReader(this.getBinaryStream(), connectionCharset);
            return inputStreamReader;
        }
    }

    @Override
    public String getSubString(long i, int j) throws SQLException {
        try (ResourceLock ignore = this.lock.obtain();){
            this.assertPosition(i, j);
            LargeObject lo = this.getLo(false);
            lo.seek((int)i - 1);
            String string = new String(lo.read(j));
            return string;
        }
    }

    @Override
    public long position(String pattern, long start) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "position(String,long)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }

    @Override
    public long position(Clob pattern, long start) throws SQLException {
        ResourceLock ignore = this.lock.obtain();
        Throwable throwable = null;
        try {
            try {
                this.checkFreed();
                throw Driver.notImplemented(this.getClass(), "position(Clob,start)");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (Throwable throwable3) {
            if (ignore != null) {
                if (throwable != null) {
                    try {
                        ignore.close();
                    }
                    catch (Throwable throwable4) {
                        throwable.addSuppressed(throwable4);
                    }
                } else {
                    ignore.close();
                }
            }
            throw throwable3;
        }
    }
}

