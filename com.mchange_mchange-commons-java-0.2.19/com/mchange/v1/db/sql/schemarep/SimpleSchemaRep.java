/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.v1.db.sql.schemarep.TableRep;
import java.util.Set;

public interface SimpleSchemaRep {
    public Set getTableNames();

    public TableRep tableRepForName(String var1);
}

