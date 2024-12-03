/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.OgnlException
 *  ognl.OgnlRuntime
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import ognl.OgnlException;
import ognl.OgnlRuntime;

public class OgnlReflectionProvider
implements ReflectionProvider {
    private OgnlUtil ognlUtil;

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    @Override
    public Field getField(Class inClass, String name) {
        return OgnlRuntime.getField((Class)inClass, (String)name);
    }

    @Override
    public Method getGetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getGetMethod(null, (Class)targetClass, (String)propertyName);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public Method getSetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getSetMethod(null, (Class)targetClass, (String)propertyName);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context) {
        this.ognlUtil.setProperties(props, o, context);
    }

    @Override
    public void setProperties(Map<String, ?> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException {
        this.ognlUtil.setProperties(props, o, context, throwPropertyExceptions);
    }

    @Override
    public void setProperties(Map<String, ?> properties, Object o) {
        this.ognlUtil.setProperties(properties, o);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        try {
            return OgnlRuntime.getPropertyDescriptor((Class)targetClass, (String)propertyName);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        this.copy(from, to, context, exclusions, inclusions, null);
    }

    @Override
    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions, Class<?> editable) {
        this.ognlUtil.copy(from, to, context, exclusions, inclusions, editable);
    }

    @Override
    public Object getRealTarget(String property, Map<String, Object> context, Object root) throws ReflectionException {
        try {
            return this.ognlUtil.getRealTarget(property, context, root);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        this.ognlUtil.setProperty(name, value, o, context);
    }

    @Override
    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        this.ognlUtil.setProperty(name, value, o, context, throwPropertyExceptions);
    }

    public Map getBeanMap(Object source) throws IntrospectionException, ReflectionException {
        try {
            return this.ognlUtil.getBeanMap(source);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public Object getValue(String expression, Map<String, Object> context, Object root) throws ReflectionException {
        try {
            return this.ognlUtil.getValue(expression, context, root);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public void setValue(String expression, Map<String, Object> context, Object root, Object value) throws ReflectionException {
        try {
            this.ognlUtil.setValue(expression, context, root, value);
        }
        catch (OgnlException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        return this.ognlUtil.getPropertyDescriptors(source);
    }
}

