/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.newexport;

import java.util.Collections;
import java.util.List;
import lombok.Generated;

public class Query {
    public final String sql;
    public final String tableName;
    public final String exportName;
    public final List<String> userkeyColums;
    public final boolean preserveIdentifierCase;

    public Query(String sql) {
        this(sql, null, null);
    }

    public Query(String sql, String tableName, String exportName) {
        this(sql, tableName, exportName, Collections.emptyList(), false);
    }

    public Query(String sql, String tableName, String exportName, boolean preserveIdentifierCase) {
        this(sql, tableName, exportName, Collections.emptyList(), preserveIdentifierCase);
    }

    public Query(String sql, String tableName, String exportName, List<String> userkeyColums) {
        this(sql, tableName, exportName, userkeyColums, false);
    }

    public Query(String sql, String tableName, String exportName, List<String> userkeyColums, boolean preserveIdentifierCase) {
        this.sql = sql;
        this.tableName = tableName;
        this.exportName = exportName;
        this.userkeyColums = userkeyColums;
        this.preserveIdentifierCase = preserveIdentifierCase;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Query)) {
            return false;
        }
        Query other = (Query)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$sql = this.sql;
        String other$sql = other.sql;
        if (this$sql == null ? other$sql != null : !this$sql.equals(other$sql)) {
            return false;
        }
        String this$tableName = this.tableName;
        String other$tableName = other.tableName;
        if (this$tableName == null ? other$tableName != null : !this$tableName.equals(other$tableName)) {
            return false;
        }
        String this$exportName = this.exportName;
        String other$exportName = other.exportName;
        if (this$exportName == null ? other$exportName != null : !this$exportName.equals(other$exportName)) {
            return false;
        }
        List<String> this$userkeyColums = this.userkeyColums;
        List<String> other$userkeyColums = other.userkeyColums;
        if (this$userkeyColums == null ? other$userkeyColums != null : !((Object)this$userkeyColums).equals(other$userkeyColums)) {
            return false;
        }
        return this.preserveIdentifierCase == other.preserveIdentifierCase;
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof Query;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $sql = this.sql;
        result = result * 59 + ($sql == null ? 43 : $sql.hashCode());
        String $tableName = this.tableName;
        result = result * 59 + ($tableName == null ? 43 : $tableName.hashCode());
        String $exportName = this.exportName;
        result = result * 59 + ($exportName == null ? 43 : $exportName.hashCode());
        List<String> $userkeyColums = this.userkeyColums;
        result = result * 59 + ($userkeyColums == null ? 43 : ((Object)$userkeyColums).hashCode());
        result = result * 59 + (this.preserveIdentifierCase ? 79 : 97);
        return result;
    }

    @Generated
    public String toString() {
        return "Query(sql=" + this.sql + ", tableName=" + this.tableName + ", exportName=" + this.exportName + ", userkeyColums=" + this.userkeyColums + ", preserveIdentifierCase=" + this.preserveIdentifierCase + ")";
    }
}

