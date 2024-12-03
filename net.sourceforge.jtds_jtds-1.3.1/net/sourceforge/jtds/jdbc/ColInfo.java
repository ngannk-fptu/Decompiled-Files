/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.util.Arrays;
import net.sourceforge.jtds.jdbc.CharsetInfo;

public class ColInfo
implements Cloneable {
    int tdsType;
    int jdbcType;
    String realName;
    String name;
    String tableName;
    String catalog;
    String schema;
    int nullable;
    boolean isCaseSensitive;
    boolean isWriteable;
    boolean isIdentity;
    boolean isKey;
    boolean isHidden;
    int userType;
    byte[] collation;
    CharsetInfo charsetInfo;
    int displaySize;
    int bufferSize;
    int precision;
    int scale;
    String sqlType;

    public String toString() {
        return this.name;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object other) {
        if (!(other instanceof ColInfo)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        ColInfo o = (ColInfo)other;
        return this.tdsType == o.tdsType && this.jdbcType == o.jdbcType && this.nullable == o.nullable && this.userType == o.userType && this.displaySize == o.displaySize && this.bufferSize == o.bufferSize && this.precision == o.precision && this.scale == o.scale && this.isCaseSensitive == o.isCaseSensitive && this.isWriteable == o.isWriteable && this.isIdentity == o.isIdentity && this.isKey == o.isKey && this.isHidden == o.isHidden && this.compare(this.realName, o.realName) && this.compare(this.name, o.name) && this.compare(this.tableName, o.tableName) && this.compare(this.catalog, o.catalog) && this.compare(this.schema, o.schema) && this.compare(this.sqlType, o.sqlType) && this.compare(this.charsetInfo, o.charsetInfo) && Arrays.equals(this.collation, o.collation);
    }

    private final boolean compare(Object o1, Object o2) {
        return o1 == o2 || o1 != null && o1.equals(o2);
    }
}

