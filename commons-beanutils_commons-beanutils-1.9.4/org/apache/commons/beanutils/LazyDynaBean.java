/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaBeanPropertyMapDecorator;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.commons.beanutils.MutableDynaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LazyDynaBean
implements DynaBean,
Serializable {
    private transient Log logger = LogFactory.getLog(LazyDynaBean.class);
    protected static final BigInteger BigInteger_ZERO = new BigInteger("0");
    protected static final BigDecimal BigDecimal_ZERO = new BigDecimal("0");
    protected static final Character Character_SPACE = new Character(' ');
    protected static final Byte Byte_ZERO = new Byte(0);
    protected static final Short Short_ZERO = new Short(0);
    protected static final Integer Integer_ZERO = new Integer(0);
    protected static final Long Long_ZERO = new Long(0L);
    protected static final Float Float_ZERO = new Float(0.0f);
    protected static final Double Double_ZERO = new Double(0.0);
    protected Map<String, Object> values = this.newMap();
    private transient Map<String, Object> mapDecorator;
    protected MutableDynaClass dynaClass;

    public LazyDynaBean() {
        this(new LazyDynaClass());
    }

    public LazyDynaBean(String name) {
        this(new LazyDynaClass(name));
    }

    public LazyDynaBean(DynaClass dynaClass) {
        this.dynaClass = dynaClass instanceof MutableDynaClass ? (MutableDynaClass)dynaClass : new LazyDynaClass(dynaClass.getName(), dynaClass.getDynaProperties());
    }

    public Map<String, Object> getMap() {
        if (this.mapDecorator == null) {
            this.mapDecorator = new DynaBeanPropertyMapDecorator(this);
        }
        return this.mapDecorator;
    }

    public int size(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        Object value = this.values.get(name);
        if (value == null) {
            return 0;
        }
        if (value instanceof Map) {
            return ((Map)value).size();
        }
        if (value instanceof List) {
            return ((List)value).size();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }
        return 0;
    }

    @Override
    public boolean contains(String name, String key) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        Object value = this.values.get(name);
        if (value == null) {
            return false;
        }
        if (value instanceof Map) {
            return ((Map)value).containsKey(key);
        }
        return false;
    }

    @Override
    public Object get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        Object value = this.values.get(name);
        if (value != null) {
            return value;
        }
        if (!this.isDynaProperty(name)) {
            return null;
        }
        value = this.createProperty(name, this.dynaClass.getDynaProperty(name).getType());
        if (value != null) {
            this.set(name, value);
        }
        return value;
    }

    @Override
    public Object get(String name, int index) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultIndexedProperty(name));
        }
        Object indexedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + this.dynaClass.getDynaProperty(name).getName());
        }
        if ((indexedProperty = this.growIndexedProperty(name, indexedProperty, index)).getClass().isArray()) {
            return Array.get(indexedProperty, index);
        }
        if (indexedProperty instanceof List) {
            return ((List)indexedProperty).get(index);
        }
        throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
    }

    @Override
    public Object get(String name, String key) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultMappedProperty(name));
        }
        Object mappedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")' " + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        if (mappedProperty instanceof Map) {
            return ((Map)mappedProperty).get(key);
        }
        throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + mappedProperty.getClass().getName());
    }

    @Override
    public DynaClass getDynaClass() {
        return this.dynaClass;
    }

    @Override
    public void remove(String name, String key) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        Object value = this.values.get(name);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + value.getClass().getName());
        }
        ((Map)value).remove(key);
    }

    @Override
    public void set(String name, Object value) {
        if (!this.isDynaProperty(name)) {
            if (this.dynaClass.isRestricted()) {
                throw new IllegalArgumentException("Invalid property name '" + name + "' (DynaClass is restricted)");
            }
            if (value == null) {
                this.dynaClass.add(name);
            } else {
                this.dynaClass.add(name, value.getClass());
            }
        }
        DynaProperty descriptor = this.dynaClass.getDynaProperty(name);
        if (value == null) {
            if (descriptor.getType().isPrimitive()) {
                throw new NullPointerException("Primitive value for '" + name + "'");
            }
        } else if (!this.isAssignable(descriptor.getType(), value.getClass())) {
            throw new ConversionException("Cannot assign value of type '" + value.getClass().getName() + "' to property '" + name + "' of type '" + descriptor.getType().getName() + "'");
        }
        this.values.put(name, value);
    }

    @Override
    public void set(String name, int index, Object value) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultIndexedProperty(name));
        }
        Object indexedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isIndexed()) {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'" + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        if ((indexedProperty = this.growIndexedProperty(name, indexedProperty, index)).getClass().isArray()) {
            Array.set(indexedProperty, index, value);
        } else if (indexedProperty instanceof List) {
            List values = (List)indexedProperty;
            values.set(index, value);
        } else {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]' " + indexedProperty.getClass().getName());
        }
    }

    @Override
    public void set(String name, String key, Object value) {
        if (!this.isDynaProperty(name)) {
            this.set(name, this.defaultMappedProperty(name));
        }
        Object mappedProperty = this.get(name);
        if (!this.dynaClass.getDynaProperty(name).isMapped()) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'" + this.dynaClass.getDynaProperty(name).getType().getName());
        }
        Map valuesMap = (Map)mappedProperty;
        valuesMap.put(key, value);
    }

    protected Object growIndexedProperty(String name, Object indexedProperty, int index) {
        int length;
        if (indexedProperty instanceof List) {
            List list = (List)indexedProperty;
            while (index >= list.size()) {
                Class<?> contentType = this.getDynaClass().getDynaProperty(name).getContentType();
                Object value = null;
                if (contentType != null) {
                    value = this.createProperty(name + "[" + list.size() + "]", contentType);
                }
                list.add(value);
            }
        }
        if (indexedProperty.getClass().isArray() && index >= (length = Array.getLength(indexedProperty))) {
            Class<?> componentType = indexedProperty.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, index + 1);
            System.arraycopy(indexedProperty, 0, newArray, 0, length);
            indexedProperty = newArray;
            this.set(name, indexedProperty);
            int newLength = Array.getLength(indexedProperty);
            for (int i = length; i < newLength; ++i) {
                Array.set(indexedProperty, i, this.createProperty(name + "[" + i + "]", componentType));
            }
        }
        return indexedProperty;
    }

    protected Object createProperty(String name, Class<?> type) {
        if (type == null) {
            return null;
        }
        if (type.isArray() || List.class.isAssignableFrom(type)) {
            return this.createIndexedProperty(name, type);
        }
        if (Map.class.isAssignableFrom(type)) {
            return this.createMappedProperty(name, type);
        }
        if (DynaBean.class.isAssignableFrom(type)) {
            return this.createDynaBeanProperty(name, type);
        }
        if (type.isPrimitive()) {
            return this.createPrimitiveProperty(name, type);
        }
        if (Number.class.isAssignableFrom(type)) {
            return this.createNumberProperty(name, type);
        }
        return this.createOtherProperty(name, type);
    }

    protected Object createIndexedProperty(String name, Class<?> type) {
        Object indexedProperty = null;
        if (type == null) {
            indexedProperty = this.defaultIndexedProperty(name);
        } else if (type.isArray()) {
            indexedProperty = Array.newInstance(type.getComponentType(), 0);
        } else if (List.class.isAssignableFrom(type)) {
            if (type.isInterface()) {
                indexedProperty = this.defaultIndexedProperty(name);
            } else {
                try {
                    indexedProperty = type.newInstance();
                }
                catch (Exception ex) {
                    throw new IllegalArgumentException("Error instantiating indexed property of type '" + type.getName() + "' for '" + name + "' " + ex);
                }
            }
        } else {
            throw new IllegalArgumentException("Non-indexed property of type '" + type.getName() + "' for '" + name + "'");
        }
        return indexedProperty;
    }

    protected Object createMappedProperty(String name, Class<?> type) {
        Map<String, Object> mappedProperty = null;
        if (type == null) {
            mappedProperty = this.defaultMappedProperty(name);
        } else if (type.isInterface()) {
            mappedProperty = this.defaultMappedProperty(name);
        } else if (Map.class.isAssignableFrom(type)) {
            try {
                mappedProperty = type.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Error instantiating mapped property of type '" + type.getName() + "' for '" + name + "' " + ex);
            }
        } else {
            throw new IllegalArgumentException("Non-mapped property of type '" + type.getName() + "' for '" + name + "'");
        }
        return mappedProperty;
    }

    protected Object createDynaBeanProperty(String name, Class<?> type) {
        try {
            return type.newInstance();
        }
        catch (Exception ex) {
            if (this.logger().isWarnEnabled()) {
                this.logger().warn((Object)("Error instantiating DynaBean property of type '" + type.getName() + "' for '" + name + "' " + ex));
            }
            return null;
        }
    }

    protected Object createPrimitiveProperty(String name, Class<?> type) {
        if (type == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (type == Integer.TYPE) {
            return Integer_ZERO;
        }
        if (type == Long.TYPE) {
            return Long_ZERO;
        }
        if (type == Double.TYPE) {
            return Double_ZERO;
        }
        if (type == Float.TYPE) {
            return Float_ZERO;
        }
        if (type == Byte.TYPE) {
            return Byte_ZERO;
        }
        if (type == Short.TYPE) {
            return Short_ZERO;
        }
        if (type == Character.TYPE) {
            return Character_SPACE;
        }
        return null;
    }

    protected Object createNumberProperty(String name, Class<?> type) {
        return null;
    }

    protected Object createOtherProperty(String name, Class<?> type) {
        if (type == Object.class || type == String.class || type == Boolean.class || type == Character.class || Date.class.isAssignableFrom(type)) {
            return null;
        }
        try {
            return type.newInstance();
        }
        catch (Exception ex) {
            if (this.logger().isWarnEnabled()) {
                this.logger().warn((Object)("Error instantiating property of type '" + type.getName() + "' for '" + name + "' " + ex));
            }
            return null;
        }
    }

    protected Object defaultIndexedProperty(String name) {
        return new ArrayList();
    }

    protected Map<String, Object> defaultMappedProperty(String name) {
        return new HashMap<String, Object>();
    }

    protected boolean isDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        if (this.dynaClass instanceof LazyDynaClass) {
            return ((LazyDynaClass)this.dynaClass).isDynaProperty(name);
        }
        return this.dynaClass.getDynaProperty(name) != null;
    }

    protected boolean isAssignable(Class<?> dest, Class<?> source) {
        return dest.isAssignableFrom(source) || dest == Boolean.TYPE && source == Boolean.class || dest == Byte.TYPE && source == Byte.class || dest == Character.TYPE && source == Character.class || dest == Double.TYPE && source == Double.class || dest == Float.TYPE && source == Float.class || dest == Integer.TYPE && source == Integer.class || dest == Long.TYPE && source == Long.class || dest == Short.TYPE && source == Short.class;
    }

    protected Map<String, Object> newMap() {
        return new HashMap<String, Object>();
    }

    private Log logger() {
        if (this.logger == null) {
            this.logger = LogFactory.getLog(LazyDynaBean.class);
        }
        return this.logger;
    }
}

