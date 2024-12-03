/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.querydsl.sql;

import com.google.common.base.Objects;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import java.io.Serializable;

public final class ColumnMetadata
implements Serializable {
    private static final long serialVersionUID = -5678865742525938470L;
    private static final int UNDEFINED = -1;
    private final String name;
    private final Integer index;
    private final Integer jdbcType;
    private final boolean nullable;
    private final int size;
    private final int decimalDigits;

    public static ColumnMetadata getColumnMetadata(Path<?> path) {
        Object columnMetadata;
        Path<?> parent = path.getMetadata().getParent();
        if (parent instanceof EntityPath && (columnMetadata = ((EntityPath)parent).getMetadata(path)) instanceof ColumnMetadata) {
            return (ColumnMetadata)columnMetadata;
        }
        return ColumnMetadata.named(path.getMetadata().getName());
    }

    public static String getName(Path<?> path) {
        Object columnMetadata;
        Path<?> parent = path.getMetadata().getParent();
        if (parent instanceof EntityPath && (columnMetadata = ((EntityPath)parent).getMetadata(path)) instanceof ColumnMetadata) {
            return ((ColumnMetadata)columnMetadata).getName();
        }
        return path.getMetadata().getName();
    }

    public static ColumnMetadata named(String name) {
        return new ColumnMetadata(null, name, null, true, -1, -1);
    }

    private ColumnMetadata(Integer index, String name, Integer jdbcType, boolean nullable, int size, int decimalDigits) {
        this.index = index;
        this.name = name;
        this.jdbcType = jdbcType;
        this.nullable = nullable;
        this.size = size;
        this.decimalDigits = decimalDigits;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public ColumnMetadata withIndex(int index) {
        return new ColumnMetadata(index, this.name, this.jdbcType, this.nullable, this.size, this.decimalDigits);
    }

    public int getJdbcType() {
        return this.jdbcType;
    }

    public boolean hasJdbcType() {
        return this.jdbcType != null;
    }

    public ColumnMetadata ofType(int jdbcType) {
        return new ColumnMetadata(this.index, this.name, jdbcType, this.nullable, this.size, this.decimalDigits);
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public ColumnMetadata notNull() {
        return new ColumnMetadata(this.index, this.name, this.jdbcType, false, this.size, this.decimalDigits);
    }

    public int getSize() {
        return this.size;
    }

    public boolean hasSize() {
        return this.size != -1;
    }

    public ColumnMetadata withSize(int size) {
        return new ColumnMetadata(this.index, this.name, this.jdbcType, this.nullable, size, this.decimalDigits);
    }

    public int getDigits() {
        return this.decimalDigits;
    }

    public boolean hasDigits() {
        return this.decimalDigits != -1;
    }

    public ColumnMetadata withDigits(int decimalDigits) {
        return new ColumnMetadata(this.index, this.name, this.jdbcType, this.nullable, this.size, decimalDigits);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ColumnMetadata) {
            ColumnMetadata md = (ColumnMetadata)o;
            return this.name.equals(md.name) && Objects.equal((Object)this.jdbcType, (Object)md.jdbcType) && this.nullable == md.nullable && this.size == md.size && this.decimalDigits == md.decimalDigits;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

