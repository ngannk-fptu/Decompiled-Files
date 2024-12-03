/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import net.java.ao.types.TypeInfo;

public class DDLField {
    private String name;
    private TypeInfo<?> type;
    private int jdbcType;
    private boolean primaryKey;
    private boolean autoIncrement;
    private boolean notNull;
    private boolean unique;
    private Object defaultValue;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeInfo<?> getType() {
        return this.type;
    }

    public void setType(TypeInfo<?> type) {
        this.type = type;
    }

    public int getJdbcType() {
        return this.jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String toString() {
        return this.getName();
    }

    public int hashCode() {
        int back = this.type.hashCode();
        if (this.name != null) {
            back += this.name.hashCode();
        }
        return back;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DDLField) {
            DDLField field = (DDLField)obj;
            if (field == this) {
                return true;
            }
            return (field.getName() == null || field.getName().equals(this.name)) && field.getType() == this.type;
        }
        return super.equals(obj);
    }
}

