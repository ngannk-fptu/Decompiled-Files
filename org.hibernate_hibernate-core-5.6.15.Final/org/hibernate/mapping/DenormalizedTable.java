/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public class DenormalizedTable
extends Table {
    private final Table includedTable;

    public DenormalizedTable(Table includedTable) {
        this.includedTable = includedTable;
        includedTable.setHasDenormalizedTables();
    }

    public DenormalizedTable(Namespace namespace, Identifier physicalTableName, boolean isAbstract, Table includedTable) {
        super(namespace, physicalTableName, isAbstract);
        this.includedTable = includedTable;
        includedTable.setHasDenormalizedTables();
    }

    public DenormalizedTable(Namespace namespace, Identifier physicalTableName, String subselectFragment, boolean isAbstract, Table includedTable) {
        super(namespace, physicalTableName, subselectFragment, isAbstract);
        this.includedTable = includedTable;
        includedTable.setHasDenormalizedTables();
    }

    public DenormalizedTable(Namespace namespace, String subselect, boolean isAbstract, Table includedTable) {
        super(namespace, subselect, isAbstract);
        this.includedTable = includedTable;
        includedTable.setHasDenormalizedTables();
    }

    @Override
    public void createForeignKeys() {
        this.includedTable.createForeignKeys();
        Iterator<ForeignKey> iter = this.includedTable.getForeignKeyIterator();
        while (iter.hasNext()) {
            ForeignKey fk = iter.next();
            this.createForeignKey(Constraint.generateName(fk.generatedConstraintNamePrefix(), (Table)this, fk.getColumns()), fk.getColumns(), fk.getReferencedEntityName(), fk.getKeyDefinition(), fk.getReferencedColumns());
        }
    }

    @Override
    public Column getColumn(Column column) {
        Column superColumn = super.getColumn(column);
        if (superColumn != null) {
            return superColumn;
        }
        return this.includedTable.getColumn(column);
    }

    @Override
    public Column getColumn(Identifier name) {
        Column superColumn = super.getColumn(name);
        if (superColumn != null) {
            return superColumn;
        }
        return this.includedTable.getColumn(name);
    }

    public Iterator getColumnIterator() {
        return new JoinedIterator(this.includedTable.getColumnIterator(), super.getColumnIterator());
    }

    @Override
    public boolean containsColumn(Column column) {
        return super.containsColumn(column) || this.includedTable.containsColumn(column);
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return this.includedTable.getPrimaryKey();
    }

    public Iterator getUniqueKeyIterator() {
        if (!this.includedTable.isPhysicalTable()) {
            Iterator<UniqueKey> iter = this.includedTable.getUniqueKeyIterator();
            while (iter.hasNext()) {
                UniqueKey uk = iter.next();
                this.createUniqueKey(uk.getColumns());
            }
        }
        return this.getUniqueKeys().values().iterator();
    }

    public Iterator getIndexIterator() {
        ArrayList<Index> indexes = new ArrayList<Index>();
        Iterator<Index> iter = this.includedTable.getIndexIterator();
        while (iter.hasNext()) {
            Index parentIndex = iter.next();
            Index index = new Index();
            index.setName(this.getName() + parentIndex.getName());
            index.setTable(this);
            index.addColumns(parentIndex.getColumnIterator());
            indexes.add(index);
        }
        return new JoinedIterator(indexes.iterator(), super.getIndexIterator());
    }

    public Table getIncludedTable() {
        return this.includedTable;
    }
}

