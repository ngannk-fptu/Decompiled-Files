/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.ForeignKeyContributingSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.engine.FetchStyle;

public interface SecondaryTableSource
extends ForeignKeyContributingSource {
    public TableSpecificationSource getTableSource();

    public List<ColumnSource> getPrimaryKeyColumnSources();

    public String getLogicalTableNameForContainedColumns();

    public String getComment();

    public FetchStyle getFetchStyle();

    public boolean isInverse();

    public boolean isOptional();

    @Override
    public boolean isCascadeDeleteEnabled();

    public CustomSql getCustomSqlInsert();

    public CustomSql getCustomSqlUpdate();

    public CustomSql getCustomSqlDelete();
}

