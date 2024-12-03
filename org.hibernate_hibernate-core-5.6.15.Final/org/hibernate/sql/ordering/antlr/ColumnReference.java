/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ordering.antlr;

import org.hibernate.sql.ordering.antlr.SqlValueReference;

public interface ColumnReference
extends SqlValueReference {
    public String getColumnName();
}

