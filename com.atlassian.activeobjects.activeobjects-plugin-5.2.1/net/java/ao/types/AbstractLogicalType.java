/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.java.ao.types;

import com.google.common.collect.ImmutableSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.java.ao.EntityManager;
import net.java.ao.types.LogicalType;

abstract class AbstractLogicalType<T>
implements LogicalType<T> {
    private final String name;
    private final Set<Class<?>> types;
    private final int defaultJdbcWriteType;
    private final Set<Integer> jdbcReadTypes;

    protected AbstractLogicalType(String name, Class<?>[] types, int defaultJdbcWriteType, Integer[] jdbcReadTypes) {
        this.name = name;
        this.types = Collections.unmodifiableSet(Arrays.stream(types).collect(Collectors.toSet()));
        this.defaultJdbcWriteType = defaultJdbcWriteType;
        this.jdbcReadTypes = Collections.unmodifiableSet(Arrays.stream(jdbcReadTypes).collect(Collectors.toSet()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Class<?>> getAllTypes() {
        return this.types;
    }

    @Override
    public Set<Integer> getAllJdbcReadTypes() {
        return this.jdbcReadTypes;
    }

    @Override
    public ImmutableSet<Class<?>> getTypes() {
        return ImmutableSet.copyOf(this.types);
    }

    @Override
    public int getDefaultJdbcWriteType() {
        return this.defaultJdbcWriteType;
    }

    @Override
    public ImmutableSet<Integer> getJdbcReadTypes() {
        return ImmutableSet.copyOf(this.jdbcReadTypes);
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return false;
    }

    @Override
    public Object validate(Object value) throws IllegalArgumentException {
        if (value != null && !this.isSupportedType(value.getClass())) {
            throw new IllegalArgumentException("Value of class " + value.getClass() + " is not valid for column type " + this.getName());
        }
        return this.validateInternal(value);
    }

    private boolean isSupportedType(Class<?> clazz) {
        for (Class<?> type : this.types) {
            if (!type.isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }

    protected T validateInternal(T value) throws IllegalArgumentException {
        return value;
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException {
        stmt.setObject(index, value, jdbcType);
    }

    @Override
    public T pullFromDatabase(EntityManager manager, ResultSet res, Class<T> type, int columnIndex) throws SQLException {
        return this.pullFromDatabase(manager, res, type, res.getMetaData().getColumnName(columnIndex));
    }

    @Override
    public boolean shouldCache(Class<?> type) {
        return this.shouldStore(type);
    }

    @Override
    public boolean shouldStore(Class<?> type) {
        return true;
    }

    @Override
    public T parse(String input) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot parse a string into type " + this.getName());
    }

    @Override
    public T parseDefault(String input) throws IllegalArgumentException {
        return this.parse(input);
    }

    @Override
    public boolean valueEquals(Object value1, Object value2) {
        return Objects.equals(value1, value2);
    }

    @Override
    public String valueToString(T value) {
        return String.valueOf(value);
    }

    protected static <T> T preserveNull(ResultSet res, T value) throws SQLException {
        return res.wasNull() ? null : (T)value;
    }

    public boolean equals(Object other) {
        return this.getClass() == other.getClass();
    }

    public String toString() {
        return this.getName();
    }
}

