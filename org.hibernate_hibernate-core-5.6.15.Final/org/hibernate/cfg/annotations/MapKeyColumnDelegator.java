/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.MapKeyColumn
 */
package org.hibernate.cfg.annotations;

import java.lang.annotation.Annotation;
import javax.persistence.Column;
import javax.persistence.MapKeyColumn;

public class MapKeyColumnDelegator
implements Column {
    private final MapKeyColumn column;

    public MapKeyColumnDelegator(MapKeyColumn column) {
        this.column = column;
    }

    public String name() {
        return this.column.name();
    }

    public boolean unique() {
        return this.column.unique();
    }

    public boolean nullable() {
        return this.column.nullable();
    }

    public boolean insertable() {
        return this.column.insertable();
    }

    public boolean updatable() {
        return this.column.updatable();
    }

    public String columnDefinition() {
        return this.column.columnDefinition();
    }

    public String table() {
        return this.column.table();
    }

    public int length() {
        return this.column.length();
    }

    public int precision() {
        return this.column.precision();
    }

    public int scale() {
        return this.column.scale();
    }

    public Class<? extends Annotation> annotationType() {
        return Column.class;
    }
}

