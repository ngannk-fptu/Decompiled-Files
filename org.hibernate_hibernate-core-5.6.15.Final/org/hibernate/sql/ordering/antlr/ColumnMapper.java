/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ordering.antlr;

import org.hibernate.HibernateException;
import org.hibernate.sql.ordering.antlr.SqlValueReference;

public interface ColumnMapper {
    public SqlValueReference[] map(String var1) throws HibernateException;
}

