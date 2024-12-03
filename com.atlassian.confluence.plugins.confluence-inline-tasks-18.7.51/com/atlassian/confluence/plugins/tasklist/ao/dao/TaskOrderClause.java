/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.tasklist.ao.dao;

import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.search.SortOrder;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class TaskOrderClause {
    private static TaskOrderClause CREATE_DATE_DESC = new TaskOrderClause("t.CREATE_DATE", SortOrder.DESCENDING, false);
    private final String dbSortingClause;
    private final String alias;
    private final SortOrder order;
    private final boolean nullable;

    public TaskOrderClause(String dbSortingClause, String alias, SortOrder order, boolean nullable) {
        this.dbSortingClause = dbSortingClause;
        this.alias = alias;
        this.order = order;
        this.nullable = nullable;
    }

    public TaskOrderClause(String dbSortingClause, SortOrder order, boolean nullable) {
        this(dbSortingClause, dbSortingClause, order, nullable);
    }

    public String getDbSortingClause() {
        return this.dbSortingClause;
    }

    public TaskOrderClause getNullLastClause() {
        if (!this.nullable) {
            return null;
        }
        String nullLastAlias = this.dbSortingClause.replace('.', '_').toUpperCase();
        String clause = String.format("case when %s is null then 0 else 1 end %s", this.dbSortingClause, nullLastAlias);
        return new TaskOrderClause(clause, nullLastAlias, SortOrder.DESCENDING, false);
    }

    static List<TaskOrderClause> orderClausesFor(SortColumn column, SortOrder order) {
        TaskOrderClause primaryClause;
        switch (column) {
            case ASSIGNEE: {
                primaryClause = new TaskOrderClause("cu.lower_display_name", order, true);
                break;
            }
            case DUE_DATE: {
                primaryClause = new TaskOrderClause("t.DUE_DATE", order, true);
                break;
            }
            case PAGE_TITLE: {
                primaryClause = new TaskOrderClause("c.TITLE", order, false);
                break;
            }
            default: {
                return ImmutableList.of();
            }
        }
        TaskOrderClause nullLastClause = primaryClause.getNullLastClause();
        return nullLastClause != null ? ImmutableList.of((Object)nullLastClause, (Object)primaryClause, (Object)CREATE_DATE_DESC) : ImmutableList.of((Object)primaryClause, (Object)CREATE_DATE_DESC);
    }

    public String toString() {
        return this.alias + " " + this.order;
    }
}

