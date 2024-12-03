/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public class IndexOrUniqueKeySecondPass
implements SecondPass {
    private Table table;
    private final String indexName;
    private final String[] columns;
    private final MetadataBuildingContext buildingContext;
    private final Ejb3Column column;
    private final boolean unique;

    public IndexOrUniqueKeySecondPass(Table table, String indexName, String[] columns, MetadataBuildingContext buildingContext) {
        this.table = table;
        this.indexName = indexName;
        this.columns = columns;
        this.buildingContext = buildingContext;
        this.column = null;
        this.unique = false;
    }

    public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, MetadataBuildingContext buildingContext) {
        this(indexName, column, buildingContext, false);
    }

    public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, MetadataBuildingContext buildingContext, boolean unique) {
        this.indexName = indexName;
        this.column = column;
        this.columns = null;
        this.buildingContext = buildingContext;
        this.unique = unique;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        if (this.columns != null) {
            for (String column1 : this.columns) {
                this.addConstraintToColumn(column1);
            }
        }
        if (this.column != null) {
            this.table = this.column.getTable();
            PropertyHolder propertyHolder = this.column.getPropertyHolder();
            String entityName = propertyHolder.isComponent() ? propertyHolder.getPersistentClass().getEntityName() : propertyHolder.getEntityName();
            PersistentClass persistentClass = (PersistentClass)persistentClasses.get(entityName);
            Property property = persistentClass.getProperty(this.column.getPropertyName());
            if (property.getValue() instanceof Component) {
                Component component = (Component)property.getValue();
                ArrayList<Column> columns = new ArrayList<Column>();
                component.getColumnIterator().forEachRemaining(selectable -> {
                    if (selectable instanceof Column) {
                        columns.add((Column)selectable);
                    }
                });
                this.addConstraintToColumns(columns);
            } else {
                this.addConstraintToColumn(this.buildingContext.getMetadataCollector().getLogicalColumnName(this.table, this.column.getMappingColumn().getQuotedName()));
            }
        }
    }

    private void addConstraintToColumn(String columnName) {
        Column column = this.table.getColumn(new Column(this.buildingContext.getMetadataCollector().getPhysicalColumnName(this.table, columnName)));
        if (column == null) {
            throw new AnnotationException("@Index references a unknown column: " + columnName);
        }
        if (this.unique) {
            this.table.getOrCreateUniqueKey(this.indexName).addColumn(column);
        } else {
            this.table.getOrCreateIndex(this.indexName).addColumn(column);
        }
    }

    private void addConstraintToColumns(List<Column> columns) {
        if (this.unique) {
            UniqueKey uniqueKey = this.table.getOrCreateUniqueKey(this.indexName);
            for (Column column : columns) {
                uniqueKey.addColumn(column);
            }
        } else {
            Index index = this.table.getOrCreateIndex(this.indexName);
            for (Column column : columns) {
                index.addColumn(column);
            }
        }
    }
}

