/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.Column;
import java.lang.annotation.Annotation;

public class ColumnImpl
implements Column {
    private final String column;

    public ColumnImpl(String column) {
        this.column = column;
    }

    @Override
    public String value() {
        return this.column;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Column.class;
    }
}

