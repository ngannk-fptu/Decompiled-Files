/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.ResultSet;
import org.hibernate.engine.jdbc.ColumnNameCache;

@Deprecated
public interface ResultSetWrapper {
    public ResultSet wrap(ResultSet var1, ColumnNameCache var2);
}

