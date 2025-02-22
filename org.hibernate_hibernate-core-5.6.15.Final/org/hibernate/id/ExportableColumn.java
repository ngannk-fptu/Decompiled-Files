/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;

public class ExportableColumn
extends Column {
    public ExportableColumn(Database database, Table table, String name, BasicType type) {
        this(database, table, name, type, database.getDialect().getTypeName(type.sqlTypes(null)[0]));
    }

    public ExportableColumn(Database database, Table table, String name, BasicType type, String dbTypeDeclaration) {
        super(name);
        this.setValue(new ValueImpl(this, table, type, database));
        this.setSqlType(dbTypeDeclaration);
    }

    public static class ColumnIterator
    implements Iterator<Selectable> {
        private final ExportableColumn column;
        private int count = 0;

        public ColumnIterator(ExportableColumn column) {
            this.column = column;
        }

        @Override
        public boolean hasNext() {
            return this.count == 0;
        }

        @Override
        public ExportableColumn next() {
            if (this.count > 0) {
                throw new NoSuchElementException("The single element has already been read");
            }
            ++this.count;
            return this.column;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove");
        }
    }

    public static class ValueImpl
    implements Value {
        private final ExportableColumn column;
        private final Table table;
        private final BasicType type;
        private final Database database;

        public ValueImpl(ExportableColumn column, Table table, BasicType type, Database database) {
            this.column = column;
            this.table = table;
            this.type = type;
            this.database = database;
        }

        @Override
        public int getColumnSpan() {
            return 1;
        }

        @Override
        public Iterator<Selectable> getColumnIterator() {
            return new ColumnIterator(this.column);
        }

        @Override
        public Type getType() throws MappingException {
            return this.type;
        }

        @Override
        public FetchMode getFetchMode() {
            return null;
        }

        @Override
        public Table getTable() {
            return this.table;
        }

        @Override
        public boolean hasFormula() {
            return false;
        }

        @Override
        public boolean isAlternateUniqueKey() {
            return false;
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public boolean[] getColumnUpdateability() {
            return new boolean[]{true};
        }

        @Override
        public boolean[] getColumnInsertability() {
            return new boolean[]{true};
        }

        @Override
        public void createForeignKey() throws MappingException {
        }

        @Override
        public boolean isSimpleValue() {
            return true;
        }

        @Override
        public boolean isValid(Mapping mapping) throws MappingException {
            return false;
        }

        @Override
        public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
        }

        @Override
        public Object accept(ValueVisitor visitor) {
            return null;
        }

        @Override
        public boolean isSame(Value value) {
            return false;
        }

        @Override
        public ServiceRegistry getServiceRegistry() {
            return this.database.getServiceRegistry();
        }
    }
}

