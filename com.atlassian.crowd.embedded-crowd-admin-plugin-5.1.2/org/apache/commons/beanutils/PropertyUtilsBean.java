/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.FastHashMap
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.beanutils.BeanIntrospectionData;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DefaultBeanIntrospector;
import org.apache.commons.beanutils.DefaultIntrospectionContext;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;
import org.apache.commons.beanutils.WeakFastHashMap;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyUtilsBean {
    private Resolver resolver = new DefaultResolver();
    private WeakFastHashMap<Class<?>, BeanIntrospectionData> descriptorsCache = null;
    private WeakFastHashMap<Class<?>, FastHashMap> mappedDescriptorsCache = null;
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private final Log log = LogFactory.getLog(PropertyUtils.class);
    private final List<BeanIntrospector> introspectors;

    protected static PropertyUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }

    public PropertyUtilsBean() {
        this.descriptorsCache = new WeakFastHashMap();
        this.descriptorsCache.setFast(true);
        this.mappedDescriptorsCache = new WeakFastHashMap();
        this.mappedDescriptorsCache.setFast(true);
        this.introspectors = new CopyOnWriteArrayList<BeanIntrospector>();
        this.resetBeanIntrospectors();
    }

    public Resolver getResolver() {
        return this.resolver;
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver == null ? new DefaultResolver() : resolver;
    }

    public final void resetBeanIntrospectors() {
        this.introspectors.clear();
        this.introspectors.add(DefaultBeanIntrospector.INSTANCE);
        this.introspectors.add(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
    }

    public void addBeanIntrospector(BeanIntrospector introspector) {
        if (introspector == null) {
            throw new IllegalArgumentException("BeanIntrospector must not be null!");
        }
        this.introspectors.add(introspector);
    }

    public boolean removeBeanIntrospector(BeanIntrospector introspector) {
        return this.introspectors.remove(introspector);
    }

    public void clearDescriptors() {
        this.descriptorsCache.clear();
        this.mappedDescriptorsCache.clear();
        Introspector.flushCaches();
    }

    public void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors;
            for (DynaProperty origDescriptor : origDescriptors = ((DynaBean)orig).getDynaClass().getDynaProperties()) {
                String name = origDescriptor.getName();
                if (!this.isReadable(orig, name) || !this.isWriteable(dest, name)) continue;
                try {
                    Object value = ((DynaBean)orig).get(name);
                    if (dest instanceof DynaBean) {
                        ((DynaBean)dest).set(name, value);
                        continue;
                    }
                    this.setSimpleProperty(dest, name, value);
                }
                catch (NoSuchMethodException e) {
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug((Object)("Error writing to '" + name + "' on class '" + dest.getClass() + "'"), (Throwable)e);
                }
            }
        } else if (orig instanceof Map) {
            for (Map.Entry entry : ((Map)orig).entrySet()) {
                String name = (String)entry.getKey();
                if (!this.isWriteable(dest, name)) continue;
                try {
                    if (dest instanceof DynaBean) {
                        ((DynaBean)dest).set(name, entry.getValue());
                        continue;
                    }
                    this.setSimpleProperty(dest, name, entry.getValue());
                }
                catch (NoSuchMethodException e) {
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug((Object)("Error writing to '" + name + "' on class '" + dest.getClass() + "'"), (Throwable)e);
                }
            }
        } else {
            PropertyDescriptor[] origDescriptors;
            for (PropertyDescriptor origDescriptor : origDescriptors = this.getPropertyDescriptors(orig)) {
                String name = origDescriptor.getName();
                if (!this.isReadable(orig, name) || !this.isWriteable(dest, name)) continue;
                try {
                    Object value = this.getSimpleProperty(orig, name);
                    if (dest instanceof DynaBean) {
                        ((DynaBean)dest).set(name, value);
                        continue;
                    }
                    this.setSimpleProperty(dest, name, value);
                }
                catch (NoSuchMethodException e) {
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug((Object)("Error writing to '" + name + "' on class '" + dest.getClass() + "'"), (Throwable)e);
                }
            }
        }
    }

    public Map<String, Object> describe(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        HashMap<String, Object> description = new HashMap<String, Object>();
        if (bean instanceof DynaBean) {
            DynaProperty[] descriptors;
            for (DynaProperty descriptor : descriptors = ((DynaBean)bean).getDynaClass().getDynaProperties()) {
                String name = descriptor.getName();
                description.put(name, this.getProperty(bean, name));
            }
        } else {
            PropertyDescriptor[] descriptors;
            for (PropertyDescriptor descriptor : descriptors = this.getPropertyDescriptors(bean)) {
                String name = descriptor.getName();
                if (descriptor.getReadMethod() == null) continue;
                description.put(name, this.getProperty(bean, name));
            }
        }
        return description;
    }

    public Object getIndexedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        int index = -1;
        try {
            index = this.resolver.getIndex(name);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        return this.getIndexedProperty(bean, name, index);
    }

    public Object getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method readMethod;
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                return Array.get(bean, index);
            }
            if (bean instanceof List) {
                return ((List)bean).get(index);
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            return ((DynaBean)bean).get(name, index);
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (descriptor instanceof IndexedPropertyDescriptor) {
            readMethod = ((IndexedPropertyDescriptor)descriptor).getIndexedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod != null) {
                Object[] subscript = new Object[]{new Integer(index)};
                try {
                    return this.invokeMethod(readMethod, bean, subscript);
                }
                catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof IndexOutOfBoundsException) {
                        throw (IndexOutOfBoundsException)e.getTargetException();
                    }
                    throw e;
                }
            }
        }
        if ((readMethod = this.getReadMethod(bean.getClass(), descriptor)) == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no getter method on bean class '" + bean.getClass() + "'");
        }
        Object value = this.invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!value.getClass().isArray()) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Property '" + name + "' is not indexed on bean class '" + bean.getClass() + "'");
            }
            return ((List)value).get(index);
        }
        try {
            return Array.get(value, index);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + Array.getLength(value) + " for property '" + name + "'");
        }
    }

    public Object getMappedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        String key = null;
        try {
            key = this.resolver.getKey(name);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        return this.getMappedProperty(bean, name, key);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Object getMappedProperty(Object bean, String name, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" + name + "' on bean class " + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor != null) return ((DynaBean)bean).get(name, key);
            throw new NoSuchMethodException("Unknown property '" + name + "'+ on bean class '" + bean.getClass() + "'");
        }
        Object result = null;
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "'+ on bean class '" + bean.getClass() + "'");
        }
        if (descriptor instanceof MappedPropertyDescriptor) {
            Method readMethod = ((MappedPropertyDescriptor)descriptor).getMappedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod == null) throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
            Object[] keyArray = new Object[]{key};
            return this.invokeMethod(readMethod, bean, keyArray);
        }
        Method readMethod = this.getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
        Object invokeResult = this.invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!(invokeResult instanceof Map)) return result;
        return ((Map)invokeResult).get(key);
    }

    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(Class<?> beanClass) {
        if (beanClass == null) {
            return null;
        }
        return this.mappedDescriptorsCache.get(beanClass);
    }

    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(Object bean) {
        if (bean == null) {
            return null;
        }
        return this.getMappedPropertyDescriptors(bean.getClass());
    }

    public Object getNestedProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = null;
            nestedBean = bean instanceof Map ? this.getPropertyOfMapBean((Map)bean, next) : (this.resolver.isMapped(next) ? this.getMappedProperty(bean, next) : (this.resolver.isIndexed(next) ? this.getIndexedProperty(bean, next) : this.getSimpleProperty(bean, next)));
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        bean = bean instanceof Map ? this.getPropertyOfMapBean((Map)bean, name) : (this.resolver.isMapped(name) ? this.getMappedProperty(bean, name) : (this.resolver.isIndexed(name) ? this.getIndexedProperty(bean, name) : this.getSimpleProperty(bean, name)));
        return bean;
    }

    protected Object getPropertyOfMapBean(Map<?, ?> bean, String propertyName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String name;
        if (this.resolver.isMapped(propertyName) && ((name = this.resolver.getProperty(propertyName)) == null || name.length() == 0)) {
            propertyName = this.resolver.getKey(propertyName);
        }
        if (this.resolver.isIndexed(propertyName) || this.resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException("Indexed or mapped properties are not supported on objects of type Map: " + propertyName);
        }
        return bean.get(propertyName);
    }

    public Object getProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return this.getNestedProperty(bean, name);
    }

    public PropertyDescriptor getPropertyDescriptor(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = this.getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        if ((name = this.resolver.getProperty(name)) == null) {
            return null;
        }
        BeanIntrospectionData data = this.getIntrospectionData(bean.getClass());
        PropertyDescriptor result = data.getDescriptor(name);
        if (result != null) {
            return result;
        }
        FastHashMap mappedDescriptors = this.getMappedPropertyDescriptors(bean);
        if (mappedDescriptors == null) {
            mappedDescriptors = new FastHashMap();
            mappedDescriptors.setFast(true);
            this.mappedDescriptorsCache.put(bean.getClass(), mappedDescriptors);
        }
        if ((result = (PropertyDescriptor)mappedDescriptors.get((Object)name)) == null) {
            try {
                result = new MappedPropertyDescriptor(name, bean.getClass());
            }
            catch (IntrospectionException introspectionException) {
                // empty catch block
            }
            if (result != null) {
                mappedDescriptors.put((Object)name, (Object)result);
            }
        }
        return result;
    }

    public PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        return this.getIntrospectionData(beanClass).getDescriptors();
    }

    public PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        return this.getPropertyDescriptors(bean.getClass());
    }

    public Class<?> getPropertyEditorClass(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor != null) {
            return descriptor.getPropertyEditorClass();
        }
        return null;
    }

    public Class<?> getPropertyType(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object descriptor;
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = this.getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof DynaBean) {
            descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                return null;
            }
            Class<?> type = ((DynaProperty)descriptor).getType();
            if (type == null) {
                return null;
            }
            if (type.isArray()) {
                return type.getComponentType();
            }
            return type;
        }
        descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            return null;
        }
        if (descriptor instanceof IndexedPropertyDescriptor) {
            return ((IndexedPropertyDescriptor)descriptor).getIndexedPropertyType();
        }
        if (descriptor instanceof MappedPropertyDescriptor) {
            return ((MappedPropertyDescriptor)descriptor).getMappedPropertyType();
        }
        return ((PropertyDescriptor)descriptor).getPropertyType();
    }

    public Method getReadMethod(PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(descriptor.getReadMethod());
    }

    Method getReadMethod(Class<?> clazz, PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod());
    }

    public Object getSimpleProperty(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.hasNested(name)) {
            throw new IllegalArgumentException("Nested property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isIndexed(name)) {
            throw new IllegalArgumentException("Indexed property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isMapped(name)) {
            throw new IllegalArgumentException("Mapped property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on dynaclass '" + ((DynaBean)bean).getDynaClass() + "'");
            }
            return ((DynaBean)bean).get(name);
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "' on class '" + bean.getClass() + "'");
        }
        Method readMethod = this.getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no getter method in class '" + bean.getClass() + "'");
        }
        Object value = this.invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        return value;
    }

    public Method getWriteMethod(PropertyDescriptor descriptor) {
        return MethodUtils.getAccessibleMethod(descriptor.getWriteMethod());
    }

    public Method getWriteMethod(Class<?> clazz, PropertyDescriptor descriptor) {
        BeanIntrospectionData data = this.getIntrospectionData(clazz);
        return MethodUtils.getAccessibleMethod(clazz, data.getWriteMethod(clazz, descriptor));
    }

    public boolean isReadable(Object bean, String name) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = this.getProperty(bean, next);
            }
            catch (IllegalAccessException e) {
                return false;
            }
            catch (InvocationTargetException e) {
                return false;
            }
            catch (NoSuchMethodException e) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }
        if (bean instanceof DynaBean) {
            return ((DynaBean)bean).getDynaClass().getDynaProperty(name) != null;
        }
        try {
            PropertyDescriptor desc = this.getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method readMethod = this.getReadMethod(bean.getClass(), desc);
                if (readMethod == null) {
                    if (desc instanceof IndexedPropertyDescriptor) {
                        readMethod = ((IndexedPropertyDescriptor)desc).getIndexedReadMethod();
                    } else if (desc instanceof MappedPropertyDescriptor) {
                        readMethod = ((MappedPropertyDescriptor)desc).getMappedReadMethod();
                    }
                    readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                }
                return readMethod != null;
            }
            return false;
        }
        catch (IllegalAccessException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            return false;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    public boolean isWriteable(Object bean, String name) {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = this.getProperty(bean, next);
            }
            catch (IllegalAccessException e) {
                return false;
            }
            catch (InvocationTargetException e) {
                return false;
            }
            catch (NoSuchMethodException e) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + next + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        name = this.resolver.getProperty(name);
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }
        if (bean instanceof DynaBean) {
            return ((DynaBean)bean).getDynaClass().getDynaProperty(name) != null;
        }
        try {
            PropertyDescriptor desc = this.getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method writeMethod = this.getWriteMethod(bean.getClass(), desc);
                if (writeMethod == null) {
                    if (desc instanceof IndexedPropertyDescriptor) {
                        writeMethod = ((IndexedPropertyDescriptor)desc).getIndexedWriteMethod();
                    } else if (desc instanceof MappedPropertyDescriptor) {
                        writeMethod = ((MappedPropertyDescriptor)desc).getMappedWriteMethod();
                    }
                    writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                }
                return writeMethod != null;
            }
            return false;
        }
        catch (IllegalAccessException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            return false;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    public void setIndexedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        int index = -1;
        try {
            index = this.resolver.getIndex(name);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        this.setIndexedProperty(bean, name, index, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void setIndexedProperty(Object bean, String name, int index, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method readMethod;
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                Array.set(bean, index, value);
                return;
            }
            if (bean instanceof List) {
                List<Object> list = PropertyUtilsBean.toObjectList(bean);
                list.set(index, value);
                return;
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean)bean).set(name, index, value);
            return;
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method writeMethod = ((IndexedPropertyDescriptor)descriptor).getIndexedWriteMethod();
            writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
            if (writeMethod != null) {
                Object[] subscript = new Object[]{new Integer(index), value};
                try {
                    if (this.log.isTraceEnabled()) {
                        String valueClassName = value == null ? "<null>" : value.getClass().getName();
                        this.log.trace((Object)("setSimpleProperty: Invoking method " + writeMethod + " with index=" + index + ", value=" + value + " (class " + valueClassName + ")"));
                    }
                    this.invokeMethod(writeMethod, bean, subscript);
                    return;
                }
                catch (InvocationTargetException e) {
                    if (!(e.getTargetException() instanceof IndexOutOfBoundsException)) throw e;
                    throw (IndexOutOfBoundsException)e.getTargetException();
                }
            }
        }
        if ((readMethod = this.getReadMethod(bean.getClass(), descriptor)) == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no getter method on bean class '" + bean.getClass() + "'");
        }
        Object array = this.invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!array.getClass().isArray()) {
            if (!(array instanceof List)) throw new IllegalArgumentException("Property '" + name + "' is not indexed on bean class '" + bean.getClass() + "'");
            List<Object> list = PropertyUtilsBean.toObjectList(array);
            list.set(index, value);
            return;
        } else {
            Array.set(array, index, value);
        }
    }

    public void setMappedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        String key = null;
        try {
            key = this.resolver.getKey(name);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        name = this.resolver.getProperty(name);
        this.setMappedProperty(bean, name, key, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void setMappedProperty(Object bean, String name, String key, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean)bean).set(name, key, value);
            return;
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (descriptor instanceof MappedPropertyDescriptor) {
            Method mappedWriteMethod = ((MappedPropertyDescriptor)descriptor).getMappedWriteMethod();
            mappedWriteMethod = MethodUtils.getAccessibleMethod(bean.getClass(), mappedWriteMethod);
            if (mappedWriteMethod == null) throw new NoSuchMethodException("Property '" + name + "' has no mapped setter methodon bean class '" + bean.getClass() + "'");
            Object[] params = new Object[]{key, value};
            if (this.log.isTraceEnabled()) {
                String valueClassName = value == null ? "<null>" : value.getClass().getName();
                this.log.trace((Object)("setSimpleProperty: Invoking method " + mappedWriteMethod + " with key=" + key + ", value=" + value + " (class " + valueClassName + ")"));
            }
            this.invokeMethod(mappedWriteMethod, bean, params);
            return;
        } else {
            Method readMethod = this.getReadMethod(bean.getClass(), descriptor);
            if (readMethod == null) throw new NoSuchMethodException("Property '" + name + "' has no mapped getter method on bean class '" + bean.getClass() + "'");
            Object invokeResult = this.invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
            if (!(invokeResult instanceof Map)) return;
            Map<String, Object> map = PropertyUtilsBean.toPropertyMap(invokeResult);
            map.put(key, value);
        }
    }

    public void setNestedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        while (this.resolver.hasNested(name)) {
            String next = this.resolver.next(name);
            Object nestedBean = null;
            nestedBean = bean instanceof Map ? this.getPropertyOfMapBean((Map)bean, next) : (this.resolver.isMapped(next) ? this.getMappedProperty(bean, next) : (this.resolver.isIndexed(next) ? this.getIndexedProperty(bean, next) : this.getSimpleProperty(bean, next)));
            if (nestedBean == null) {
                throw new NestedNullException("Null property value for '" + name + "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = this.resolver.remove(name);
        }
        if (bean instanceof Map) {
            this.setPropertyOfMapBean(PropertyUtilsBean.toPropertyMap(bean), name, value);
        } else if (this.resolver.isMapped(name)) {
            this.setMappedProperty(bean, name, value);
        } else if (this.resolver.isIndexed(name)) {
            this.setIndexedProperty(bean, name, value);
        } else {
            this.setSimpleProperty(bean, name, value);
        }
    }

    protected void setPropertyOfMapBean(Map<String, Object> bean, String propertyName, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String name;
        if (this.resolver.isMapped(propertyName) && ((name = this.resolver.getProperty(propertyName)) == null || name.length() == 0)) {
            propertyName = this.resolver.getKey(propertyName);
        }
        if (this.resolver.isIndexed(propertyName) || this.resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException("Indexed or mapped properties are not supported on objects of type Map: " + propertyName);
        }
        bean.put(propertyName, value);
    }

    public void setProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this.setNestedProperty(bean, name, value);
    }

    public void setSimpleProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.hasNested(name)) {
            throw new IllegalArgumentException("Nested property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isIndexed(name)) {
            throw new IllegalArgumentException("Indexed property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (this.resolver.isMapped(name)) {
            throw new IllegalArgumentException("Mapped property names are not allowed: Property '" + name + "' on bean class '" + bean.getClass() + "'");
        }
        if (bean instanceof DynaBean) {
            DynaProperty descriptor = ((DynaBean)bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" + name + "' on dynaclass '" + ((DynaBean)bean).getDynaClass() + "'");
            }
            ((DynaBean)bean).set(name, value);
            return;
        }
        PropertyDescriptor descriptor = this.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" + name + "' on class '" + bean.getClass() + "'");
        }
        Method writeMethod = this.getWriteMethod(bean.getClass(), descriptor);
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no setter method in class '" + bean.getClass() + "'");
        }
        Object[] values = new Object[]{value};
        if (this.log.isTraceEnabled()) {
            String valueClassName = value == null ? "<null>" : value.getClass().getName();
            this.log.trace((Object)("setSimpleProperty: Invoking method " + writeMethod + " with value " + value + " (class " + valueClassName + ")"));
        }
        this.invokeMethod(writeMethod, bean, values);
    }

    private Object invokeMethod(Method method, Object bean, Object[] values) throws IllegalAccessException, InvocationTargetException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified - this should have been checked before reaching this method");
        }
        try {
            return method.invoke(bean, values);
        }
        catch (NullPointerException cause) {
            IllegalArgumentException e;
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        valueString = valueString + ", ";
                    }
                    valueString = values[i] == null ? valueString + "<null>" : valueString + values[i].getClass().getName();
                }
            }
            String expectedString = "";
            Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; ++i) {
                    if (i > 0) {
                        expectedString = expectedString + ", ";
                    }
                    expectedString = expectedString + parTypes[i].getName();
                }
            }
            if (!BeanUtils.initCause(e = new IllegalArgumentException("Cannot invoke " + method.getDeclaringClass().getName() + "." + method.getName() + " on bean class '" + bean.getClass() + "' - " + cause.getMessage() + " - had objects of type \"" + valueString + "\" but expected signature \"" + expectedString + "\""), cause)) {
                this.log.error((Object)"Method invocation failed", (Throwable)cause);
            }
            throw e;
        }
        catch (IllegalArgumentException cause) {
            IllegalArgumentException e;
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        valueString = valueString + ", ";
                    }
                    valueString = values[i] == null ? valueString + "<null>" : valueString + values[i].getClass().getName();
                }
            }
            String expectedString = "";
            Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; ++i) {
                    if (i > 0) {
                        expectedString = expectedString + ", ";
                    }
                    expectedString = expectedString + parTypes[i].getName();
                }
            }
            if (!BeanUtils.initCause(e = new IllegalArgumentException("Cannot invoke " + method.getDeclaringClass().getName() + "." + method.getName() + " on bean class '" + bean.getClass() + "' - " + cause.getMessage() + " - had objects of type \"" + valueString + "\" but expected signature \"" + expectedString + "\""), cause)) {
                this.log.error((Object)"Method invocation failed", (Throwable)cause);
            }
            throw e;
        }
    }

    private BeanIntrospectionData getIntrospectionData(Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("No bean class specified");
        }
        BeanIntrospectionData data = this.descriptorsCache.get(beanClass);
        if (data == null) {
            data = this.fetchIntrospectionData(beanClass);
            this.descriptorsCache.put(beanClass, data);
        }
        return data;
    }

    private BeanIntrospectionData fetchIntrospectionData(Class<?> beanClass) {
        DefaultIntrospectionContext ictx = new DefaultIntrospectionContext(beanClass);
        for (BeanIntrospector bi : this.introspectors) {
            try {
                bi.introspect(ictx);
            }
            catch (IntrospectionException iex) {
                this.log.error((Object)"Exception during introspection", (Throwable)iex);
            }
        }
        return new BeanIntrospectionData(ictx.getPropertyDescriptors());
    }

    private static List<Object> toObjectList(Object obj) {
        List list = (List)obj;
        return list;
    }

    private static Map<String, Object> toPropertyMap(Object obj) {
        Map map = (Map)obj;
        return map;
    }
}

