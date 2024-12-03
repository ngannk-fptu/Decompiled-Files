/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.locale.LocaleBeanUtilsBean;

public class LocaleBeanUtils
extends BeanUtils {
    public static Locale getDefaultLocale() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getDefaultLocale();
    }

    public static void setDefaultLocale(Locale locale) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
    }

    public static boolean getApplyLocalized() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getApplyLocalized();
    }

    public static void setApplyLocalized(boolean newApplyLocalized) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
    }

    public static String getIndexedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
    }

    public static String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
    }

    public static String getIndexedProperty(Object bean, String name, int index, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
    }

    public static String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
    }

    public static String getSimpleProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
    }

    public static String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
    }

    public static String getMappedProperty(Object bean, String name, String key, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key, pattern);
    }

    public static String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key);
    }

    public static String getMappedPropertyLocale(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
    }

    public static String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name);
    }

    public static String getNestedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name, pattern);
    }

    public static String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name);
    }

    public static String getProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name, pattern);
    }

    public static String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
    }

    public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
    }

    public static void setProperty(Object bean, String name, Object value, String pattern) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
    }

    protected static Class<?> definePropertyType(Object target, String name, String propName) throws IllegalAccessException, InvocationTargetException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
    }

    protected static Object convert(Class<?> type, int index, Object value, String pattern) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
    }

    protected static Object convert(Class<?> type, int index, Object value) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value);
    }

    protected static void invokeSetter(Object target, String propName, String key, int index, Object newValue) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
    }

    @Deprecated
    protected static Descriptor calculate(Object bean, String name) throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.Descriptor descriptor = LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().calculate(bean, name);
        return new Descriptor(descriptor.getTarget(), descriptor.getName(), descriptor.getPropName(), descriptor.getKey(), descriptor.getIndex());
    }

    @Deprecated
    protected static class Descriptor {
        private int index = -1;
        private String name;
        private String propName;
        private String key;
        private Object target;

        public Descriptor(Object target, String name, String propName, String key, int index) {
            this.setTarget(target);
            this.setName(name);
            this.setPropName(propName);
            this.setKey(key);
            this.setIndex(index);
        }

        public Object getTarget() {
            return this.target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPropName() {
            return this.propName;
        }

        public void setPropName(String propName) {
            this.propName = propName;
        }
    }
}

