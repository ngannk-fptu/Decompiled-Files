/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.DerivedValueSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Formula;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;

public class RelationalObjectBinder {
    private final Database database;
    private final PhysicalNamingStrategy physicalNamingStrategy;

    public RelationalObjectBinder(MetadataBuildingContext buildingContext) {
        this.database = buildingContext.getMetadataCollector().getDatabase();
        this.physicalNamingStrategy = buildingContext.getBuildingOptions().getPhysicalNamingStrategy();
    }

    public void bindColumnOrFormula(MappingDocument sourceDocument, RelationalValueSource relationalValueSource, SimpleValue simpleValue, boolean areColumnsNullableByDefault, ColumnNamingDelegate columnNamingDelegate) {
        this.bindColumnsAndFormulas(sourceDocument, Collections.singletonList(relationalValueSource), simpleValue, areColumnsNullableByDefault, columnNamingDelegate);
    }

    public void bindColumns(MappingDocument sourceDocument, List<ColumnSource> columnSources, SimpleValue simpleValue, boolean areColumnsNullableByDefault, ColumnNamingDelegate columnNamingDelegate) {
        for (ColumnSource columnSource : columnSources) {
            this.bindColumn(sourceDocument, columnSource, simpleValue, areColumnsNullableByDefault, columnNamingDelegate);
        }
    }

    public void bindColumnsAndFormulas(MappingDocument sourceDocument, List<RelationalValueSource> relationalValueSources, SimpleValue simpleValue, boolean areColumnsNullableByDefault, ColumnNamingDelegate columnNamingDelegate) {
        for (RelationalValueSource relationalValueSource : relationalValueSources) {
            if (ColumnSource.class.isInstance(relationalValueSource)) {
                ColumnSource columnSource = (ColumnSource)relationalValueSource;
                this.bindColumn(sourceDocument, columnSource, simpleValue, areColumnsNullableByDefault, columnNamingDelegate);
                continue;
            }
            DerivedValueSource formulaSource = (DerivedValueSource)relationalValueSource;
            simpleValue.addFormula(new Formula(formulaSource.getExpression()));
        }
    }

    public void bindColumn(MappingDocument sourceDocument, ColumnSource columnSource, SimpleValue simpleValue, boolean areColumnsNullableByDefault, ColumnNamingDelegate columnNamingDelegate) {
        Table table = simpleValue.getTable();
        Column column = new Column();
        column.setValue(simpleValue);
        Identifier logicalName = StringHelper.isNotEmpty(columnSource.getName()) ? this.database.toIdentifier(columnSource.getName()) : columnNamingDelegate.determineImplicitName(sourceDocument);
        Identifier physicalName = this.physicalNamingStrategy.toPhysicalColumnName(logicalName, this.database.getJdbcEnvironment());
        column.setName(physicalName.render(this.database.getDialect()));
        if (table != null) {
            table.addColumn(column);
            sourceDocument.getMetadataCollector().addColumnNameBinding(table, logicalName, column);
        }
        if (columnSource.getSizeSource() != null) {
            if (columnSource.getSizeSource().getLength() != null) {
                column.setLength(columnSource.getSizeSource().getLength());
            } else {
                column.setLength(255);
            }
            if (columnSource.getSizeSource().getScale() != null) {
                column.setScale(columnSource.getSizeSource().getScale());
            } else {
                column.setScale(2);
            }
            if (columnSource.getSizeSource().getPrecision() != null) {
                column.setPrecision(columnSource.getSizeSource().getPrecision());
            } else {
                column.setPrecision(19);
            }
        }
        column.setNullable(RelationalObjectBinder.interpretNullability(columnSource.isNullable(), areColumnsNullableByDefault));
        column.setUnique(columnSource.isUnique());
        column.setCheckConstraint(columnSource.getCheckCondition());
        column.setDefaultValue(columnSource.getDefaultValue());
        column.setSqlType(columnSource.getSqlType());
        column.setComment(columnSource.getComment());
        column.setCustomRead(columnSource.getReadFragment());
        column.setCustomWrite(columnSource.getWriteFragment());
        simpleValue.addColumn(column);
        if (table != null) {
            for (String name : columnSource.getIndexConstraintNames()) {
                table.getOrCreateIndex(name).addColumn(column);
            }
            for (String name : columnSource.getUniqueKeyConstraintNames()) {
                table.getOrCreateUniqueKey(name).addColumn(column);
            }
        }
    }

    private static boolean interpretNullability(TruthValue nullable, boolean areColumnsNullableByDefault) {
        if (nullable == null || nullable == TruthValue.UNKNOWN) {
            return areColumnsNullableByDefault;
        }
        return nullable == TruthValue.TRUE;
    }

    public void bindFormulas(MappingDocument sourceDocument, List<DerivedValueSource> formulaSources, OneToOne oneToOneBinding) {
        for (DerivedValueSource formulaSource : formulaSources) {
            oneToOneBinding.addFormula(new Formula(formulaSource.getExpression()));
        }
    }

    public static interface ColumnNamingDelegate {
        public Identifier determineImplicitName(LocalMetadataBuildingContext var1);
    }
}

