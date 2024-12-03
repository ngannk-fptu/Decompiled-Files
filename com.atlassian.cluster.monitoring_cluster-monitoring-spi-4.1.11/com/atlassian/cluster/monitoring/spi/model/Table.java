/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import com.atlassian.cluster.monitoring.spi.model.Description;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Table<ColumnId extends String, ColumnName extends String, RowId extends String, CellValue extends String, Row extends List<CellValue>>
implements Serializable {
    @JsonProperty
    private final Map<ColumnId, ColumnName> columns;
    @JsonProperty
    private final Map<RowId, Row> rows;
    @Nullable
    @JsonProperty
    private final Description description;
    @JsonProperty
    private final boolean sortAutomatically;

    public Table(Map<ColumnId, ColumnName> columns, Map<RowId, Row> rows, Description description, boolean sortAutomatically) {
        this.columns = ImmutableMap.copyOf(Objects.requireNonNull(columns));
        this.rows = ImmutableMap.copyOf(Objects.requireNonNull(rows));
        this.description = description;
        this.sortAutomatically = sortAutomatically;
    }

    public Table(Map<ColumnId, ColumnName> columns, Map<RowId, Row> rows) {
        this(columns, rows, null, true);
    }

    public Table(Map<ColumnId, ColumnName> columns, Map<RowId, Row> rows, Description description) {
        this(columns, rows, description, true);
    }

    public Table(Map<ColumnId, ColumnName> columns, Map<RowId, Row> rows, boolean sortAutomatically) {
        this(columns, rows, null, sortAutomatically);
    }

    public Map<ColumnId, ColumnName> getColumns() {
        return this.columns;
    }

    public Map<RowId, Row> getRows() {
        return this.rows;
    }

    public Description getDescription() {
        return this.description;
    }

    public boolean isSortAutomatically() {
        return this.sortAutomatically;
    }
}

