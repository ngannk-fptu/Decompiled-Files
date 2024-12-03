/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.primitives.Primitives
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.DefaultSQLExceptionTranslator;
import com.querydsl.sql.JDBCTypeMapping;
import com.querydsl.sql.JavaTypeMapping;
import com.querydsl.sql.NameMapping;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExceptionTranslator;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListeners;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.types.ArrayType;
import com.querydsl.sql.types.Null;
import com.querydsl.sql.types.Type;
import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    static final Configuration DEFAULT = new Configuration(SQLTemplates.DEFAULT);
    private final JDBCTypeMapping jdbcTypeMapping = new JDBCTypeMapping();
    private final JavaTypeMapping javaTypeMapping = new JavaTypeMapping();
    private final NameMapping nameMapping = new NameMapping();
    private final Map<String, Class<?>> typeToName = Maps.newHashMap();
    private SQLTemplates templates;
    private SQLExceptionTranslator exceptionTranslator = DefaultSQLExceptionTranslator.DEFAULT;
    private final SQLListeners listeners = new SQLListeners();
    private boolean hasTableColumnTypes = false;
    private boolean useLiterals = false;

    public Configuration(SQLTemplates templates) {
        this.templates = templates;
        for (Type<?> type : templates.getCustomTypes()) {
            this.javaTypeMapping.register(type);
        }
        for (Map.Entry entry : templates.getTableOverrides().entrySet()) {
            this.registerTableOverride((SchemaAndTable)entry.getKey(), (SchemaAndTable)entry.getValue());
        }
        if (templates.isArraysSupported()) {
            ImmutableList classes = ImmutableList.of(String.class, Long.class, Integer.class, Short.class, Byte.class, Boolean.class, Date.class, Timestamp.class, Time.class, Double.class, Float.class);
            for (Class cl : classes) {
                int code = this.jdbcTypeMapping.get(cl);
                String name = templates.getTypeNameForCode(code);
                Class<?> arrType = Array.newInstance(cl, 0).getClass();
                this.javaTypeMapping.register(new ArrayType(arrType, name));
                if (!Primitives.isWrapperType((Class)cl) || cl.equals(Byte.class)) continue;
                cl = Primitives.unwrap((Class)cl);
                arrType = Array.newInstance(cl, 0).getClass();
                this.javaTypeMapping.register(new ArrayType(arrType, name));
            }
        }
    }

    public String asLiteral(Object o) {
        if (o == null || o instanceof Null) {
            return "null";
        }
        Type<?> type = this.javaTypeMapping.getType(o.getClass());
        if (type != null) {
            return this.templates.serialize(type.getLiteral(o), type.getSQLTypes()[0]);
        }
        throw new IllegalArgumentException("Unsupported literal type " + o.getClass().getName());
    }

    public SQLTemplates getTemplates() {
        return this.templates;
    }

    public Class<?> getJavaType(int sqlType, String typeName, int size, int digits, String tableName, String columnName) {
        Type<?> type = this.javaTypeMapping.getType(tableName, columnName);
        if (type != null) {
            return type.getReturnedClass();
        }
        if (typeName != null && !typeName.isEmpty()) {
            Class<?> clazz = this.typeToName.get(typeName = typeName.toLowerCase());
            if (clazz != null) {
                return clazz;
            }
            if (sqlType == 2003) {
                Integer sqlComponentType;
                if (typeName.startsWith("_")) {
                    typeName = typeName.substring(1);
                } else if (typeName.endsWith(" array")) {
                    typeName = typeName.substring(0, typeName.length() - 6);
                }
                if (typeName.contains("[")) {
                    typeName = typeName.substring(0, typeName.indexOf("["));
                }
                if (typeName.contains("(")) {
                    typeName = typeName.substring(0, typeName.indexOf("("));
                }
                if ((sqlComponentType = this.templates.getCodeForTypeName(typeName)) == null) {
                    logger.warn("Found no JDBC type for " + typeName + " using OTHER instead");
                    sqlComponentType = 1111;
                }
                Class<?> componentType = this.jdbcTypeMapping.get(sqlComponentType, size, digits);
                return Array.newInstance(componentType, 0).getClass();
            }
        }
        return this.jdbcTypeMapping.get(sqlType, size, digits);
    }

    @Nullable
    public <T> T get(ResultSet rs, @Nullable Path<?> path, int i, Class<T> clazz) throws SQLException {
        return this.getType(path, clazz).getValue(rs, i);
    }

    @Nullable
    public SchemaAndTable getOverride(SchemaAndTable key) {
        return this.nameMapping.getOverride(key);
    }

    public String getColumnOverride(SchemaAndTable key, String column) {
        return this.nameMapping.getColumnOverride(key, column);
    }

    public <T> void set(PreparedStatement stmt, Path<?> path, int i, T value) throws SQLException {
        if (value == null || value instanceof Null) {
            ColumnMetadata columnMetadata;
            Integer sqlType = null;
            if (path != null && (columnMetadata = ColumnMetadata.getColumnMetadata(path)).hasJdbcType()) {
                sqlType = columnMetadata.getJdbcType();
            }
            if (sqlType != null) {
                stmt.setNull(i, sqlType);
            } else {
                stmt.setNull(i, 0);
            }
        } else {
            this.getType(path, value.getClass()).setValue(stmt, i, value);
        }
    }

    private <T> Type<T> getType(@Nullable Path<?> path, Class<T> clazz) {
        String column;
        String table;
        Type<?> type;
        if (this.hasTableColumnTypes && path != null && !clazz.equals(Null.class) && path.getMetadata().getParent() instanceof RelationalPath && (type = this.javaTypeMapping.getType(table = ((RelationalPath)path.getMetadata().getParent()).getTableName(), column = ColumnMetadata.getName(path))) != null) {
            return type;
        }
        return this.javaTypeMapping.getType(clazz);
    }

    public String getTypeName(Class<?> type) {
        Integer jdbcType = this.jdbcTypeMapping.get(type);
        if (jdbcType == null) {
            jdbcType = this.javaTypeMapping.getType(type).getSQLTypes()[0];
        }
        return this.templates.getTypeNameForCode(jdbcType);
    }

    public String getTypeNameForCast(Class<?> type) {
        Integer jdbcType = this.jdbcTypeMapping.get(type);
        if (jdbcType == null) {
            jdbcType = this.javaTypeMapping.getType(type).getSQLTypes()[0];
        }
        return this.templates.getCastTypeNameForCode(jdbcType);
    }

    public String registerSchemaOverride(String oldSchema, String newSchema) {
        return this.nameMapping.registerSchemaOverride(oldSchema, newSchema);
    }

    public String registerTableOverride(String oldTable, String newTable) {
        return this.nameMapping.registerTableOverride(oldTable, newTable);
    }

    public String registerTableOverride(String schema, String oldTable, String newTable) {
        SchemaAndTable st = this.registerTableOverride(schema, oldTable, schema, newTable);
        return st != null ? st.getTable() : null;
    }

    public SchemaAndTable registerTableOverride(String schema, String oldTable, String newSchema, String newTable) {
        return this.registerTableOverride(new SchemaAndTable(schema, oldTable), new SchemaAndTable(newSchema, newTable));
    }

    public SchemaAndTable registerTableOverride(SchemaAndTable from, SchemaAndTable to) {
        return this.nameMapping.registerTableOverride(from, to);
    }

    public String registerColumnOverride(String schema, String table, String oldColumn, String newColumn) {
        return this.nameMapping.registerColumnOverride(schema, table, oldColumn, newColumn);
    }

    public String registerColumnOverride(String table, String oldColumn, String newColumn) {
        return this.nameMapping.registerColumnOverride(table, oldColumn, newColumn);
    }

    public void register(Type<?> type) {
        this.jdbcTypeMapping.register(type.getSQLTypes()[0], type.getReturnedClass());
        this.javaTypeMapping.register(type);
    }

    public void registerType(String typeName, Class<?> clazz) {
        this.typeToName.put(typeName.toLowerCase(), clazz);
    }

    public void registerNumeric(int total, int decimal, Class<?> javaType) {
        this.jdbcTypeMapping.registerNumeric(total, decimal, javaType);
    }

    public void registerNumeric(int beginTotal, int endTotal, int beginDecimal, int endDecimal, Class<?> javaType) {
        for (int total = beginTotal; total <= endTotal; ++total) {
            for (int decimal = beginDecimal; decimal <= endDecimal; ++decimal) {
                this.registerNumeric(total, decimal, javaType);
            }
        }
    }

    public void register(String table, String column, Class<?> javaType) {
        this.register(table, column, this.javaTypeMapping.getType(javaType));
    }

    public void register(String table, String column, Type<?> type) {
        this.javaTypeMapping.setType(table, column, type);
        this.hasTableColumnTypes = true;
    }

    public RuntimeException translate(SQLException ex) {
        return this.exceptionTranslator.translate(ex);
    }

    public RuntimeException translate(String sql, List<Object> bindings, SQLException ex) {
        return this.exceptionTranslator.translate(sql, bindings, ex);
    }

    public void addListener(SQLListener listener) {
        this.listeners.add(listener);
    }

    public SQLListeners getListeners() {
        return this.listeners;
    }

    public boolean getUseLiterals() {
        return this.useLiterals;
    }

    public void setUseLiterals(boolean useLiterals) {
        this.useLiterals = useLiterals;
    }

    public void setExceptionTranslator(SQLExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    public void setTemplates(SQLTemplates templates) {
        this.templates = templates;
    }
}

