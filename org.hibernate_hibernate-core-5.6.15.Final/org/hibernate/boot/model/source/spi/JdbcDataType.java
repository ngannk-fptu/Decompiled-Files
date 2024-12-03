/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public class JdbcDataType {
    private final int typeCode;
    private final String typeName;
    private final Class javaType;
    private final int hashCode;

    public JdbcDataType(int typeCode, String typeName, Class javaType) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.javaType = javaType;
        int result = typeCode;
        if (typeName != null) {
            result = 31 * result + typeName.hashCode();
        }
        if (javaType != null) {
            result = 31 * result + javaType.hashCode();
        }
        this.hashCode = result;
    }

    public int getTypeCode() {
        return this.typeCode;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public Class getJavaType() {
        return this.javaType;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JdbcDataType jdbcDataType = (JdbcDataType)o;
        return this.typeCode == jdbcDataType.typeCode && this.javaType.equals(jdbcDataType.javaType) && this.typeName.equals(jdbcDataType.typeName);
    }

    public String toString() {
        return super.toString() + "[code=" + this.typeCode + ", name=" + this.typeName + ", javaClass=" + this.javaType.getName() + "]";
    }
}

