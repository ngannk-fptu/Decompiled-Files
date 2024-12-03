/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.tasklist.macro;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ColumnNameMapper {
    public static final Set<String> COLUMNS = ImmutableSet.of((Object)"description", (Object)"duedate", (Object)"assignee", (Object)"location", (Object)"completedate", (Object)"labels", (Object[])new String[0]);
    public static final String DEFAULT_COLUMNS = "description,duedate,assignee,location";
    public static final String REQUIRED_COLUMN = "description";
    private final List<String> columnNames;

    public ColumnNameMapper() {
        this("");
    }

    public ColumnNameMapper(String columns) {
        columns = StringUtils.isBlank((CharSequence)columns) ? DEFAULT_COLUMNS : columns;
        Iterable names = Splitter.on((char)',').trimResults().omitEmptyStrings().split((CharSequence)columns);
        ArrayList collected = Lists.newArrayList((Iterable)names);
        ArrayList<String> invalidColumns = new ArrayList<String>();
        for (String col : collected) {
            if (COLUMNS.contains(col)) continue;
            invalidColumns.add(col);
        }
        if (!invalidColumns.isEmpty()) {
            throw new IllegalArgumentException("Unrecognized column name(s) " + Joiner.on((String)",").join(invalidColumns) + ". Allowed columns are description, duedate, assignee, location, completedate, labels.");
        }
        if (!collected.contains(REQUIRED_COLUMN)) {
            collected.add(0, REQUIRED_COLUMN);
        }
        this.columnNames = ImmutableList.copyOf((Collection)collected);
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }
}

