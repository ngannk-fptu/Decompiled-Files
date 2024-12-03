/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import java.util.List;

public interface ForeignKeyRep {
    public List getLocalColumnNames();

    public String getReferencedTableName();

    public List getReferencedColumnNames();
}

