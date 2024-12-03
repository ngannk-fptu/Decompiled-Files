/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.java.ao.schema.helper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import net.java.ao.DatabaseProvider;
import net.java.ao.RawEntity;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.helper.DatabaseMetaDataReader;
import net.java.ao.schema.helper.Field;
import net.java.ao.schema.helper.ForeignKey;
import net.java.ao.schema.helper.Index;
import net.java.ao.sql.AbstractCloseableResultSetMetaData;
import net.java.ao.sql.CloseableResultSetMetaData;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;
import net.java.ao.types.TypeQualifiers;
import net.java.ao.util.StringUtils;

public class DatabaseMetaDataReaderImpl
implements DatabaseMetaDataReader {
    private static final Pattern STRING_VALUE = Pattern.compile("\"(.*)\"");
    private static final Set<Integer> STRING_JDBC_TYPES = ImmutableSet.of((Object)12, (Object)-1, (Object)-9, (Object)-16, (Object)2005, (Object)2011, (Object[])new Integer[0]);
    private final DatabaseProvider databaseProvider;
    private final NameConverters nameConverters;
    private final SchemaConfiguration schemaConfiguration;

    public DatabaseMetaDataReaderImpl(DatabaseProvider databaseProvider, NameConverters nameConverters, SchemaConfiguration schemaConfiguration) {
        this.databaseProvider = databaseProvider;
        this.nameConverters = nameConverters;
        this.schemaConfiguration = schemaConfiguration;
    }

    @Override
    public boolean isTablePresent(DatabaseMetaData databaseMetaData, Class<? extends RawEntity<?>> type) {
        String entityTableName = this.nameConverters.getTableNameConverter().getName(type);
        Iterable<String> tableNames = this.getTableNames(databaseMetaData);
        return StreamSupport.stream(tableNames.spliterator(), false).anyMatch(tableName -> tableName.equalsIgnoreCase(entityTableName));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Iterable<String> getTableNames(DatabaseMetaData metaData) {
        try (ResultSet rs = this.databaseProvider.getTables(metaData.getConnection());){
            LinkedList<String> tableNames = new LinkedList<String>();
            while (rs.next()) {
                String tableName = this.parseStringValue(rs, "TABLE_NAME");
                if (!this.schemaConfiguration.shouldManageTable(tableName, this.databaseProvider.isCaseSensitive())) continue;
                tableNames.add(tableName);
            }
            LinkedList<String> linkedList = tableNames;
            return linkedList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterable<? extends Field> getFields(DatabaseMetaData databaseMetaData, String tableName) {
        TypeManager manager = this.databaseProvider.getTypeManager();
        List<String> sequenceNames = this.getSequenceNames(databaseMetaData);
        Set<String> uniqueFields = this.getUniqueFields(databaseMetaData, tableName);
        HashMap fields = Maps.newHashMap();
        try (CloseableResultSetMetaData rsmd = null;){
            String fieldName;
            rsmd = this.getResultSetMetaData(databaseMetaData, tableName);
            for (int i = 1; i < rsmd.getColumnCount() + 1; ++i) {
                fieldName = rsmd.getColumnName(i);
                TypeQualifiers qualifiers = this.getTypeQualifiers(rsmd, i);
                int jdbcType = rsmd.getColumnType(i);
                TypeInfo<?> databaseType = manager.getTypeFromSchema(jdbcType, qualifiers);
                if (databaseType == null) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("TABLE: " + tableName + ": ");
                    for (int j = 1; j <= rsmd.getColumnCount(); ++j) {
                        buf.append(rsmd.getColumnName(j)).append(" - ");
                    }
                    buf.append("can't find type " + jdbcType + " " + qualifiers + " in field " + fieldName);
                    throw new IllegalStateException(buf.toString());
                }
                boolean autoIncrement = this.isAutoIncrement(rsmd, i, sequenceNames, tableName, fieldName);
                boolean notNull = this.isNotNull(rsmd, i);
                boolean isUnique = this.isUnique(uniqueFields, fieldName);
                fields.put(fieldName, this.newField(fieldName, databaseType, jdbcType, autoIncrement, notNull, isUnique));
            }
            ResultSet rs = null;
            try {
                rs = databaseMetaData.getColumns(databaseMetaData.getConnection().getCatalog(), null, tableName, null);
                while (rs.next()) {
                    String columnName = this.parseStringValue(rs, "COLUMN_NAME");
                    FieldImpl current = (FieldImpl)fields.get(columnName);
                    if (current == null) {
                        throw new IllegalStateException("Could not find column '" + columnName + "' in previously parsed query!");
                    }
                    current.setDefaultValue(this.databaseProvider.parseValue(current.getDatabaseType().getJdbcWriteType(), this.parseStringValue(rs, "COLUMN_DEF")));
                    current.setNotNull(current.isNotNull() || this.parseStringValue(rs, "IS_NULLABLE").equals("NO"));
                }
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(rs);
                throw throwable;
            }
            SqlUtils.closeQuietly(rs);
            try {
                rs = databaseMetaData.getPrimaryKeys(databaseMetaData.getConnection().getCatalog(), this.databaseProvider.getSchema(), tableName);
                while (rs.next()) {
                    fieldName = this.parseStringValue(rs, "COLUMN_NAME");
                    FieldImpl field = (FieldImpl)fields.get(fieldName);
                    field.setPrimaryKey(true);
                    field.setUnique(false);
                }
            }
            finally {
                SqlUtils.closeQuietly(rs);
            }
            Collection collection = fields.values();
            return collection;
        }
    }

    @Override
    public Iterable<? extends Index> getIndexes(DatabaseMetaData databaseMetaData, String tableName) {
        ImmutableList immutableList;
        ImmutableList.Builder indexes = ImmutableList.builder();
        ResultSet resultSet = null;
        try {
            ArrayListMultimap fieldsByIndex = ArrayListMultimap.create();
            resultSet = this.databaseProvider.getIndexes(databaseMetaData.getConnection(), tableName);
            while (resultSet.next()) {
                boolean nonUnique = resultSet.getBoolean("NON_UNIQUE");
                if (!nonUnique) continue;
                fieldsByIndex.put((Object)this.parseStringValue(resultSet, "INDEX_NAME"), (Object)this.parseStringValue(resultSet, "COLUMN_NAME"));
            }
            for (String indexName : fieldsByIndex.keySet()) {
                Collection fieldNames = fieldsByIndex.get((Object)indexName);
                indexes.add((Object)new IndexImpl(indexName, tableName, fieldNames));
            }
            immutableList = indexes.build();
        }
        catch (SQLException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(resultSet);
                throw throwable;
            }
        }
        SqlUtils.closeQuietly(resultSet);
        return immutableList;
    }

    private Set<String> getUniqueFields(DatabaseMetaData metaData, String tableName) {
        HashSet hashSet;
        ResultSet rs = null;
        try {
            rs = this.databaseProvider.getIndexes(metaData.getConnection(), tableName);
            HashSet fields = Sets.newHashSet();
            while (rs.next()) {
                boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                if (nonUnique) continue;
                fields.add(this.parseStringValue(rs, "COLUMN_NAME"));
            }
            hashSet = fields;
        }
        catch (SQLException e) {
            try {
                throw new RuntimeException("Could not get unique fields for table '" + tableName + "'", e);
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(rs);
                throw throwable;
            }
        }
        SqlUtils.closeQuietly(rs);
        return hashSet;
    }

    private List<String> getSequenceNames(DatabaseMetaData metaData) {
        ResultSet rs = null;
        try {
            rs = this.databaseProvider.getSequences(metaData.getConnection());
            LinkedList<String> sequenceNames = new LinkedList<String>();
            while (rs.next()) {
                sequenceNames.add(this.databaseProvider.processID(this.parseStringValue(rs, "TABLE_NAME")));
            }
            LinkedList<String> linkedList = sequenceNames;
            return linkedList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (rs != null) {
                try {
                    SqlUtils.closeQuietly(rs.getStatement());
                }
                catch (SQLException sQLException) {}
            }
            SqlUtils.closeQuietly(rs);
        }
    }

    private boolean isAutoIncrement(ResultSetMetaData rsmd, int i, List<String> sequenceNames, String tableName, String fieldName) throws SQLException {
        boolean autoIncrement = rsmd.isAutoIncrement(i);
        if (!autoIncrement) {
            autoIncrement = this.isUsingSequence(sequenceNames, tableName, fieldName);
        }
        return autoIncrement;
    }

    private boolean isUsingSequence(List<String> sequenceNames, String tableName, String fieldName) {
        return sequenceNames.contains(this.databaseProvider.processID(this.nameConverters.getSequenceNameConverter().getName(tableName, fieldName)));
    }

    private boolean isUnique(Set<String> uniqueFields, String fieldName) throws SQLException {
        return uniqueFields.contains(fieldName);
    }

    private FieldImpl newField(String fieldName, TypeInfo<?> databaseType, int jdbcType, boolean autoIncrement, boolean notNull, boolean isUnique) {
        return new FieldImpl(fieldName, databaseType, jdbcType, autoIncrement, notNull, isUnique);
    }

    private boolean isNotNull(ResultSetMetaData resultSetMetaData, int fieldIndex) throws SQLException {
        return resultSetMetaData.isNullable(fieldIndex) == 0;
    }

    private TypeQualifiers getTypeQualifiers(ResultSetMetaData rsmd, int fieldIndex) throws SQLException {
        TypeQualifiers ret = TypeQualifiers.qualifiers();
        if (this.isStringType(rsmd, fieldIndex)) {
            int length = rsmd.getColumnDisplaySize(fieldIndex);
            if (length > 0) {
                ret = ret.stringLength(length);
            }
        } else {
            int precision = rsmd.getPrecision(fieldIndex);
            int scale = rsmd.getScale(fieldIndex);
            if (precision > 0) {
                ret = ret.precision(precision);
            }
            if (scale > 0) {
                ret = ret.scale(scale);
            }
        }
        return ret;
    }

    private boolean isStringType(ResultSetMetaData rsmd, int fieldIndex) throws SQLException {
        return STRING_JDBC_TYPES.contains(rsmd.getColumnType(fieldIndex));
    }

    private CloseableResultSetMetaData getResultSetMetaData(DatabaseMetaData metaData, String tableName) throws SQLException {
        final PreparedStatement stmt = metaData.getConnection().prepareStatement(this.databaseProvider.renderMetadataQuery(tableName));
        final ResultSet rs = stmt.executeQuery();
        return new AbstractCloseableResultSetMetaData(rs.getMetaData()){

            @Override
            public void close() {
                SqlUtils.closeQuietly(rs);
                SqlUtils.closeQuietly(stmt);
            }
        };
    }

    public Iterable<ForeignKey> getForeignKeys(DatabaseMetaData metaData, String tableName) {
        LinkedList<ForeignKey> linkedList;
        ResultSet resultSet = null;
        try {
            LinkedList<ForeignKey> keys = new LinkedList<ForeignKey>();
            resultSet = this.getImportedKeys(metaData, tableName);
            while (resultSet.next()) {
                keys.add(this.newForeignKey(resultSet, tableName));
            }
            linkedList = keys;
        }
        catch (SQLException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                SqlUtils.closeQuietly(resultSet);
                throw throwable;
            }
        }
        SqlUtils.closeQuietly(resultSet);
        return linkedList;
    }

    private ResultSet getImportedKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
        return this.databaseProvider.getImportedKeys(metaData.getConnection(), tableName);
    }

    private ForeignKey newForeignKey(ResultSet rs, String localTableName) throws SQLException {
        String localFieldName = this.parseStringValue(rs, "FKCOLUMN_NAME");
        String foreignFieldName = this.parseStringValue(rs, "PKCOLUMN_NAME");
        String foreignTableName = this.parseStringValue(rs, "PKTABLE_NAME");
        return new ForeignKeyImpl(localTableName, localFieldName, foreignTableName, foreignFieldName);
    }

    private String parseStringValue(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (StringUtils.isBlank(value)) {
            return value;
        }
        Matcher m = STRING_VALUE.matcher(value);
        return m.find() ? m.group(1) : value;
    }

    private static final class IndexImpl
    implements Index {
        private final String indexName;
        private final String tableName;
        private final Collection<String> fieldNames;

        public IndexImpl(String indexName, String tableName, Collection<String> fieldNames) {
            this.indexName = indexName;
            this.tableName = tableName;
            this.fieldNames = fieldNames;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public Collection<String> getFieldNames() {
            return this.fieldNames;
        }

        @Override
        public String getIndexName() {
            return this.indexName;
        }

        public String toString() {
            return "IndexImpl{indexName='" + this.indexName + '\'' + ", tableName='" + this.tableName + '\'' + ", fieldNames=" + this.fieldNames + '}';
        }
    }

    private static final class ForeignKeyImpl
    implements ForeignKey {
        private final String localTableName;
        private final String localFieldName;
        private final String foreignTableName;
        private final String foreignFieldName;

        public ForeignKeyImpl(String localTableName, String localFieldName, String foreignTableName, String foreignFieldName) {
            this.localTableName = localTableName;
            this.localFieldName = localFieldName;
            this.foreignTableName = foreignTableName;
            this.foreignFieldName = foreignFieldName;
        }

        @Override
        public String getLocalTableName() {
            return this.localTableName;
        }

        @Override
        public String getLocalFieldName() {
            return this.localFieldName;
        }

        @Override
        public String getForeignTableName() {
            return this.foreignTableName;
        }

        @Override
        public String getForeignFieldName() {
            return this.foreignFieldName;
        }
    }

    private static final class FieldImpl
    implements Field {
        private final String name;
        private final TypeInfo<?> databaseType;
        private final int jdbcType;
        private final boolean autoIncrement;
        private boolean notNull;
        private Object defaultValue;
        private boolean primaryKey;
        private boolean isUnique;

        public FieldImpl(String name, TypeInfo<?> databaseType, int jdbcType, boolean autoIncrement, boolean notNull, boolean isUnique) {
            this.name = name;
            this.databaseType = databaseType;
            this.jdbcType = jdbcType;
            this.autoIncrement = autoIncrement;
            this.notNull = notNull;
            this.isUnique = isUnique;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public TypeInfo<?> getDatabaseType() {
            return this.databaseType;
        }

        @Override
        public int getJdbcType() {
            return this.jdbcType;
        }

        @Override
        public boolean isAutoIncrement() {
            return this.autoIncrement;
        }

        @Override
        public boolean isNotNull() {
            return this.notNull;
        }

        public void setNotNull(boolean notNull) {
            this.notNull = notNull;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean isPrimaryKey() {
            return this.primaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        @Override
        public boolean isUnique() {
            return this.isUnique;
        }

        public void setUnique(boolean unique) {
            this.isUnique = unique;
        }
    }
}

