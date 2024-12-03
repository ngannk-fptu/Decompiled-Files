/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.java.ao;

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;

public class Query
implements Serializable {
    private static final String PRIMARY_KEY_FIELD = "''''primary_key_field''''";
    protected static final Pattern ALIAS_PATTERN = Pattern.compile("(.+?)\\s+(?:[aA][sS]\\s+)?(\\w+)$");
    protected static final Pattern AGGREGATE_FUNCTION_PATTERN = Pattern.compile("(\\S+)\\((\\S+)\\)");
    private final QueryType type;
    private String fields;
    private boolean distinct = false;
    private Class<? extends RawEntity<?>> tableType;
    private String table;
    private String whereClause;
    private Object[] whereParams;
    private String orderClause;
    private String groupClause;
    private String havingClause;
    private int limit = -1;
    private int offset = -1;
    private Map<Class<? extends RawEntity<?>>, String> joins;
    private Map<Class<? extends RawEntity<?>>, String> aliases = Maps.newHashMap();

    public Query(QueryType type, String fields) {
        Query.validateSelectFields(fields);
        this.type = type;
        this.fields = fields;
        this.joins = new LinkedHashMap();
    }

    public Iterable<String> getFields() {
        return this.getFields(Function.identity());
    }

    public List<FieldMetadata> getFieldMetadata() {
        return this.getFields(this::getSingleFieldMetadata);
    }

    private <T> List<T> getFields(Function<String, T> resultMapper) {
        if (this.fields.contains(PRIMARY_KEY_FIELD)) {
            return Collections.emptyList();
        }
        return Arrays.stream(this.fields.split(",")).map(String::trim).map(resultMapper).collect(Collectors.toList());
    }

    void setFields(String[] fields) {
        if (fields.length == 0) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String field : fields) {
            builder.append(field).append(',');
        }
        if (fields.length > 1) {
            builder.setLength(builder.length() - 1);
        }
        this.fields = builder.toString();
    }

    <K> void resolvePrimaryKey(FieldInfo<K> fieldInfo) {
        this.fields = this.fields.replaceAll(PRIMARY_KEY_FIELD, fieldInfo.getName());
    }

    public Query distinct() {
        this.distinct = true;
        return this;
    }

    public Query from(Class<? extends RawEntity<?>> tableType) {
        this.table = null;
        this.tableType = tableType;
        return this;
    }

    public Query from(String table) {
        this.tableType = null;
        this.table = table;
        return this;
    }

    public Query where(String clause, Object ... params) {
        this.whereClause = clause;
        this.setWhereParams(params);
        return this;
    }

    public Query order(String clause) {
        this.orderClause = clause;
        return this;
    }

    public Query group(String clause) {
        this.groupClause = clause;
        return this;
    }

    public Query having(String clause) {
        this.havingClause = clause;
        return this;
    }

    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Query offset(int offset) {
        this.offset = offset;
        return this;
    }

    public Query alias(Class<? extends RawEntity<?>> table, String alias) {
        if (this.aliases.containsValue(alias)) {
            throw new ActiveObjectsException("There is already a table aliased '" + alias + "' for this query!");
        }
        this.aliases.put(table, alias);
        return this;
    }

    public String getAlias(Class<? extends RawEntity<?>> table) {
        return this.aliases.get(table);
    }

    public Query join(Class<? extends RawEntity<?>> join, String on) {
        this.joins.put(join, on);
        return this;
    }

    public Query join(Class<? extends RawEntity<?>> join) {
        this.joins.put(join, null);
        return this;
    }

    public boolean isDistinct() {
        return this.distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Class<? extends RawEntity<?>> getTableType() {
        return this.tableType;
    }

    public void setTableType(Class<? extends RawEntity<?>> tableType) {
        this.tableType = tableType;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getWhereClause() {
        return this.whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public Object[] getWhereParams() {
        return this.whereParams;
    }

    public void setWhereParams(Object[] whereParams) {
        this.whereParams = whereParams;
        if (whereParams != null) {
            for (int i = 0; i < whereParams.length; ++i) {
                if (!(whereParams[i] instanceof RawEntity)) continue;
                whereParams[i] = Common.getPrimaryKeyValue((RawEntity)whereParams[i]);
            }
        }
    }

    public String getOrderClause() {
        return this.orderClause;
    }

    public void setOrderClause(String orderClause) {
        this.orderClause = orderClause;
    }

    public String getGroupClause() {
        return this.groupClause;
    }

    public void setGroupClause(String groupClause) {
        this.groupClause = groupClause;
    }

    public String getHavingClause() {
        return this.havingClause;
    }

    public void setHavingClause(String havingClause) {
        this.havingClause = havingClause;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Map<Class<? extends RawEntity<?>>, String> getJoins() {
        return Collections.unmodifiableMap(this.joins);
    }

    public void setJoins(Map<Class<? extends RawEntity<?>>, String> joins) {
        this.joins = joins;
    }

    public QueryType getType() {
        return this.type;
    }

    public String[] getCanonicalFields(EntityInfo<?, ?> entityInfo) {
        String[] back = this.fields.split(",");
        ArrayList<String> result = new ArrayList<String>();
        for (String fieldName : back) {
            String trimmedFieldName = fieldName.trim();
            Optional<String> alias = this.getAlias(trimmedFieldName);
            if (alias.isPresent()) {
                result.add(alias.get());
                continue;
            }
            if (trimmedFieldName.equals("*")) {
                for (FieldInfo fieldInfo : entityInfo.getFields()) {
                    result.add(fieldInfo.getName());
                }
                continue;
            }
            result.add(trimmedFieldName);
        }
        return result.toArray(new String[result.size()]);
    }

    protected Optional<String> getAlias(String field) {
        Matcher matcher = ALIAS_PATTERN.matcher(field);
        if (matcher.find()) {
            return Optional.of(matcher.group(2));
        }
        return Optional.empty();
    }

    protected FieldMetadata getSingleFieldMetadata(String field) {
        String maybeAggregateFunction;
        String actualField;
        String actualFieldAndMaybeAggregate;
        String maybeAlias;
        Matcher aliasMatcher = ALIAS_PATTERN.matcher(field);
        if (aliasMatcher.find()) {
            maybeAlias = aliasMatcher.group(2);
            actualFieldAndMaybeAggregate = aliasMatcher.group(1);
        } else {
            maybeAlias = null;
            actualFieldAndMaybeAggregate = field;
        }
        Matcher aggregateMatcher = AGGREGATE_FUNCTION_PATTERN.matcher(actualFieldAndMaybeAggregate);
        if (aggregateMatcher.find()) {
            actualField = aggregateMatcher.group(2);
            maybeAggregateFunction = aggregateMatcher.group(1);
        } else {
            actualField = actualFieldAndMaybeAggregate;
            maybeAggregateFunction = null;
        }
        return new FieldMetadata(actualField, maybeAlias, maybeAggregateFunction);
    }

    protected <K> String toSQL(EntityInfo<? extends RawEntity<K>, K> entityInfo, DatabaseProvider provider, TableNameConverter converter, boolean count) {
        if (this.tableType == null && this.table == null) {
            this.tableType = entityInfo.getEntityType();
        }
        this.resolvePrimaryKey(entityInfo.getPrimaryKey());
        return provider.renderQuery(this, converter, count);
    }

    protected void setParameters(EntityManager manager, PreparedStatement stmt) throws SQLException {
        if (this.whereParams != null) {
            TypeManager typeManager = manager.getProvider().getTypeManager();
            for (int i = 0; i < this.whereParams.length; ++i) {
                if (this.whereParams[i] == null) {
                    manager.getProvider().putNull(stmt, i + 1);
                    continue;
                }
                Class<Object> javaType = this.whereParams[i].getClass();
                if (this.whereParams[i] instanceof RawEntity) {
                    javaType = ((RawEntity)this.whereParams[i]).getEntityType();
                }
                TypeInfo<?> typeInfo = typeManager.getType(javaType);
                typeInfo.getLogicalType().putToDatabase(manager, stmt, i + 1, this.whereParams[i], typeInfo.getJdbcWriteType());
            }
        }
    }

    public static Query select() {
        return Query.select(PRIMARY_KEY_FIELD);
    }

    public static Query select(String fields) {
        return new Query(QueryType.SELECT, fields);
    }

    private static void validateSelectFields(String fields) {
        if (fields == null) {
            return;
        }
        if (fields.contains("*")) {
            throw new IllegalArgumentException("fields must not contain '*' - got '" + fields + "'");
        }
    }

    public static class FieldMetadata {
        private final String columnName;
        private final String alias;
        private final String aggregateFunction;

        public FieldMetadata(@Nonnull String columnName, @Nullable String alias, @Nullable String aggregateFunction) {
            this.columnName = Objects.requireNonNull(columnName);
            this.alias = alias;
            this.aggregateFunction = aggregateFunction;
        }

        public String getColumnName() {
            return this.columnName;
        }

        public Optional<String> getAlias() {
            return Optional.ofNullable(this.alias);
        }

        public Optional<String> getAggregateFunction() {
            return Optional.ofNullable(this.aggregateFunction);
        }

        public String renderField(boolean quoteColumnName, boolean quoteAlias, String quoteCharacter) {
            StringBuilder builder = new StringBuilder();
            if (this.aggregateFunction != null) {
                builder.append(this.aggregateFunction);
                builder.append("(");
            }
            if (quoteColumnName) {
                builder.append(quoteCharacter);
            }
            builder.append(Common.shorten(this.columnName, Integer.MAX_VALUE));
            if (quoteColumnName) {
                builder.append(quoteCharacter);
            }
            if (this.aggregateFunction != null) {
                builder.append(")");
            }
            if (this.alias != null) {
                builder.append(" as ");
                if (quoteAlias) {
                    builder.append(quoteCharacter);
                }
                builder.append(this.alias);
                if (quoteAlias) {
                    builder.append(quoteCharacter);
                }
            }
            return builder.toString();
        }
    }

    public static enum QueryType {
        SELECT;

    }
}

