/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 */
package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.FastHashMap;

public class PropertyUtils {
    @Deprecated
    public static final char INDEXED_DELIM = '[';
    @Deprecated
    public static final char INDEXED_DELIM2 = ']';
    @Deprecated
    public static final char MAPPED_DELIM = '(';
    @Deprecated
    public static final char MAPPED_DELIM2 = ')';
    @Deprecated
    public static final char NESTED_DELIM = '.';
    @Deprecated
    private static int debug = 0;

    @Deprecated
    public static int getDebug() {
        return debug;
    }

    @Deprecated
    public static void setDebug(int newDebug) {
        debug = newDebug;
    }

    public static void clearDescriptors() {
        PropertyUtilsBean.getInstance().clearDescriptors();
    }

    public static void resetBeanIntrospectors() {
        PropertyUtilsBean.getInstance().resetBeanIntrospectors();
    }

    public static void addBeanIntrospector(BeanIntrospector introspector) {
        PropertyUtilsBean.getInstance().addBeanIntrospector(introspector);
    }

    public static boolean removeBeanIntrospector(BeanIntrospector introspector) {
        return PropertyUtilsBean.getInstance().removeBeanIntrospector(introspector);
    }

    public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().copyProperties(dest, orig);
    }

    public static Map<String, Object> describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().describe(bean);
    }

    public static Object getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getIndexedProperty(bean, name);
    }

    public static Object getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getIndexedProperty(bean, name, index);
    }

    public static Object getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getMappedProperty(bean, name);
    }

    public static Object getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getMappedProperty(bean, name, key);
    }

    @Deprecated
    public static FastHashMap getMappedPropertyDescriptors(Class<?> beanClass) {
        return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(beanClass);
    }

    @Deprecated
    public static FastHashMap getMappedPropertyDescriptors(Object bean) {
        return PropertyUtilsBean.getInstance().getMappedPropertyDescriptors(bean);
    }

    public static Object getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getNestedProperty(bean, name);
    }

    public static Object getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getProperty(bean, name);
    }

    public static PropertyDescriptor getPropertyDescriptor(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyDescriptor(bean, name);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        return PropertyUtilsBean.getInstance().getPropertyDescriptors(beanClass);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        return PropertyUtilsBean.getInstance().getPropertyDescriptors(bean);
    }

    public static Class<?> getPropertyEditorClass(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyEditorClass(bean, name);
    }

    public static Class<?> getPropertyType(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getPropertyType(bean, name);
    }

    public static Method getReadMethod(PropertyDescriptor descriptor) {
        return PropertyUtilsBean.getInstance().getReadMethod(descriptor);
    }

    public static Object getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PropertyUtilsBean.getInstance().getSimpleProperty(bean, name);
    }

    public static Method getWriteMethod(PropertyDescriptor descriptor) {
        return PropertyUtilsBean.getInstance().getWriteMethod(descriptor);
    }

    public static boolean isReadable(Object bean, String name) {
        return PropertyUtilsBean.getInstance().isReadable(bean, name);
    }

    public static boolean isWriteable(Object bean, String name) {
        return PropertyUtilsBean.getInstance().isWriteable(bean, name);
    }

    public static void setIndexedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, value);
    }

    public static void setIndexedProperty(Object bean, String name, int index, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setIndexedProperty(bean, name, index, value);
    }

    public static void setMappedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, value);
    }

    public static void setMappedProperty(Object bean, String name, String key, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setMappedProperty(bean, name, key, value);
    }

    public static void setNestedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setNestedProperty(bean, name, value);
    }

    public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setProperty(bean, name, value);
    }

    public static void setSimpleProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtilsBean.getInstance().setSimpleProperty(bean, name, value);
    }
}

