/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Query;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplatedQuery {
    public final String defaultQuery;
    public final String tableName;
    public final String exportName;
    public final List<String> userkeyColums;
    public final boolean preserveIdentifierCase;
    public final Map<DbType, String> dbSpecificQuery;

    public TemplatedQuery(String defaultQuery) {
        this(defaultQuery, null, null);
    }

    public TemplatedQuery(String defaultQuery, String tableName, String exportName) {
        this(defaultQuery, tableName, exportName, Collections.emptyList(), false);
    }

    public TemplatedQuery(String defaultQuery, String tableName, String exportName, boolean preserveIdentifierCase) {
        this(defaultQuery, tableName, exportName, Collections.emptyList(), preserveIdentifierCase);
    }

    public TemplatedQuery(String defaultQuery, String tableName, String exportName, List<String> userkeyColums) {
        this(defaultQuery, tableName, exportName, userkeyColums, false);
    }

    public TemplatedQuery(String defaultQuery, String tableName, String exportName, List<String> userkeyColums, boolean preserveIdentifierCase) {
        this(defaultQuery, tableName, exportName, userkeyColums, preserveIdentifierCase, new HashMap<DbType, String>());
    }

    public TemplatedQuery(String defaultQuery, Map<DbType, String> dbSpecificQuery) {
        this(defaultQuery, null, null, Collections.emptyList(), false, dbSpecificQuery);
    }

    public TemplatedQuery(String defaultQuery, String tableName, String exportName, List<String> userkeyColums, boolean preserveIdentifierCase, Map<DbType, String> dbSpecificQuery) {
        this.defaultQuery = defaultQuery;
        this.tableName = tableName;
        this.exportName = exportName;
        this.userkeyColums = userkeyColums;
        this.preserveIdentifierCase = preserveIdentifierCase;
        this.dbSpecificQuery = dbSpecificQuery;
    }

    public Query toQuery(DbType dbType, String ... templateArgs) {
        String query = this.dbSpecificQuery.getOrDefault((Object)dbType, this.defaultQuery);
        return new Query(String.format(query, templateArgs), this.tableName, this.exportName, this.userkeyColums, this.preserveIdentifierCase);
    }

    public String query(DbType dbType) {
        return this.dbSpecificQuery.getOrDefault((Object)dbType, this.defaultQuery);
    }

    public String query() {
        return this.defaultQuery;
    }
}

