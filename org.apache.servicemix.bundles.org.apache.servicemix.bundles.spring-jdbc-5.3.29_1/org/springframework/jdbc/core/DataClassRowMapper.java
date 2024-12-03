/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.TypeConverter
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DataClassRowMapper<T>
extends BeanPropertyRowMapper<T> {
    @Nullable
    private Constructor<T> mappedConstructor;
    @Nullable
    private String[] constructorParameterNames;
    @Nullable
    private TypeDescriptor[] constructorParameterTypes;

    public DataClassRowMapper() {
    }

    public DataClassRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }

    @Override
    protected void initialize(Class<T> mappedClass) {
        super.initialize(mappedClass);
        this.mappedConstructor = BeanUtils.getResolvableConstructor(mappedClass);
        int paramCount = this.mappedConstructor.getParameterCount();
        if (paramCount > 0) {
            for (String name : this.constructorParameterNames = BeanUtils.getParameterNames(this.mappedConstructor)) {
                this.suppressProperty(name);
            }
            this.constructorParameterTypes = new TypeDescriptor[paramCount];
            for (int i = 0; i < paramCount; ++i) {
                this.constructorParameterTypes[i] = new TypeDescriptor(new MethodParameter(this.mappedConstructor, i));
            }
        }
    }

    @Override
    protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
        Object[] args;
        Assert.state((this.mappedConstructor != null ? 1 : 0) != 0, (String)"Mapped constructor was not initialized");
        if (this.constructorParameterNames != null && this.constructorParameterTypes != null) {
            args = new Object[this.constructorParameterNames.length];
            for (int i = 0; i < args.length; ++i) {
                int index;
                String name = this.constructorParameterNames[i];
                try {
                    index = rs.findColumn(this.lowerCaseName(name));
                }
                catch (SQLException ex) {
                    index = rs.findColumn(this.underscoreName(name));
                }
                TypeDescriptor td = this.constructorParameterTypes[i];
                Object value = this.getColumnValue(rs, index, td.getType());
                args[i] = tc.convertIfNecessary(value, td.getType(), td);
            }
        } else {
            args = new Object[]{};
        }
        return (T)BeanUtils.instantiateClass(this.mappedConstructor, (Object[])args);
    }

    public static <T> DataClassRowMapper<T> newInstance(Class<T> mappedClass) {
        return new DataClassRowMapper<T>(mappedClass);
    }

    public static <T> DataClassRowMapper<T> newInstance(Class<T> mappedClass, @Nullable ConversionService conversionService) {
        DataClassRowMapper<T> rowMapper = DataClassRowMapper.newInstance(mappedClass);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }
}

