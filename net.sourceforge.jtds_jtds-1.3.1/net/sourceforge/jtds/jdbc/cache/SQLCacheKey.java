/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc.cache;

import net.sourceforge.jtds.jdbc.JtdsConnection;

public class SQLCacheKey {
    private final String sql;
    private final int serverType;
    private final int majorVersion;
    private final int minorVersion;
    private final int hashCode;

    public SQLCacheKey(String sql, JtdsConnection connection) {
        this.sql = sql;
        this.serverType = connection.getServerType();
        this.majorVersion = connection.getDatabaseMajorVersion();
        this.minorVersion = connection.getDatabaseMinorVersion();
        this.hashCode = sql.hashCode() ^ (this.serverType << 24 | this.majorVersion << 16 | this.minorVersion);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object object) {
        try {
            SQLCacheKey key = (SQLCacheKey)object;
            return this.hashCode == key.hashCode && this.majorVersion == key.majorVersion && this.minorVersion == key.minorVersion && this.serverType == key.serverType && this.sql.equals(key.sql);
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (NullPointerException e) {
            return false;
        }
    }
}

