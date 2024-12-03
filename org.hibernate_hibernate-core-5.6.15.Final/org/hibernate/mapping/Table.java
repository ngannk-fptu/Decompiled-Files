/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.InitCommand;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.RelationalModel;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.TableMetadata;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.jboss.logging.Logger;

public class Table
implements RelationalModel,
Serializable,
Exportable {
    private static final Logger log = Logger.getLogger(Table.class);
    private static final Column[] EMPTY_COLUMN_ARRAY = new Column[0];
    private Identifier catalog;
    private Identifier schema;
    private Identifier name;
    private Map<String, Column> columns = new LinkedHashMap<String, Column>();
    private KeyValue idValue;
    private PrimaryKey primaryKey;
    private Map<ForeignKeyKey, ForeignKey> foreignKeys = new LinkedHashMap<ForeignKeyKey, ForeignKey>();
    private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
    private Map<String, UniqueKey> uniqueKeys = new LinkedHashMap<String, UniqueKey>();
    private int uniqueInteger;
    private List<String> checkConstraints = new ArrayList<String>();
    private String rowId;
    private String subselect;
    private boolean isAbstract;
    private boolean hasDenormalizedTables;
    private String comment;
    private List<Function<SqlStringGenerationContext, InitCommand>> initCommandProducers;
    private int sizeOfUniqueKeyMapOnLastCleanse;

    public Table() {
    }

    public Table(String name) {
        this.setName(name);
    }

    public Table(Namespace namespace, Identifier physicalTableName, boolean isAbstract) {
        this.catalog = namespace.getPhysicalName().getCatalog();
        this.schema = namespace.getPhysicalName().getSchema();
        this.name = physicalTableName;
        this.isAbstract = isAbstract;
    }

    public Table(Identifier catalog, Identifier schema, Identifier physicalTableName, boolean isAbstract) {
        this.catalog = catalog;
        this.schema = schema;
        this.name = physicalTableName;
        this.isAbstract = isAbstract;
    }

    public Table(Namespace namespace, Identifier physicalTableName, String subselect, boolean isAbstract) {
        this.catalog = namespace.getPhysicalName().getCatalog();
        this.schema = namespace.getPhysicalName().getSchema();
        this.name = physicalTableName;
        this.subselect = subselect;
        this.isAbstract = isAbstract;
    }

    public Table(Namespace namespace, String subselect, boolean isAbstract) {
        this.catalog = namespace.getPhysicalName().getCatalog();
        this.schema = namespace.getPhysicalName().getSchema();
        this.subselect = subselect;
        this.isAbstract = isAbstract;
    }

    public String getQualifiedName(SqlStringGenerationContext context) {
        if (this.subselect != null) {
            return "( " + this.subselect + " )";
        }
        return context.format(new QualifiedTableName(this.catalog, this.schema, this.name));
    }

    @Deprecated
    public static String qualify(String catalog, String schema, String table) {
        StringBuilder qualifiedName = new StringBuilder();
        if (catalog != null) {
            qualifiedName.append(catalog).append('.');
        }
        if (schema != null) {
            qualifiedName.append(schema).append('.');
        }
        return qualifiedName.append(table).toString();
    }

    public void setName(String name) {
        this.name = Identifier.toIdentifier(name);
    }

    public String getName() {
        return this.name == null ? null : this.name.getText();
    }

    public Identifier getNameIdentifier() {
        return this.name;
    }

    public String getQuotedName() {
        return this.name == null ? null : this.name.toString();
    }

    public String getQuotedName(Dialect dialect) {
        return this.name == null ? null : this.name.render(dialect);
    }

    public QualifiedTableName getQualifiedTableName() {
        return this.name == null ? null : new QualifiedTableName(this.catalog, this.schema, this.name);
    }

    public boolean isQuoted() {
        return this.name.isQuoted();
    }

    public void setQuoted(boolean quoted) {
        if (quoted == this.name.isQuoted()) {
            return;
        }
        this.name = new Identifier(this.name.getText(), quoted);
    }

    public void setSchema(String schema) {
        this.schema = Identifier.toIdentifier(schema);
    }

    public String getSchema() {
        return this.schema == null ? null : this.schema.getText();
    }

    public String getQuotedSchema() {
        return this.schema == null ? null : this.schema.toString();
    }

    public String getQuotedSchema(Dialect dialect) {
        return this.schema == null ? null : this.schema.render(dialect);
    }

    public boolean isSchemaQuoted() {
        return this.schema != null && this.schema.isQuoted();
    }

    public void setCatalog(String catalog) {
        this.catalog = Identifier.toIdentifier(catalog);
    }

    public String getCatalog() {
        return this.catalog == null ? null : this.catalog.getText();
    }

    public String getQuotedCatalog() {
        return this.catalog == null ? null : this.catalog.render();
    }

    public String getQuotedCatalog(Dialect dialect) {
        return this.catalog == null ? null : this.catalog.render(dialect);
    }

    public boolean isCatalogQuoted() {
        return this.catalog != null && this.catalog.isQuoted();
    }

    public Column getColumn(Column column) {
        if (column == null) {
            return null;
        }
        Column myColumn = this.columns.get(column.getCanonicalName());
        return column.equals(myColumn) ? myColumn : null;
    }

    public Column getColumn(Identifier name) {
        if (name == null) {
            return null;
        }
        return this.columns.get(name.getCanonicalName());
    }

    public Column getColumn(int n) {
        Iterator<Column> iter = this.columns.values().iterator();
        for (int i = 0; i < n - 1; ++i) {
            iter.next();
        }
        return iter.next();
    }

    public void addColumn(Column column) {
        Column old = this.getColumn(column);
        if (old == null) {
            if (this.primaryKey != null) {
                for (Column c : this.primaryKey.getColumns()) {
                    if (!c.getCanonicalName().equals(column.getCanonicalName())) continue;
                    column.setNullable(false);
                    log.debugf("Forcing column [%s] to be non-null as it is part of the primary key for table [%s]", (Object)column.getCanonicalName(), (Object)this.getNameIdentifier().getCanonicalName());
                }
            }
            this.columns.put(column.getCanonicalName(), column);
            column.uniqueInteger = this.columns.size();
        } else {
            column.uniqueInteger = old.uniqueInteger;
        }
    }

    public int getColumnSpan() {
        return this.columns.size();
    }

    public Iterator<Column> getColumnIterator() {
        return this.columns.values().iterator();
    }

    public Iterator<Index> getIndexIterator() {
        return this.indexes.values().iterator();
    }

    public Iterator<ForeignKey> getForeignKeyIterator() {
        return this.foreignKeys.values().iterator();
    }

    public Map<ForeignKeyKey, ForeignKey> getForeignKeys() {
        return Collections.unmodifiableMap(this.foreignKeys);
    }

    public Iterator<UniqueKey> getUniqueKeyIterator() {
        return this.getUniqueKeys().values().iterator();
    }

    Map<String, UniqueKey> getUniqueKeys() {
        this.cleanseUniqueKeyMapIfNeeded();
        return this.uniqueKeys;
    }

    private void cleanseUniqueKeyMapIfNeeded() {
        if (this.uniqueKeys.size() == this.sizeOfUniqueKeyMapOnLastCleanse) {
            return;
        }
        this.cleanseUniqueKeyMap();
        this.sizeOfUniqueKeyMapOnLastCleanse = this.uniqueKeys.size();
    }

    private void cleanseUniqueKeyMap() {
        if (this.uniqueKeys.isEmpty()) {
            return;
        }
        if (this.uniqueKeys.size() == 1) {
            Map.Entry<String, UniqueKey> uniqueKeyEntry = this.uniqueKeys.entrySet().iterator().next();
            if (this.isSameAsPrimaryKeyColumns(uniqueKeyEntry.getValue())) {
                this.uniqueKeys.remove(uniqueKeyEntry.getKey());
            }
        } else {
            Iterator<Map.Entry<String, UniqueKey>> uniqueKeyEntries = this.uniqueKeys.entrySet().iterator();
            while (uniqueKeyEntries.hasNext()) {
                Map.Entry<String, UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
                UniqueKey uniqueKey = uniqueKeyEntry.getValue();
                boolean removeIt = false;
                for (UniqueKey otherUniqueKey : this.uniqueKeys.values()) {
                    if (uniqueKeyEntry.getValue() == otherUniqueKey || !otherUniqueKey.getColumns().containsAll(uniqueKey.getColumns()) || !uniqueKey.getColumns().containsAll(otherUniqueKey.getColumns())) continue;
                    removeIt = true;
                    break;
                }
                if (this.isSameAsPrimaryKeyColumns(uniqueKeyEntry.getValue())) {
                    removeIt = true;
                }
                if (!removeIt) continue;
                uniqueKeyEntries.remove();
            }
        }
    }

    private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
        if (this.primaryKey == null || !this.primaryKey.columnIterator().hasNext()) {
            return false;
        }
        return this.primaryKey.getColumns().containsAll(uniqueKey.getColumns()) && uniqueKey.getColumns().containsAll(this.primaryKey.getColumns());
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.catalog == null ? 0 : this.catalog.hashCode());
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.schema == null ? 0 : this.schema.hashCode());
        return result;
    }

    public boolean equals(Object object) {
        return object instanceof Table && this.equals((Table)object);
    }

    public boolean equals(Table table) {
        if (null == table) {
            return false;
        }
        if (this == table) {
            return true;
        }
        return Identifier.areEqual(this.name, table.name) && Identifier.areEqual(this.schema, table.schema) && Identifier.areEqual(this.catalog, table.catalog);
    }

    public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
        Iterator<Column> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            Column col = iter.next();
            ColumnMetadata columnInfo = tableInfo.getColumnMetadata(col.getName());
            if (columnInfo == null) {
                throw new HibernateException("Missing column: " + col.getName() + " in " + Table.qualify(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
            }
            boolean typesMatch = dialect.equivalentTypes(columnInfo.getTypeCode(), col.getSqlTypeCode(mapping)) || col.getSqlType(dialect, mapping).toLowerCase(Locale.ROOT).startsWith(columnInfo.getTypeName().toLowerCase(Locale.ROOT));
            if (typesMatch) continue;
            throw new HibernateException("Wrong column type in " + Table.qualify(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) + " for column " + col.getName() + ". Found: " + columnInfo.getTypeName().toLowerCase(Locale.ROOT) + ", expected: " + col.getSqlType(dialect, mapping));
        }
    }

    public Iterator<String> sqlAlterStrings(Dialect dialect, Metadata metadata, TableInformation tableInfo, SqlStringGenerationContext sqlStringGenerationContext) throws HibernateException {
        String tableName = sqlStringGenerationContext.format(new QualifiedTableName(this.catalog, this.schema, this.name));
        StringBuilder root = new StringBuilder(dialect.getAlterTableString(tableName)).append(' ').append(dialect.getAddColumnString());
        Iterator<Column> iter = this.getColumnIterator();
        ArrayList<String> results = new ArrayList<String>();
        while (iter.hasNext()) {
            String columnComment;
            Column column = iter.next();
            ColumnInformation columnInfo = tableInfo.getColumn(Identifier.toIdentifier(column.getName(), column.isQuoted()));
            if (columnInfo != null) continue;
            StringBuilder alter = new StringBuilder(root.toString()).append(' ').append(column.getQuotedName(dialect)).append(' ').append(column.getSqlType(dialect, metadata));
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null) {
                alter.append(" default ").append(defaultValue);
            }
            if (column.isNullable()) {
                alter.append(dialect.getNullColumnString());
            } else {
                alter.append(" not null");
            }
            if (column.isUnique()) {
                String keyName = Constraint.generateName("UK_", this, column);
                UniqueKey uk = this.getOrCreateUniqueKey(keyName);
                uk.addColumn(column);
                alter.append(dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(column, sqlStringGenerationContext));
            }
            if (column.hasCheckConstraint() && dialect.supportsColumnCheck()) {
                alter.append(" check(").append(column.getCheckConstraint()).append(")");
            }
            if ((columnComment = column.getComment()) != null) {
                alter.append(dialect.getColumnComment(columnComment));
            }
            alter.append(dialect.getAddColumnSuffixString());
            results.add(alter.toString());
        }
        if (results.isEmpty()) {
            log.debugf("No alter strings for table : %s", (Object)this.getQuotedName());
        }
        return results.iterator();
    }

    public boolean hasPrimaryKey() {
        return this.getPrimaryKey() != null;
    }

    @Override
    public String sqlCreateString(Mapping p, SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        StringBuilder buf = new StringBuilder(this.hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString()).append(' ').append(this.getQualifiedName(context)).append(" (");
        boolean identityColumn = this.idValue != null && this.idValue.isIdentityColumn(p.getIdentifierGeneratorFactory(), dialect);
        String pkname = null;
        if (this.hasPrimaryKey() && identityColumn) {
            pkname = this.getPrimaryKey().getColumnIterator().next().getQuotedName(dialect);
        }
        Iterator<Column> iter = this.getColumnIterator();
        while (iter.hasNext()) {
            String columnComment;
            Column col = iter.next();
            buf.append(col.getQuotedName(dialect)).append(' ');
            if (identityColumn && col.getQuotedName(dialect).equals(pkname)) {
                if (dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn()) {
                    buf.append(col.getSqlType(dialect, p));
                }
                buf.append(' ').append(dialect.getIdentityColumnSupport().getIdentityColumnString(col.getSqlTypeCode(p)));
            } else {
                buf.append(col.getSqlType(dialect, p));
                String defaultValue = col.getDefaultValue();
                if (defaultValue != null) {
                    buf.append(" default ").append(defaultValue);
                }
                if (col.isNullable()) {
                    buf.append(dialect.getNullColumnString());
                } else {
                    buf.append(" not null");
                }
            }
            if (col.isUnique()) {
                String keyName = Constraint.generateName("UK_", this, col);
                UniqueKey uk = this.getOrCreateUniqueKey(keyName);
                uk.addColumn(col);
                buf.append(dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(col, context));
            }
            if (col.hasCheckConstraint() && dialect.supportsColumnCheck()) {
                buf.append(" check (").append(col.getCheckConstraint()).append(")");
            }
            if ((columnComment = col.getComment()) != null) {
                buf.append(dialect.getColumnComment(columnComment));
            }
            if (!iter.hasNext()) continue;
            buf.append(", ");
        }
        if (this.hasPrimaryKey()) {
            buf.append(", ").append(this.getPrimaryKey().sqlConstraintString(dialect));
        }
        buf.append(dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment(this, context));
        if (dialect.supportsTableCheck()) {
            for (String checkConstraint : this.checkConstraints) {
                buf.append(", check (").append(checkConstraint).append(')');
            }
        }
        buf.append(')');
        if (this.comment != null) {
            buf.append(dialect.getTableComment(this.comment));
        }
        return buf.append(dialect.getTableTypeString()).toString();
    }

    @Override
    public String sqlDropString(SqlStringGenerationContext context, String defaultCatalog, String defaultSchema) {
        Dialect dialect = context.getDialect();
        return dialect.getDropTableString(this.getQualifiedName(context));
    }

    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Index getOrCreateIndex(String indexName) {
        Index index = this.indexes.get(indexName);
        if (index == null) {
            index = new Index();
            index.setName(indexName);
            index.setTable(this);
            this.indexes.put(indexName, index);
        }
        return index;
    }

    public Index getIndex(String indexName) {
        return this.indexes.get(indexName);
    }

    public Index addIndex(Index index) {
        Index current = this.indexes.get(index.getName());
        if (current != null) {
            throw new MappingException("Index " + index.getName() + " already exists!");
        }
        this.indexes.put(index.getName(), index);
        return index;
    }

    public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
        UniqueKey current = this.uniqueKeys.get(uniqueKey.getName());
        if (current != null) {
            throw new MappingException("UniqueKey " + uniqueKey.getName() + " already exists!");
        }
        this.uniqueKeys.put(uniqueKey.getName(), uniqueKey);
        return uniqueKey;
    }

    public UniqueKey createUniqueKey(List<Column> keyColumns) {
        String keyName = Constraint.generateName("UK_", this, keyColumns);
        UniqueKey uk = this.getOrCreateUniqueKey(keyName);
        uk.addColumns(keyColumns.iterator());
        return uk;
    }

    public UniqueKey getUniqueKey(String keyName) {
        return this.uniqueKeys.get(keyName);
    }

    public UniqueKey getOrCreateUniqueKey(String keyName) {
        UniqueKey uk = this.uniqueKeys.get(keyName);
        if (uk == null) {
            uk = new UniqueKey();
            uk.setName(keyName);
            uk.setTable(this);
            this.uniqueKeys.put(keyName, uk);
        }
        return uk;
    }

    public void createForeignKeys() {
    }

    public ForeignKey createForeignKey(String keyName, List<Column> keyColumns, String referencedEntityName, String keyDefinition) {
        return this.createForeignKey(keyName, keyColumns, referencedEntityName, keyDefinition, null);
    }

    public ForeignKey createForeignKey(String keyName, List<Column> keyColumns, String referencedEntityName, String keyDefinition, List<Column> referencedColumns) {
        ForeignKeyKey key = new ForeignKeyKey(keyColumns, referencedEntityName, referencedColumns);
        ForeignKey fk = this.foreignKeys.get(key);
        if (fk == null) {
            fk = new ForeignKey();
            fk.setTable(this);
            fk.setReferencedEntityName(referencedEntityName);
            fk.setKeyDefinition(keyDefinition);
            fk.addColumns(keyColumns.iterator());
            if (referencedColumns != null) {
                fk.addReferencedColumns(referencedColumns.iterator());
            }
            fk.setName(keyName);
            this.foreignKeys.put(key, fk);
        }
        if (keyName != null) {
            fk.setName(keyName);
        }
        return fk;
    }

    public void setUniqueInteger(int uniqueInteger) {
        this.uniqueInteger = uniqueInteger;
    }

    public int getUniqueInteger() {
        return this.uniqueInteger;
    }

    public void setIdentifierValue(KeyValue idValue) {
        this.idValue = idValue;
    }

    public KeyValue getIdentifierValue() {
        return this.idValue;
    }

    public void addCheckConstraint(String constraint) {
        this.checkConstraints.add(constraint);
    }

    public boolean containsColumn(Column column) {
        return this.columns.containsValue(column);
    }

    public String getRowId() {
        return this.rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(this.getClass().getName()).append('(');
        if (this.getCatalog() != null) {
            buf.append(this.getCatalog()).append(".");
        }
        if (this.getSchema() != null) {
            buf.append(this.getSchema()).append(".");
        }
        buf.append(this.getName()).append(')');
        return buf.toString();
    }

    public String getSubselect() {
        return this.subselect;
    }

    public void setSubselect(String subselect) {
        this.subselect = subselect;
    }

    public boolean isSubselect() {
        return this.subselect != null;
    }

    public boolean isAbstractUnionTable() {
        return this.hasDenormalizedTables() && this.isAbstract;
    }

    public boolean hasDenormalizedTables() {
        return this.hasDenormalizedTables;
    }

    void setHasDenormalizedTables() {
        this.hasDenormalizedTables = true;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isPhysicalTable() {
        return !this.isSubselect() && !this.isAbstractUnionTable();
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Iterator<String> getCheckConstraintsIterator() {
        return this.checkConstraints.iterator();
    }

    @Override
    public String getExportIdentifier() {
        return Table.qualify(this.render(this.catalog), this.render(this.schema), this.name.render());
    }

    private String render(Identifier identifier) {
        return identifier == null ? null : identifier.render();
    }

    @Deprecated
    public void addInitCommand(InitCommand command) {
        this.addInitCommand((SqlStringGenerationContext ignored) -> command);
    }

    public void addInitCommand(Function<SqlStringGenerationContext, InitCommand> commandProducer) {
        if (this.initCommandProducers == null) {
            this.initCommandProducers = new ArrayList<Function<SqlStringGenerationContext, InitCommand>>();
        }
        this.initCommandProducers.add(commandProducer);
    }

    public List<InitCommand> getInitCommands(SqlStringGenerationContext context) {
        if (this.initCommandProducers == null) {
            return Collections.emptyList();
        }
        ArrayList<InitCommand> initCommands = new ArrayList<InitCommand>();
        for (Function<SqlStringGenerationContext, InitCommand> producer : this.initCommandProducers) {
            initCommands.add(producer.apply(context));
        }
        return Collections.unmodifiableList(initCommands);
    }

    public static class ForeignKeyKey
    implements Serializable {
        private final String referencedClassName;
        private final Column[] columns;
        private final Column[] referencedColumns;

        ForeignKeyKey(List<Column> columns, String referencedClassName, List<Column> referencedColumns) {
            Objects.requireNonNull(columns);
            Objects.requireNonNull(referencedClassName);
            this.referencedClassName = referencedClassName;
            this.columns = columns.toArray(EMPTY_COLUMN_ARRAY);
            this.referencedColumns = referencedColumns != null ? referencedColumns.toArray(EMPTY_COLUMN_ARRAY) : EMPTY_COLUMN_ARRAY;
        }

        public int hashCode() {
            return Arrays.hashCode(this.columns) + Arrays.hashCode(this.referencedColumns);
        }

        public boolean equals(Object other) {
            ForeignKeyKey fkk = (ForeignKeyKey)other;
            return fkk != null && Arrays.equals(fkk.columns, this.columns) && Arrays.equals(fkk.referencedColumns, this.referencedColumns);
        }

        public String toString() {
            return "ForeignKeyKey{columns=" + Arrays.toString(this.columns) + ", referencedClassName='" + this.referencedClassName + "', referencedColumns=" + Arrays.toString(this.referencedColumns) + '}';
        }
    }
}

