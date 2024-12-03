/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 */
package org.hibernate.cfg.annotations;

import java.lang.annotation.Annotation;
import javax.persistence.Column;
import org.hibernate.annotations.Columns;

public class CustomizableColumns
implements Columns {
    private final Column[] columns;

    public CustomizableColumns(Column[] columns) {
        this.columns = columns;
    }

    @Override
    public Column[] columns() {
        return this.columns;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Columns.class;
    }
}

