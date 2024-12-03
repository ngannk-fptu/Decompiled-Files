/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BeanUtilsBean {
    private static final ContextClassLoaderLocal<BeanUtilsBean> BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<BeanUtilsBean>(){

        @Override
        protected BeanUtilsBean initialValue() {
            return new BeanUtilsBean();
        }
    };
    private final Log log = LogFactory.getLog(BeanUtils.class);
    private final ConvertUtilsBean convertUtilsBean;
    private final PropertyUtilsBean propertyUtilsBean;
    private static final Method INIT_CAUSE_METHOD = BeanUtilsBean.getInitCauseMethod();

    public static BeanUtilsBean getInstance() {
        return BEANS_BY_CLASSLOADER.get();
    }

    public static void setInstance(BeanUtilsBean newInstance) {
        BEANS_BY_CLASSLOADER.set(newInstance);
    }

    public BeanUtilsBean() {
        this(new ConvertUtilsBean(), new PropertyUtilsBean());
    }

    public BeanUtilsBean(ConvertUtilsBean convertUtilsBean) {
        this(convertUtilsBean, new PropertyUtilsBean());
    }

    public BeanUtilsBean(ConvertUtilsBean convertUtilsBean, PropertyUtilsBean propertyUtilsBean) {
        this.convertUtilsBean = convertUtilsBean;
        this.propertyUtilsBean = propertyUtilsBean;
    }

    public Object cloneBean(Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Cloning bean: " + bean.getClass().getName()));
        }
        DynaBean newBean = null;
        newBean = bean instanceof DynaBean ? ((DynaBean)bean).getDynaClass().newInstance() : (DynaBean)bean.getClass().newInstance();
        this.getPropertyUtils().copyProperties(newBean, bean);
        return newBean;
    }

    public void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("BeanUtils.copyProperties(" + dest + ", " + orig + ")"));
        }
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors;
            for (DynaProperty origDescriptor : origDescriptors = ((DynaBean)orig).getDynaClass().getDynaProperties()) {
                String name = origDescriptor.getName();
                if (!this.getPropertyUtils().isReadable(orig, name) || !this.getPropertyUtils().isWriteable(dest, name)) continue;
                Object value = ((DynaBean)orig).get(name);
                this.copyProperty(dest, name, value);
            }
        } else if (orig instanceof Map) {
            Map propMap = (Map)orig;
            for (Map.Entry entry : propMap.entrySet()) {
                String name = (String)entry.getKey();
                if (!this.getPropertyUtils().isWriteable(dest, name)) continue;
                this.copyProperty(dest, name, entry.getValue());
            }
        } else {
            PropertyDescriptor[] origDescriptors;
            for (PropertyDescriptor origDescriptor : origDescriptors = this.getPropertyUtils().getPropertyDescriptors(orig)) {
                String name = origDescriptor.getName();
                if ("class".equals(name) || !this.getPropertyUtils().isReadable(orig, name) || !this.getPropertyUtils().isWriteable(dest, name)) continue;
                try {
                    Object value = this.getPropertyUtils().getSimpleProperty(orig, name);
                    this.copyProperty(dest, name, value);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
        }
    }

    public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder("  copyProperty(");
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
        Class<?> type = null;
        int index = resolver.getIndex(name);
        String key = resolver.getKey(name);
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return;
            }
            type = BeanUtilsBean.dynaPropertyType(dynaProperty, value);
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return;
                }
            }
            catch (NoSuchMethodException e) {
                return;
            }
            type = descriptor.getPropertyType();
            if (type == null) {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("    target type for property '" + propName + "' is null, so skipping ths setter"));
                }
                return;
            }
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("    target propName=" + propName + ", type=" + type + ", index=" + index + ", key=" + key));
        }
        if (index >= 0) {
            value = this.convertForCopy(value, type.getComponentType());
            try {
                this.getPropertyUtils().setIndexedProperty(target, propName, index, value);
            }
            catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, "Cannot set " + propName);
            }
        }
        if (key != null) {
            try {
                this.getPropertyUtils().setMappedProperty(target, propName, key, value);
            }
            catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, "Cannot set " + propName);
            }
        }
        value = this.convertForCopy(value, type);
        try {
            this.getPropertyUtils().setSimpleProperty(target, propName, value);
        }
        catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, "Cannot set " + propName);
        }
    }

    public Map<String, String> describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            return new HashMap<String, String>();
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Describing bean: " + bean.getClass().getName()));
        }
        HashMap<String, String> description = new HashMap<String, String>();
        if (bean instanceof DynaBean) {
            DynaProperty[] descriptors;
            for (DynaProperty descriptor : descriptors = ((DynaBean)bean).getDynaClass().getDynaProperties()) {
                String name = descriptor.getName();
                description.put(name, this.getProperty(bean, name));
            }
        } else {
            PropertyDescriptor[] descriptors = this.getPropertyUtils().getPropertyDescriptors(bean);
            Class<?> clazz = bean.getClass();
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (this.getPropertyUtils().getReadMethod(clazz, descriptor) == null) continue;
                description.put(name, this.getProperty(bean, name));
            }
        }
        return description;
    }

    public String[] getArrayProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getProperty(bean, name);
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            ArrayList<String> values = new ArrayList<String>();
            for (Object item : (Collection)value) {
                if (item == null) {
                    values.add(null);
                    continue;
                }
                values.add(this.getConvertUtils().convert(item));
            }
            return values.toArray(new String[values.size()]);
        }
        if (value.getClass().isArray()) {
            int n = Array.getLength(value);
            String[] results = new String[n];
            for (int i = 0; i < n; ++i) {
                Object item = Array.get(value, i);
                results[i] = item == null ? null : this.getConvertUtils().convert(item);
            }
            return results;
        }
        String[] results = new String[]{this.getConvertUtils().convert(value)};
        return results;
    }

    public String getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getIndexedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }

    public String getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getIndexedProperty(bean, name, index);
        return this.getConvertUtils().convert(value);
    }

    public String getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getMappedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }

    public String getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getMappedProperty(bean, name, key);
        return this.getConvertUtils().convert(value);
    }

    public String getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getNestedProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }

    public String getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }

    public String getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object value = this.getPropertyUtils().getSimpleProperty(bean, name);
        return this.getConvertUtils().convert(value);
    }

    public void populate(Object bean, Map<String, ? extends Object> properties) throws IllegalAccessException, InvocationTargetException {
        if (bean == null || properties == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("BeanUtils.populate(" + bean + ", " + properties + ")"));
        }
        for (Map.Entry<String, ? extends Object> entry : properties.entrySet()) {
            String name = entry.getKey();
            if (name == null) continue;
            this.setProperty(bean, name, entry.getValue());
        }
    }

    public void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
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
                if (target == null) {
                    return;
                }
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
        Class type = null;
        int index = resolver.getIndex(name);
        String key = resolver.getKey(name);
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean)target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return;
            }
            type = BeanUtilsBean.dynaPropertyType(dynaProperty, value);
            if (index >= 0 && List.class.isAssignableFrom(type)) {
                type = Object.class;
            }
        } else if (target instanceof Map) {
            type = Object.class;
        } else if (target != null && target.getClass().isArray() && index >= 0) {
            type = Array.get(target, index).getClass();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor = this.getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return;
                }
            }
            catch (NoSuchMethodException e) {
                return;
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                if (((MappedPropertyDescriptor)descriptor).getMappedWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = ((MappedPropertyDescriptor)descriptor).getMappedPropertyType();
            } else if (index >= 0 && descriptor instanceof IndexedPropertyDescriptor) {
                if (((IndexedPropertyDescriptor)descriptor).getIndexedWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType();
            } else if (index >= 0 && List.class.isAssignableFrom(descriptor.getPropertyType())) {
                type = Object.class;
            } else if (key != null) {
                if (descriptor.getReadMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = value == null ? Object.class : value.getClass();
            } else {
                if (descriptor.getWriteMethod() == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"Skipping read-only property");
                    }
                    return;
                }
                type = descriptor.getPropertyType();
            }
        }
        Object newValue = null;
        if (type.isArray() && index < 0) {
            if (value == null) {
                String[] values = new String[]{null};
                newValue = this.getConvertUtils().convert(values, type);
            } else {
                newValue = value instanceof String ? this.getConvertUtils().convert(value, type) : (value instanceof String[] ? this.getConvertUtils().convert((String[])value, type) : this.convert(value, type));
            }
        } else {
            newValue = type.isArray() ? (value instanceof String || value == null ? this.getConvertUtils().convert((String)value, type.getComponentType()) : (value instanceof String[] ? this.getConvertUtils().convert(((String[])value)[0], type.getComponentType()) : this.convert(value, type.getComponentType()))) : (value instanceof String ? this.getConvertUtils().convert((String)value, type) : (value instanceof String[] ? this.getConvertUtils().convert(((String[])value)[0], type) : this.convert(value, type)));
        }
        try {
            this.getPropertyUtils().setProperty(target, name, newValue);
        }
        catch (NoSuchMethodException e) {
            throw new InvocationTargetException(e, "Cannot set " + propName);
        }
    }

    public ConvertUtilsBean getConvertUtils() {
        return this.convertUtilsBean;
    }

    public PropertyUtilsBean getPropertyUtils() {
        return this.propertyUtilsBean;
    }

    public boolean initCause(Throwable throwable, Throwable cause) {
        if (INIT_CAUSE_METHOD != null && cause != null) {
            try {
                INIT_CAUSE_METHOD.invoke((Object)throwable, cause);
                return true;
            }
            catch (Throwable e) {
                return false;
            }
        }
        return false;
    }

    protected Object convert(Object value, Class<?> type) {
        Converter converter = this.getConvertUtils().lookup(type);
        if (converter != null) {
            this.log.trace((Object)("        USING CONVERTER " + converter));
            return converter.convert(type, value);
        }
        return value;
    }

    private Object convertForCopy(Object value, Class<?> type) {
        return value != null ? this.convert(value, type) : value;
    }

    private static Method getInitCauseMethod() {
        try {
            Class[] paramsClasses = new Class[]{Throwable.class};
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (NoSuchMethodException e) {
            Log log = LogFactory.getLog(BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn((Object)"Throwable does not have initCause() method in JDK 1.3");
            }
            return null;
        }
        catch (Throwable e) {
            Log log = LogFactory.getLog(BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn((Object)"Error getting the Throwable initCause() method", e);
            }
            return null;
        }
    }

    private static Class<?> dynaPropertyType(DynaProperty dynaProperty, Object value) {
        if (!dynaProperty.isMapped()) {
            return dynaProperty.getType();
        }
        return value == null ? String.class : value.getClass();
    }
}

