/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql.dml;

import com.google.common.collect.Maps;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.Mapper;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractMapper<T>
implements Mapper<T> {
    protected Map<String, Path<?>> getColumns(RelationalPath<?> path) {
        LinkedHashMap columns = Maps.newLinkedHashMap();
        for (Path<?> column : path.getColumns()) {
            columns.put(column.getMetadata().getName(), column);
        }
        return columns;
    }

    protected boolean isPrimaryKeyColumn(RelationalPath<?> parent, Path<?> property) {
        return parent.getPrimaryKey() != null && parent.getPrimaryKey().getLocalColumns().contains(property);
    }
}

