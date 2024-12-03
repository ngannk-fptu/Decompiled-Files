/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.SchemaAndTable;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public interface RelationalPath<T>
extends EntityPath<T>,
ProjectionRole<T> {
    public SchemaAndTable getSchemaAndTable();

    public String getSchemaName();

    public String getTableName();

    public List<Path<?>> getColumns();

    @Nullable
    public PrimaryKey<T> getPrimaryKey();

    public Collection<ForeignKey<?>> getForeignKeys();

    public Collection<ForeignKey<?>> getInverseForeignKeys();

    @Override
    @Nullable
    public ColumnMetadata getMetadata(Path<?> var1);
}

