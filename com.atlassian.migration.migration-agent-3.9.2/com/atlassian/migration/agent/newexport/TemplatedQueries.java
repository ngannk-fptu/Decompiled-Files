/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.TemplatedQuery;
import java.util.Arrays;
import java.util.List;

public class TemplatedQueries {
    private final List<TemplatedQuery> templatedQueriesList;

    public TemplatedQueries(TemplatedQuery ... templatedQueries) {
        this.templatedQueriesList = Arrays.asList(templatedQueries);
    }

    public Query[] toQueries(DbType dbType, String ... templateArgs) {
        return (Query[])this.templatedQueriesList.stream().map(templatedQuery -> templatedQuery.toQuery(dbType, templateArgs)).toArray(Query[]::new);
    }
}

