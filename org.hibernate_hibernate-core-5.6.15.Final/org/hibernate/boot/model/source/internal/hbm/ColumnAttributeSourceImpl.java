/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Set;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.JdbcDataType;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;

class ColumnAttributeSourceImpl
extends AbstractHbmSourceNode
implements ColumnSource {
    private final String tableName;
    private final String columnName;
    private final SizeSource sizeSource;
    private final TruthValue nullable;
    private final TruthValue unique;
    private final Set<String> indexConstraintNames;
    private final Set<String> ukConstraintNames;

    ColumnAttributeSourceImpl(MappingDocument mappingDocument, String tableName, String columnName, SizeSource sizeSource, TruthValue nullable, TruthValue unique, Set<String> indexConstraintNames, Set<String> ukConstraintNames) {
        super(mappingDocument);
        this.tableName = tableName;
        this.columnName = columnName;
        this.sizeSource = sizeSource;
        this.nullable = nullable;
        this.unique = unique;
        this.indexConstraintNames = indexConstraintNames;
        this.ukConstraintNames = ukConstraintNames;
    }

    @Override
    public RelationalValueSource.Nature getNature() {
        return RelationalValueSource.Nature.COLUMN;
    }

    @Override
    public String getContainingTableName() {
        return this.tableName;
    }

    @Override
    public String getName() {
        return this.columnName;
    }

    @Override
    public TruthValue isNullable() {
        return this.nullable;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public String getSqlType() {
        return null;
    }

    @Override
    public JdbcDataType getDatatype() {
        return null;
    }

    @Override
    public SizeSource getSizeSource() {
        return this.sizeSource;
    }

    @Override
    public String getReadFragment() {
        return null;
    }

    @Override
    public String getWriteFragment() {
        return null;
    }

    @Override
    public boolean isUnique() {
        return this.unique == TruthValue.TRUE;
    }

    @Override
    public String getCheckCondition() {
        return null;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public Set<String> getIndexConstraintNames() {
        return this.indexConstraintNames;
    }

    @Override
    public Set<String> getUniqueKeyConstraintNames() {
        return this.ukConstraintNames;
    }
}

