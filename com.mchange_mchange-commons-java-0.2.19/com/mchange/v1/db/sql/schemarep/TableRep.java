/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.ColumnRep;
import java.util.Iterator;
import java.util.Set;

public interface TableRep {
    public String getTableName();

    public Iterator getColumnNames();

    public ColumnRep columnRepForName(String var1);

    public Set getPrimaryKeyColumnNames();

    public Set getForeignKeyReps();

    public Set getUniquenessConstraintReps();
}

