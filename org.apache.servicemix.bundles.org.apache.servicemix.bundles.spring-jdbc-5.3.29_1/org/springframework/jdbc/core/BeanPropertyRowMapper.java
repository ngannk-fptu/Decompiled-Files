/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.BeanWrapperImpl
 *  org.springframework.beans.NotWritablePropertyException
 *  org.springframework.beans.TypeConverter
 *  org.springframework.beans.TypeMismatchException
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.dao.DataRetrievalFailureException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class BeanPropertyRowMapper<T>
implements RowMapper<T> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Class<T> mappedClass;
    private boolean checkFullyPopulated = false;
    private boolean primitivesDefaultedForNullValue = false;
    @Nullable
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();
    @Nullable
    private Map<String, PropertyDescriptor> mappedProperties;
    @Nullable
    private Set<String> mappedPropertyNames;

    public BeanPropertyRowMapper() {
    }

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        this.initialize(mappedClass);
    }

    public BeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
        this.initialize(mappedClass);
        this.checkFullyPopulated = checkFullyPopulated;
    }

    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            this.initialize(mappedClass);
        } else if (this.mappedClass != mappedClass) {
            throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " + mappedClass + " since it is already providing mapping for " + this.mappedClass);
        }
    }

    @Nullable
    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    public void setCheckFullyPopulated(boolean checkFullyPopulated) {
        this.checkFullyPopulated = checkFullyPopulated;
    }

    public boolean isCheckFullyPopulated() {
        return this.checkFullyPopulated;
    }

    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    public boolean isPrimitivesDefaultedForNullValue() {
        return this.primitivesDefaultedForNullValue;
    }

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedProperties = new HashMap<String, PropertyDescriptor>();
        this.mappedPropertyNames = new HashSet<String>();
        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(mappedClass)) {
            if (pd.getWriteMethod() == null) continue;
            String lowerCaseName = this.lowerCaseName(pd.getName());
            this.mappedProperties.put(lowerCaseName, pd);
            String underscoreName = this.underscoreName(pd.getName());
            if (!lowerCaseName.equals(underscoreName)) {
                this.mappedProperties.put(underscoreName, pd);
            }
            this.mappedPropertyNames.add(pd.getName());
        }
    }

    protected void suppressProperty(String propertyName) {
        if (this.mappedProperties != null) {
            this.mappedProperties.remove(this.lowerCaseName(propertyName));
            this.mappedProperties.remove(this.underscoreName(propertyName));
        }
    }

    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    protected String underscoreName(String name) {
        if (!StringUtils.hasLength((String)name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.initBeanWrapper((BeanWrapper)bw);
        T mappedObject = this.constructMappedInstance(rs, (TypeConverter)bw);
        bw.setBeanInstance(mappedObject);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        HashSet<String> populatedProperties = this.isCheckFullyPopulated() ? new HashSet<String>() : null;
        for (int index = 1; index <= columnCount; ++index) {
            PropertyDescriptor pd;
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String property = this.lowerCaseName(StringUtils.delete((String)column, (String)" "));
            PropertyDescriptor propertyDescriptor = pd = this.mappedProperties != null ? this.mappedProperties.get(property) : null;
            if (pd == null) continue;
            try {
                Object value = this.getColumnValue(rs, index, pd);
                if (rowNumber == 0 && this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Mapping column '" + column + "' to property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'"));
                }
                try {
                    bw.setPropertyValue(pd.getName(), value);
                }
                catch (TypeMismatchException ex) {
                    if (value == null && this.primitivesDefaultedForNullValue) {
                        if (this.logger.isDebugEnabled()) {
                            String propertyType = ClassUtils.getQualifiedName(pd.getPropertyType());
                            this.logger.debug((Object)String.format("Ignoring intercepted TypeMismatchException for row %d and column '%s' with null value when setting property '%s' of type '%s' on object: %s", rowNumber, column, pd.getName(), propertyType, mappedObject), (Throwable)ex);
                        }
                    }
                    throw ex;
                }
                if (populatedProperties == null) continue;
                populatedProperties.add(pd.getName());
                continue;
            }
            catch (NotWritablePropertyException ex) {
                throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + pd.getName() + "'", (Throwable)ex);
            }
        }
        if (populatedProperties != null && !populatedProperties.equals(this.mappedPropertyNames)) {
            throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all properties necessary to populate object of " + this.mappedClass + ": " + this.mappedPropertyNames);
        }
        return mappedObject;
    }

    protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
        Assert.state((this.mappedClass != null ? 1 : 0) != 0, (String)"Mapped class was not specified");
        return (T)BeanUtils.instantiateClass(this.mappedClass);
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = this.getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }

    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, paramType);
    }

    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        return new BeanPropertyRowMapper<T>(mappedClass);
    }

    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass, @Nullable ConversionService conversionService) {
        BeanPropertyRowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(mappedClass);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }
}

