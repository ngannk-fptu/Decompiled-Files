/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.NotReadablePropertyException
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core.namedparam;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class BeanPropertySqlParameterSource
extends AbstractSqlParameterSource {
    private final BeanWrapper beanWrapper;
    @Nullable
    private String[] propertyNames;

    public BeanPropertySqlParameterSource(Object object) {
        this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess((Object)object);
    }

    @Override
    public boolean hasValue(String paramName) {
        return this.beanWrapper.isReadableProperty(paramName);
    }

    @Override
    @Nullable
    public Object getValue(String paramName) throws IllegalArgumentException {
        try {
            return this.beanWrapper.getPropertyValue(paramName);
        }
        catch (NotReadablePropertyException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public int getSqlType(String paramName) {
        int sqlType = super.getSqlType(paramName);
        if (sqlType != Integer.MIN_VALUE) {
            return sqlType;
        }
        Class propType = this.beanWrapper.getPropertyType(paramName);
        return StatementCreatorUtils.javaTypeToSqlParameterType(propType);
    }

    @Override
    @NonNull
    public String[] getParameterNames() {
        return this.getReadablePropertyNames();
    }

    public String[] getReadablePropertyNames() {
        if (this.propertyNames == null) {
            PropertyDescriptor[] props;
            ArrayList<String> names = new ArrayList<String>();
            for (PropertyDescriptor pd : props = this.beanWrapper.getPropertyDescriptors()) {
                if (!this.beanWrapper.isReadableProperty(pd.getName())) continue;
                names.add(pd.getName());
            }
            this.propertyNames = StringUtils.toStringArray(names);
        }
        return this.propertyNames;
    }
}

