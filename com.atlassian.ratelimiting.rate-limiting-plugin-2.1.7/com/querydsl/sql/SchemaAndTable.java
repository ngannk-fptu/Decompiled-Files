/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.querydsl.sql;

import com.google.common.base.Objects;
import java.io.Serializable;

public class SchemaAndTable
implements Serializable {
    private final String schema;
    private final String table;

    public SchemaAndTable(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getTable() {
        return this.table;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SchemaAndTable) {
            SchemaAndTable st = (SchemaAndTable)o;
            return Objects.equal((Object)st.schema, (Object)this.schema) && Objects.equal((Object)st.table, (Object)this.table);
        }
        return false;
    }

    public int hashCode() {
        return (this.schema != null ? 31 * this.schema.hashCode() : 0) + this.table.hashCode();
    }

    public String toString() {
        return "(" + this.schema + " " + this.table + ")";
    }
}

