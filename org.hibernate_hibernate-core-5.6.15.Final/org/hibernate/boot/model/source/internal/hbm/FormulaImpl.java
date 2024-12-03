/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.DerivedValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

class FormulaImpl
extends AbstractHbmSourceNode
implements DerivedValueSource {
    private String tableName;
    private final String expression;

    FormulaImpl(MappingDocument mappingDocument, String tableName, String expression) {
        super(mappingDocument);
        this.tableName = tableName;
        this.expression = expression;
    }

    @Override
    public RelationalValueSource.Nature getNature() {
        return RelationalValueSource.Nature.DERIVED;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public String getContainingTableName() {
        return this.tableName;
    }
}

