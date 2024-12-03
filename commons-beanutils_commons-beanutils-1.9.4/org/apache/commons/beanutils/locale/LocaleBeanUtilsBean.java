/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils.locale;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.beanutils.locale.LocaleConvertUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LocaleBeanUtilsBean
extends BeanUtilsBean {
    private static final ContextClassLoaderLocal<LocaleBeanUtilsBean> LOCALE_BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<LocaleBeanUtilsBean>(){

        @Override
        protected LocaleBeanUtilsBean initialValue() {
            return new LocaleBeanUtilsBean();
        }
    };
    private final Log log = LogFactory.getLog(LocaleBeanUtilsBean.class);
    private final LocaleConvertUtilsBean localeConvertUtils;

    public static LocaleBeanUtilsBean getLocaleBeanUtilsInstance() {
        return LOCALE_BEANS_BY_CLASSLOADER.get();
    }

    public static void setInstance(LocaleBeanUtilsBean newInstance) {
        LOCALE_BEANS_BY_CLASSLOADER.set(newInstance);
    }

    public LocaleBeanUtilsBean() {
        this.localeConvertUtils = new LocaleConvertUtilsBean();
    }

    public LocaleBeanUtilsBean(LocaleConvertUtilsBean localeConvertUtils, ConvertUtilsBean convertUtilsBean, PropertyUtilsBean propertyUtilsBean) {
        super(convertUtilsBean, propertyUtilsBean);
        this.localeConvertUtils = localeConvertUtils;
    }

    public LocaleBeanUtilsBean(LocaleConvertUtilsBean localeConvertUtils) {
        this.localeConvertUtils = localeConvertUtils;
    }

    public LocaleConvertUtilsBean getLocaleConvertUtils() {
        return this.localeConvertUtils;
    }

    public Locale getDefaultLocale() {
        return this.getLocaleConvertUtils().getDefaultLocale();
    }

    public void setDefaultLocale(Locale locale) {
        this.getLocaleConvertUtils().setDefaultLocale(locale);
    }

    public boolean getApplyLocalized() {
        return this.getLocaleConvertUtils().getApplyLocalized();
    }

    public void setApplyLocalized(boolean newApplyLocalized) {
        this.getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
    }

    public String getIndexedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getIndexedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getIndexedProperty(bean, name, null);
    }

    public String getIndexedProperty(Object bean, String name, int index, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getIndexedProperty(bean, name, index);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getIndexedProperty(bean, name, index, null);
    }

    public String getSimpleProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getSimpleProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getSimpleProperty(bean, name, null);
    }

    public String getMappedProperty(Object bean, String name, String key, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getMappedProperty(bean, name, key);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getMappedProperty(bean, name, key, null);
    }

    public String getMappedPropertyLocale(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getMappedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getMappedPropertyLocale(bean, name, null);
    }

    public String getNestedProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getNestedProperty(bean, name);
        return this.getLocaleConvertUtils().convert(value, pattern);
    }

    @Override
    public String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name, null);
    }

    public String getProperty(Object bean, String name, String pattern) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name, pattern);
    }

    @Override
    public String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }

    @Override
    public void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        this.setProperty(bean, name, value, null);
    }

    public void setProperty(Object bean, String name, Object value, String pattern) throws IllegalAccessException, InvocationTargetException {
        if (this.log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder("  setProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            } else if (value instanceof String) {
                sb.append((String)value);
            } else if (value instanceof String[]) {
                String[] values = (String[])value;
                sb.append('[');
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            } else {
                sb.append(value.toString());
            }
            sb.append(')');
            this.log.trace((Object)sb.toString());
        }
        Object target = bean;
        Resolver resolver = this.getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = this.getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            }
            catch (NoSuchMethodException e) {
                return;
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    Target bean = " + target));
            this.log.trace((Object)("    Target name = " + name));
        }
        String propName = resolver.getProperty(name);
        int index = resolver.getIndex(name);
        String key = resolver.getKey(name);
        Class<?> type = this.definePropertyType(target, name, propName);
        if (type != null) {
            Object newValue = this.convert(type, index, value, pattern);
            this.invokeSetter(target, propName, key, index, newValue);
        }
    }

    protected Class<?> definePropertyType(Object target, String name, String propName) throws IllegalAccessException, InvocationTargetException {
        Class<?> type = null;
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return null;
            }
            type = dynaProperty.getType();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return null;
                }
            }
            catch (NoSuchMethodException e) {
                return null;
            }
            type = descriptor instanceof MappedPropertyDescriptor ? ((MappedPropertyDescriptor)descriptor).getMappedPropertyType() : (descriptor instanceof IndexedPropertyDescriptor ? ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType() : descriptor.getPropertyType());
        }
        return type;
    }

    protected Object convert(Class<?> type, int index, Object value, String pattern) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Converting value '" + value + "' to type:" + type));
        }
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value instanceof String) {
                String[] values = new String[]{(String)value};
                newValue = this.getLocaleConvertUtils().convert(values, type, pattern);
            } else {
                newValue = value instanceof String[] ? this.getLocaleConvertUtils().convert((String[])value, type, pattern) : value;
            }
        } else {
            newValue = type.isArray() ? (value instanceof String ? this.getLocaleConvertUtils().convert((String)value, type.getComponentType(), pattern) : (value instanceof String[] ? this.getLocaleConvertUtils().convert(((String[])value)[0], type.getComponentType(), pattern) : value)) : (value instanceof String ? this.getLocaleConvertUtils().convert((String)value, type, pattern) : (value instanceof String[] ? this.getLocaleConvertUtils().convert(((String[])value)[0], type, pattern) : value));
        }
        return newValue;
    }

    protected Object convert(Class<?> type, int index, Object value) {
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value instanceof String) {
                String[] values = new String[]{(String)value};
                newValue = ConvertUtils.convert(values, type);
            } else {
                newValue = value instanceof String[] ? ConvertUtils.convert((String[])value, type) : value;
            }
        } else {
            newValue = type.isArray() ? (value instanceof String ? ConvertUtils.convert((String)value, type.getComponentType()) : (value instanceof String[] ? ConvertUtils.convert(((String[])value)[0], type.getComponentType()) : value)) : (value instanceof String ? ConvertUtils.convert((String)value, type) : (value instanceof String[] ? ConvertUtils.convert(((String[])value)[0], type) : value));
        }
        return newValue;
    }

    protected void invokeSetter(Object target, String propName, String key, int index, Object newValue) throws IllegalAccessException, InvocationTargetException {
        try {
            if (index >= 0) {
                this.getPropertyUtils().setIndexedProperty(target, propName, index, newValue);
            } else if (key != null) {
                this.getPropertyUtils().setMappedProperty(target, propName, key, newValue);
            } else {
                this.getPropertyUtils().setProperty(target, propName, newValue);
            }
        }
        catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, "Cannot set " + propName);
        }
    }

    @Deprecated
    protected Descriptor calculate(Object bean, String name) throws IllegalAccessException, InvocationTargetException {
        Object target = bean;
        Resolver resolver = this.getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = this.getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    Target bean = " + target));
            this.log.trace((Object)("    Target name = " + name));
        }
        String propName = resolver.getProperty(name);
        int index = resolver.getIndex(name);
        String key = resolver.getKey(name);
        return new Descriptor(target, name, propName, key, index);
    }

    @Deprecated
    protected class Descriptor {
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

