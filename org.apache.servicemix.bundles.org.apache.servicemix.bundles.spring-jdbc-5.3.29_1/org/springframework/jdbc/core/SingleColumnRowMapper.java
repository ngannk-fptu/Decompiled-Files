/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.dao.TypeMismatchDataAccessException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.NumberUtils
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;

public class SingleColumnRowMapper<T>
implements RowMapper<T> {
    @Nullable
    private Class<?> requiredType;
    @Nullable
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    public SingleColumnRowMapper() {
    }

    public SingleColumnRowMapper(Class<T> requiredType) {
        this.setRequiredType(requiredType);
    }

    public void setRequiredType(Class<T> requiredType) {
        this.requiredType = ClassUtils.resolvePrimitiveIfNecessary(requiredType);
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    @Nullable
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1) {
            throw new IncorrectResultSetColumnCountException(1, nrOfColumns);
        }
        Object result = this.getColumnValue(rs, 1, this.requiredType);
        if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
            try {
                return (T)this.convertValueToRequiredType(result, this.requiredType);
            }
            catch (IllegalArgumentException ex) {
                throw new TypeMismatchDataAccessException("Type mismatch affecting row number " + rowNum + " and column type '" + rsmd.getColumnTypeName(1) + "': " + ex.getMessage());
            }
        }
        return (T)result;
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, @Nullable Class<?> requiredType) throws SQLException {
        if (requiredType != null) {
            return JdbcUtils.getResultSetValue(rs, index, requiredType);
        }
        return this.getColumnValue(rs, index);
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index);
    }

    @Nullable
    protected Object convertValueToRequiredType(Object value, Class<?> requiredType) {
        if (String.class == requiredType) {
            return value.toString();
        }
        if (Number.class.isAssignableFrom(requiredType)) {
            if (value instanceof Number) {
                return NumberUtils.convertNumberToTargetClass((Number)((Number)value), requiredType);
            }
            return NumberUtils.parseNumber((String)value.toString(), requiredType);
        }
        if (this.conversionService != null && this.conversionService.canConvert(value.getClass(), requiredType)) {
            return this.conversionService.convert(value, requiredType);
        }
        throw new IllegalArgumentException("Value [" + value + "] is of type [" + value.getClass().getName() + "] and cannot be converted to required type [" + requiredType.getName() + "]");
    }

    public static <T> SingleColumnRowMapper<T> newInstance(Class<T> requiredType) {
        return new SingleColumnRowMapper<T>(requiredType);
    }

    public static <T> SingleColumnRowMapper<T> newInstance(Class<T> requiredType, @Nullable ConversionService conversionService) {
        SingleColumnRowMapper<T> rowMapper = SingleColumnRowMapper.newInstance(requiredType);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }
}

