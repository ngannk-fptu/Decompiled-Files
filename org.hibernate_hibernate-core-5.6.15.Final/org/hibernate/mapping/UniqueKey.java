/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;

public class UniqueKey
extends Constraint {
    private Map<Column, String> columnOrderMap = new HashMap<Column, String>();

    @Override
    public String sqlConstraintString(SqlStringGenerationContext context, String constraintName, String defaultCatalog, String defaultSchema) {
        return "";
    }

    @Override
    public String sqlCreateString(Mapping p, SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        return null;
    }

    @Override
    public String sqlDropString(SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        return null;
    }

    public void addColumn(Column column, String order) {
        this.addColumn(column);
        if (StringHelper.isNotEmpty(order)) {
            this.columnOrderMap.put(column, order);
        }
    }

    public Map<Column, String> getColumnOrderMap() {
        return this.columnOrderMap;
    }

    @Override
    public String generatedConstraintNamePrefix() {
        return "UK_";
    }

    @Override
    public String getExportIdentifier() {
        return StringHelper.qualify(this.getTable().getExportIdentifier(), "UK-" + this.getName());
    }
}

