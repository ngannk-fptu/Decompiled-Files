/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Transformer
 *  org.apache.commons.collections.keyvalue.AbstractMapEntry
 */
package org.apache.commons.beanutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;

public class BeanMap
extends AbstractMap<Object, Object>
implements Cloneable {
    private transient Object bean;
    private transient HashMap<String, Method> readMethods = new HashMap();
    private transient HashMap<String, Method> writeMethods = new HashMap();
    private transient HashMap<String, Class<? extends Object>> types = new HashMap();
    public static final Object[] NULL_ARGUMENTS = new Object[0];
    private static final Map<Class<? extends Object>, Transformer> typeTransformers = Collections.unmodifiableMap(BeanMap.createTypeTransformers());
    @Deprecated
    public static HashMap defaultTransformers = new HashMap(){

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object key) {
            return typeTransformers.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return typeTransformers.containsValue(value);
        }

        @Override
        public Set entrySet() {
            return typeTransformers.entrySet();
        }

        @Override
        public Object get(Object key) {
            return typeTransformers.get(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Set keySet() {
            return typeTransformers.keySet();
        }

        @Override
        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return typeTransformers.size();
        }

        @Override
        public Collection values() {
            return typeTransformers.values();
        }
    };

    private static Map<Class<? extends Object>, Transformer> createTypeTransformers() {
        HashMap<Class<? extends Object>, Transformer> defaultTransformers = new HashMap<Class<? extends Object>, Transformer>();
        defaultTransformers.put(Boolean.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Boolean.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Character.TYPE, new Transformer(){

            public Object transform(Object input) {
                return new Character(input.toString().charAt(0));
            }
        });
        defaultTransformers.put(Byte.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Byte.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Short.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Short.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Integer.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Integer.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Long.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Long.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Float.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Float.valueOf(input.toString());
            }
        });
        defaultTransformers.put(Double.TYPE, new Transformer(){

            public Object transform(Object input) {
                return Double.valueOf(input.toString());
            }
        });
        return defaultTransformers;
    }

    public BeanMap() {
    }

    public BeanMap(Object bean) {
        this.bean = bean;
        this.initialise();
    }

    @Override
    public String toString() {
        return "BeanMap<" + String.valueOf(this.bean) + ">";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BeanMap newMap = (BeanMap)super.clone();
        if (this.bean == null) {
            return newMap;
        }
        Object newBean = null;
        Class<?> beanClass = this.bean.getClass();
        try {
            newBean = beanClass.newInstance();
        }
        catch (Exception e) {
            CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to instantiate the underlying bean \"" + beanClass.getName() + "\": " + e);
            BeanUtils.initCause(cnse, e);
            throw cnse;
        }
        try {
            newMap.setBean(newBean);
        }
        catch (Exception exception) {
            CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to set bean in the cloned bean map: " + exception);
            BeanUtils.initCause(cnse, exception);
            throw cnse;
        }
        try {
            for (String key : this.readMethods.keySet()) {
                if (this.getWriteMethod((Object)key) == null) continue;
                newMap.put(key, this.get(key));
            }
        }
        catch (Exception exception) {
            CloneNotSupportedException cnse = new CloneNotSupportedException("Unable to copy bean values to cloned bean map: " + exception);
            BeanUtils.initCause(cnse, exception);
            throw cnse;
        }
        return newMap;
    }

    public void putAllWriteable(BeanMap map) {
        for (String key : map.readMethods.keySet()) {
            if (this.getWriteMethod((Object)key) == null) continue;
            this.put(key, map.get(key));
        }
    }

    @Override
    public void clear() {
        if (this.bean == null) {
            return;
        }
        Class<?> beanClass = null;
        try {
            beanClass = this.bean.getClass();
            this.bean = beanClass.newInstance();
        }
        catch (Exception e) {
            UnsupportedOperationException uoe = new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
            BeanUtils.initCause(uoe, e);
            throw uoe;
        }
    }

    @Override
    public boolean containsKey(Object name) {
        Method method = this.getReadMethod(name);
        return method != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    public Object get(Object name) {
        Method method;
        if (this.bean != null && (method = this.getReadMethod(name)) != null) {
            try {
                return method.invoke(this.bean, NULL_ARGUMENTS);
            }
            catch (IllegalAccessException e) {
                this.logWarn(e);
            }
            catch (IllegalArgumentException e) {
                this.logWarn(e);
            }
            catch (InvocationTargetException e) {
                this.logWarn(e);
            }
            catch (NullPointerException e) {
                this.logWarn(e);
            }
        }
        return null;
    }

    @Override
    public Object put(Object name, Object value) throws IllegalArgumentException, ClassCastException {
        if (this.bean != null) {
            Object oldValue = this.get(name);
            Method method = this.getWriteMethod(name);
            if (method == null) {
                throw new IllegalArgumentException("The bean of type: " + this.bean.getClass().getName() + " has no property called: " + name);
            }
            try {
                Object[] arguments = this.createWriteMethodArguments(method, value);
                method.invoke(this.bean, arguments);
                Object newValue = this.get(name);
                this.firePropertyChange(name, oldValue, newValue);
            }
            catch (InvocationTargetException e) {
                IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
                if (!BeanUtils.initCause(iae, e)) {
                    this.logInfo(e);
                }
                throw iae;
            }
            catch (IllegalAccessException e) {
                IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
                if (!BeanUtils.initCause(iae, e)) {
                    this.logInfo(e);
                }
                throw iae;
            }
            return oldValue;
        }
        return null;
    }

    @Override
    public int size() {
        return this.readMethods.size();
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(this.readMethods.keySet());
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableSet(new AbstractSet<Map.Entry<Object, Object>>(){

            @Override
            public Iterator<Map.Entry<Object, Object>> iterator() {
                return BeanMap.this.entryIterator();
            }

            @Override
            public int size() {
                return BeanMap.this.readMethods.size();
            }
        });
    }

    @Override
    public Collection<Object> values() {
        ArrayList<Object> answer = new ArrayList<Object>(this.readMethods.size());
        Iterator<Object> iter = this.valueIterator();
        while (iter.hasNext()) {
            answer.add(iter.next());
        }
        return Collections.unmodifiableList(answer);
    }

    public Class<?> getType(String name) {
        return this.types.get(name);
    }

    public Iterator<String> keyIterator() {
        return this.readMethods.keySet().iterator();
    }

    public Iterator<Object> valueIterator() {
        final Iterator<String> iter = this.keyIterator();
        return new Iterator<Object>(){

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Object next() {
                Object key = iter.next();
                return BeanMap.this.get(key);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported for BeanMap");
            }
        };
    }

    public Iterator<Map.Entry<Object, Object>> entryIterator() {
        final Iterator<String> iter = this.keyIterator();
        return new Iterator<Map.Entry<Object, Object>>(){

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Map.Entry<Object, Object> next() {
                Object key = iter.next();
                Object value = BeanMap.this.get(key);
                Entry tmpEntry = new Entry(BeanMap.this, key, value);
                return tmpEntry;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported for BeanMap");
            }
        };
    }

    public Object getBean() {
        return this.bean;
    }

    public void setBean(Object newBean) {
        this.bean = newBean;
        this.reinitialise();
    }

    public Method getReadMethod(String name) {
        return this.readMethods.get(name);
    }

    public Method getWriteMethod(String name) {
        return this.writeMethods.get(name);
    }

    protected Method getReadMethod(Object name) {
        return this.readMethods.get(name);
    }

    protected Method getWriteMethod(Object name) {
        return this.writeMethods.get(name);
    }

    protected void reinitialise() {
        this.readMethods.clear();
        this.writeMethods.clear();
        this.types.clear();
        this.initialise();
    }

    private void initialise() {
        if (this.getBean() == null) {
            return;
        }
        Class<?> beanClass = this.getBean().getClass();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor == null) continue;
                    String name = propertyDescriptor.getName();
                    Method readMethod = propertyDescriptor.getReadMethod();
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    Class<?> aType = propertyDescriptor.getPropertyType();
                    if (readMethod != null) {
                        this.readMethods.put(name, readMethod);
                    }
                    if (writeMethod != null) {
                        this.writeMethods.put(name, writeMethod);
                    }
                    this.types.put(name, aType);
                }
            }
        }
        catch (IntrospectionException e) {
            this.logWarn(e);
        }
    }

    protected void firePropertyChange(Object key, Object oldValue, Object newValue) {
    }

    protected Object[] createWriteMethodArguments(Method method, Object value) throws IllegalAccessException, ClassCastException {
        try {
            Class<?> paramType;
            Class<?>[] types;
            if (value != null && (types = method.getParameterTypes()) != null && types.length > 0 && !(paramType = types[0]).isAssignableFrom(value.getClass())) {
                value = this.convertType(paramType, value);
            }
            Object[] answer = new Object[]{value};
            return answer;
        }
        catch (InvocationTargetException e) {
            IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
            if (!BeanUtils.initCause(iae, e)) {
                this.logInfo(e);
            }
            throw iae;
        }
        catch (InstantiationException e) {
            IllegalArgumentException iae = new IllegalArgumentException(e.getMessage());
            if (!BeanUtils.initCause(iae, e)) {
                this.logInfo(e);
            }
            BeanUtils.initCause(iae, e);
            throw iae;
        }
    }

    protected Object convertType(Class<?> newType, Object value) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class[] types = new Class[]{value.getClass()};
        try {
            Constructor<?> constructor = newType.getConstructor(types);
            Object[] arguments = new Object[]{value};
            return constructor.newInstance(arguments);
        }
        catch (NoSuchMethodException e) {
            Transformer transformer = this.getTypeTransformer(newType);
            if (transformer != null) {
                return transformer.transform(value);
            }
            return value;
        }
    }

    protected Transformer getTypeTransformer(Class<?> aType) {
        return typeTransformers.get(aType);
    }

    protected void logInfo(Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }

    protected void logWarn(Exception ex) {
        System.out.println("WARN: Exception: " + ex);
        ex.printStackTrace();
    }

    protected static class Entry
    extends AbstractMapEntry {
        private final BeanMap owner;

        protected Entry(BeanMap owner, Object key, Object value) {
            super(key, value);
            this.owner = owner;
        }

        public Object setValue(Object value) {
            Object key = this.getKey();
            Object oldValue = this.owner.get(key);
            this.owner.put(key, value);
            Object newValue = this.owner.get(key);
            super.setValue(newValue);
            return oldValue;
        }
    }
}

