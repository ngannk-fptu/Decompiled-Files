/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.primitives.Primitives
 */
package com.querydsl.core.util;

import com.google.common.base.Function;
import com.google.common.primitives.Primitives;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanMap
extends AbstractMap<String, Object>
implements Cloneable {
    private transient Object bean;
    private transient Map<String, Method> readMethods = new HashMap<String, Method>();
    private transient Map<String, Method> writeMethods = new HashMap<String, Method>();
    private transient Map<String, Class<?>> types = new HashMap();
    private static final Object[] NULL_ARGUMENTS = new Object[0];
    private static final Map<Class<?>, Function<?, ?>> defaultFunctions = new HashMap();

    public BeanMap() {
    }

    public BeanMap(Object bean) {
        this.bean = bean;
        this.initialise();
    }

    @Override
    public String toString() {
        return "BeanMap<" + this.bean + ">";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BeanMap newMap = (BeanMap)super.clone();
        if (this.bean == null) {
            return newMap;
        }
        Object newBean = null;
        Class<?> beanClass = null;
        try {
            beanClass = this.bean.getClass();
            newBean = beanClass.newInstance();
        }
        catch (Exception e) {
            throw new CloneNotSupportedException("Unable to instantiate the underlying bean \"" + beanClass.getName() + "\": " + e);
        }
        try {
            newMap.setBean(newBean);
        }
        catch (Exception exception) {
            throw new CloneNotSupportedException("Unable to set bean in the cloned bean map: " + exception);
        }
        try {
            for (String key : this.readMethods.keySet()) {
                if (this.getWriteMethod(key) == null) continue;
                newMap.put(key, this.get(key));
            }
        }
        catch (Exception exception) {
            throw new CloneNotSupportedException("Unable to copy bean values to cloned bean map: " + exception);
        }
        return newMap;
    }

    public void putAllWriteable(BeanMap map) {
        for (String key : map.readMethods.keySet()) {
            if (this.getWriteMethod(key) == null) continue;
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
            throw new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
        }
    }

    public boolean containsKey(String name) {
        Method method = this.getReadMethod(name);
        return method != null;
    }

    public Object get(String name) {
        Method method;
        if (this.bean != null && (method = this.getReadMethod(name)) != null) {
            try {
                return method.invoke(this.bean, NULL_ARGUMENTS);
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public Object put(String name, Object value) {
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
                throw new IllegalArgumentException(e.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage());
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
    public Set<String> keySet() {
        return this.readMethods.keySet();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new AbstractSet<Map.Entry<String, Object>>(){

            @Override
            public Iterator<Map.Entry<String, Object>> iterator() {
                return BeanMap.this.entryIterator();
            }

            @Override
            public int size() {
                return BeanMap.this.readMethods.size();
            }
        };
    }

    @Override
    public Collection<Object> values() {
        ArrayList<Object> answer = new ArrayList<Object>(this.readMethods.size());
        Iterator<Object> iter = this.valueIterator();
        while (iter.hasNext()) {
            answer.add(iter.next());
        }
        return answer;
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

    public Iterator<Map.Entry<String, Object>> entryIterator() {
        final Iterator<String> iter = this.keyIterator();
        return new Iterator<Map.Entry<String, Object>>(){

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public Map.Entry<String, Object> next() {
                String key = (String)iter.next();
                Object value = BeanMap.this.get(key);
                return new MyMapEntry(BeanMap.this, key, value);
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
                for (int i = 0; i < propertyDescriptors.length; ++i) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    if (propertyDescriptor == null) continue;
                    String name = propertyDescriptor.getName();
                    Method readMethod = propertyDescriptor.getReadMethod();
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    Class<?> aType = propertyDescriptor.getPropertyType();
                    if (readMethod != null) {
                        this.readMethods.put(name, readMethod);
                    }
                    if (this.writeMethods != null) {
                        this.writeMethods.put(name, writeMethod);
                    }
                    this.types.put(name, aType);
                }
            }
        }
        catch (IntrospectionException introspectionException) {
            // empty catch block
        }
    }

    protected void firePropertyChange(String key, Object oldValue, Object newValue) {
    }

    protected Object[] createWriteMethodArguments(Method method, Object value) throws IllegalAccessException {
        try {
            Class<?>[] types;
            if (value != null && (types = method.getParameterTypes()) != null && types.length > 0) {
                Class paramType = types[0];
                if (paramType.isPrimitive()) {
                    paramType = Primitives.wrap(paramType);
                }
                if (!paramType.isAssignableFrom(value.getClass())) {
                    value = this.convertType(paramType, value);
                }
            }
            return new Object[]{value};
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    protected Object convertType(Class<?> newType, Object value) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class[] types = new Class[]{value.getClass()};
        try {
            Constructor<?> constructor = newType.getConstructor(types);
            Object[] arguments = new Object[]{value};
            return constructor.newInstance(arguments);
        }
        catch (NoSuchMethodException e) {
            Function<?, ?> function = this.getTypeFunction(newType);
            if (function != null) {
                return function.apply(value);
            }
            return value;
        }
    }

    protected Function<?, ?> getTypeFunction(Class<?> aType) {
        return defaultFunctions.get(aType);
    }

    static {
        defaultFunctions.put(Boolean.TYPE, new Function(){

            public Object apply(Object input) {
                return Boolean.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Character.TYPE, new Function(){

            public Object apply(Object input) {
                return Character.valueOf(input.toString().charAt(0));
            }
        });
        defaultFunctions.put(Byte.TYPE, new Function(){

            public Object apply(Object input) {
                return Byte.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Short.TYPE, new Function(){

            public Object apply(Object input) {
                return Short.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Integer.TYPE, new Function(){

            public Object apply(Object input) {
                return Integer.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Long.TYPE, new Function(){

            public Object apply(Object input) {
                return Long.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Float.TYPE, new Function(){

            public Object apply(Object input) {
                return Float.valueOf(input.toString());
            }
        });
        defaultFunctions.put(Double.TYPE, new Function(){

            public Object apply(Object input) {
                return Double.valueOf(input.toString());
            }
        });
    }

    protected static class MyMapEntry
    implements Map.Entry<String, Object> {
        private final BeanMap owner;
        private String key;
        private Object value;

        protected MyMapEntry(BeanMap owner, String key, Object value) {
            this.key = key;
            this.value = value;
            this.owner = owner;
        }

        @Override
        public Object setValue(Object value) {
            Object newValue;
            String key = this.getKey();
            Object oldValue = this.owner.get(key);
            this.owner.put(key, value);
            this.value = newValue = this.owner.get(key);
            return oldValue;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }
    }
}

