/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeInfo
implements Comparable {
    static final int NUM_COLS = 18;
    private final String typeName;
    private final int dataType;
    private final int precision;
    private final String literalPrefix;
    private final String literalSuffix;
    private final String createParams;
    private final short nullable;
    private final boolean caseSensitive;
    private final short searchable;
    private final boolean unsigned;
    private final boolean fixedPrecScale;
    private final boolean autoIncrement;
    private final String localTypeName;
    private final short minimumScale;
    private final short maximumScale;
    private final int sqlDataType;
    private final int sqlDatetimeSub;
    private final int numPrecRadix;
    private final int normalizedType;
    private final int distanceFromJdbcType;

    public TypeInfo(ResultSet rs, boolean useLOBs) throws SQLException {
        this.typeName = rs.getString(1);
        this.dataType = rs.getInt(2);
        this.precision = rs.getInt(3);
        this.literalPrefix = rs.getString(4);
        this.literalSuffix = rs.getString(5);
        this.createParams = rs.getString(6);
        this.nullable = rs.getShort(7);
        this.caseSensitive = rs.getBoolean(8);
        this.searchable = rs.getShort(9);
        this.unsigned = rs.getBoolean(10);
        this.fixedPrecScale = rs.getBoolean(11);
        this.autoIncrement = rs.getBoolean(12);
        this.localTypeName = rs.getString(13);
        if (rs.getMetaData().getColumnCount() >= 18) {
            this.minimumScale = rs.getShort(14);
            this.maximumScale = rs.getShort(15);
            this.sqlDataType = rs.getInt(16);
            this.sqlDatetimeSub = rs.getInt(17);
            this.numPrecRadix = rs.getInt(18);
        } else {
            this.minimumScale = 0;
            this.maximumScale = 0;
            this.sqlDataType = 0;
            this.sqlDatetimeSub = 0;
            this.numPrecRadix = 0;
        }
        this.normalizedType = TypeInfo.normalizeDataType(this.dataType, useLOBs);
        this.distanceFromJdbcType = this.determineDistanceFromJdbcType();
    }

    public TypeInfo(String typeName, int dataType, boolean autoIncrement) {
        this.typeName = typeName;
        this.dataType = dataType;
        this.autoIncrement = autoIncrement;
        this.precision = 0;
        this.literalPrefix = null;
        this.literalSuffix = null;
        this.createParams = null;
        this.nullable = 0;
        this.caseSensitive = false;
        this.searchable = 0;
        this.unsigned = false;
        this.fixedPrecScale = false;
        this.localTypeName = null;
        this.minimumScale = 0;
        this.maximumScale = 0;
        this.sqlDataType = 0;
        this.sqlDatetimeSub = 0;
        this.numPrecRadix = 0;
        this.normalizedType = TypeInfo.normalizeDataType(dataType, true);
        this.distanceFromJdbcType = this.determineDistanceFromJdbcType();
    }

    public boolean equals(Object o) {
        if (o instanceof TypeInfo) {
            return this.compareTo(o) == 0;
        }
        return false;
    }

    public int hashCode() {
        return this.normalizedType * this.dataType * (this.autoIncrement ? 7 : 11);
    }

    public String toString() {
        return this.typeName + " (" + (this.dataType != this.normalizedType ? this.dataType + "->" : "") + this.normalizedType + ')';
    }

    public void update(ResultSet rs) throws SQLException {
        rs.updateString(1, this.typeName);
        rs.updateInt(2, this.normalizedType);
        rs.updateInt(3, this.precision);
        rs.updateString(4, this.literalPrefix);
        rs.updateString(5, this.literalSuffix);
        rs.updateString(6, this.createParams);
        rs.updateShort(7, this.nullable);
        rs.updateBoolean(8, this.caseSensitive);
        rs.updateShort(9, this.searchable);
        rs.updateBoolean(10, this.unsigned);
        rs.updateBoolean(11, this.fixedPrecScale);
        rs.updateBoolean(12, this.autoIncrement);
        rs.updateString(13, this.localTypeName);
        if (rs.getMetaData().getColumnCount() >= 18) {
            rs.updateShort(14, this.minimumScale);
            rs.updateShort(15, this.maximumScale);
            rs.updateInt(16, this.sqlDataType);
            rs.updateInt(17, this.sqlDatetimeSub);
            rs.updateInt(18, this.numPrecRadix);
        }
    }

    public int compareTo(Object o) {
        TypeInfo other = (TypeInfo)o;
        return this.compare(this.normalizedType, other.normalizedType) * 10 + this.compare(this.distanceFromJdbcType, other.distanceFromJdbcType);
    }

    private int compare(int i1, int i2) {
        return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
    }

    private int determineDistanceFromJdbcType() {
        switch (this.dataType) {
            case 6: 
            case 9: 
            case 10: 
            case 11: {
                return 0;
            }
            case 12: {
                if (this.typeName.equalsIgnoreCase("varchar")) {
                    return 0;
                }
                if (this.typeName.equalsIgnoreCase("nvarchar")) {
                    return 1;
                }
                return 2;
            }
            case -9: {
                return this.typeName.equalsIgnoreCase("sysname") ? 4 : 3;
            }
            case -11: {
                return 9;
            }
            case -150: {
                return 8;
            }
        }
        return this.dataType == this.normalizedType && !this.autoIncrement ? 0 : 5;
    }

    public static int normalizeDataType(int serverDataType, boolean useLOBs) {
        switch (serverDataType) {
            case 35: {
                return 12;
            }
            case 11: {
                return 93;
            }
            case 10: {
                return 92;
            }
            case 9: {
                return 91;
            }
            case 6: {
                return 8;
            }
            case -1: {
                return useLOBs ? 2005 : -1;
            }
            case -4: {
                return useLOBs ? 2004 : -4;
            }
            case -8: {
                return 1;
            }
            case -9: {
                return 12;
            }
            case -10: {
                return useLOBs ? 2005 : -1;
            }
            case -11: {
                return 1;
            }
            case -150: {
                return 12;
            }
        }
        return serverDataType;
    }
}

