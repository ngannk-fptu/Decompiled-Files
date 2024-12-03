/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.AccessorFactory;
import com.oracle.webservices.api.message.PropertySet;
import com.oracle.webservices.api.message.ReadOnlyPropertyException;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class BasePropertySet
implements PropertySet {
    private Map<String, Object> mapView;

    protected BasePropertySet() {
    }

    protected abstract PropertyMap getPropertyMap();

    protected static PropertyMap parse(Class<?> clazz) {
        return BasePropertySet.parse(clazz, MethodHandles.lookup());
    }

    protected static PropertyMap parse(Class<?> clazz, MethodHandles.Lookup caller) {
        Class<?> cl = Objects.requireNonNull(clazz, "clazz must not be null");
        MethodHandles.Lookup lookup = Objects.requireNonNull(caller, "caller must not be null");
        try {
            return AccessController.doPrivileged(() -> {
                PropertyMap props = new PropertyMap();
                for (Class c = cl; c != Object.class; c = c.getSuperclass()) {
                    PropertySet.Property cp;
                    MethodHandles.Lookup privateLookup = AccessorFactory.createPrivateLookup(c, lookup);
                    for (Field field : c.getDeclaredFields()) {
                        cp = field.getAnnotation(PropertySet.Property.class);
                        if (cp == null) continue;
                        for (String value : cp.value()) {
                            props.put(value, AccessorFactory.createAccessor(field, value, privateLookup));
                        }
                    }
                    for (AccessibleObject accessibleObject : c.getDeclaredMethods()) {
                        Method setter;
                        cp = ((Method)accessibleObject).getAnnotation(PropertySet.Property.class);
                        if (cp == null) continue;
                        String name = ((Method)accessibleObject).getName();
                        assert (name.startsWith("get") || name.startsWith("is"));
                        String setName = name.startsWith("is") ? "set" + name.substring(2) : 's' + name.substring(1);
                        try {
                            setter = cl.getMethod(setName, ((Method)accessibleObject).getReturnType());
                        }
                        catch (NoSuchMethodException e) {
                            setter = null;
                        }
                        for (String value : cp.value()) {
                            props.put(value, AccessorFactory.createAccessor((Method)accessibleObject, setter, value, privateLookup));
                        }
                    }
                }
                return props;
            });
        }
        catch (PrivilegedActionException ex) {
            Throwable t = ex.getCause();
            throw new RuntimeException(t);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        Accessor sp = (Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            return sp.get(this) != null;
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        Accessor sp = (Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            return sp.get(this);
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    public Object put(String key, Object value) {
        Accessor sp = (Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            Object old = sp.get(this);
            sp.set(this, value);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    public boolean supports(Object key) {
        return this.getPropertyMap().containsKey(key);
    }

    @Override
    public Object remove(Object key) {
        Accessor sp = (Accessor)this.getPropertyMap().get(key);
        if (sp != null) {
            Object old = sp.get(this);
            sp.set(this, null);
            return old;
        }
        throw new IllegalArgumentException("Undefined property " + key);
    }

    @Override
    @Deprecated
    public final Map<String, Object> createMapView() {
        final HashSet<Map.Entry<String, Object>> core = new HashSet<Map.Entry<String, Object>>();
        this.createEntrySet(core);
        return new AbstractMap<String, Object>(){

            @Override
            public Set<Map.Entry<String, Object>> entrySet() {
                return core;
            }
        };
    }

    @Override
    public Map<String, Object> asMap() {
        if (this.mapView == null) {
            this.mapView = this.createView();
        }
        return this.mapView;
    }

    protected Map<String, Object> createView() {
        return new MapView(this.mapAllowsAdditionalProperties());
    }

    protected boolean mapAllowsAdditionalProperties() {
        return false;
    }

    protected void createEntrySet(Set<Map.Entry<String, Object>> core) {
        for (final Map.Entry e : this.getPropertyMap().entrySet()) {
            core.add(new Map.Entry<String, Object>(){

                @Override
                public String getKey() {
                    return (String)e.getKey();
                }

                @Override
                public Object getValue() {
                    return ((Accessor)e.getValue()).get(BasePropertySet.this);
                }

                @Override
                public Object setValue(Object value) {
                    Accessor acc = (Accessor)e.getValue();
                    Object old = acc.get(BasePropertySet.this);
                    acc.set(BasePropertySet.this, value);
                    return old;
                }
            });
        }
    }

    final class MapView
    extends HashMap<String, Object> {
        boolean extensible;

        MapView(boolean extensible) {
            super(BasePropertySet.this.getPropertyMap().getPropertyMapEntries().length);
            this.extensible = extensible;
            this.initialize();
        }

        public void initialize() {
            PropertyMapEntry[] entries;
            for (PropertyMapEntry entry : entries = BasePropertySet.this.getPropertyMap().getPropertyMapEntries()) {
                super.put(entry.key, entry.value);
            }
        }

        @Override
        public Object get(Object key) {
            Object o = super.get(key);
            if (o instanceof Accessor) {
                return ((Accessor)o).get(BasePropertySet.this);
            }
            return o;
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            HashSet<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
            for (String key : this.keySet()) {
                entries.add(new AbstractMap.SimpleImmutableEntry<String, Object>(key, this.get(key)));
            }
            return entries;
        }

        @Override
        public Object put(String key, Object value) {
            Object o = super.get(key);
            if (o != null && o instanceof Accessor) {
                Object oldValue = ((Accessor)o).get(BasePropertySet.this);
                ((Accessor)o).set(BasePropertySet.this, value);
                return oldValue;
            }
            if (this.extensible) {
                return super.put(key, value);
            }
            throw new IllegalStateException("Unknown property [" + key + "] for PropertySet [" + BasePropertySet.this.getClass().getName() + "]");
        }

        @Override
        public void clear() {
            for (String key : this.keySet()) {
                this.remove(key);
            }
        }

        @Override
        public Object remove(Object key) {
            Object o = super.get(key);
            if (o instanceof Accessor) {
                ((Accessor)o).set(BasePropertySet.this, null);
            }
            return super.remove(key);
        }
    }

    static final class MethodAccessor
    implements Accessor {
        @NotNull
        private final Method getter;
        @Nullable
        private final Method setter;
        private final String name;

        protected MethodAccessor(Method getter, Method setter, String value) {
            this.getter = getter;
            this.setter = setter;
            this.name = value;
            getter.setAccessible(true);
            if (setter != null) {
                setter.setAccessible(true);
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean hasValue(PropertySet props) {
            return this.get(props) != null;
        }

        @Override
        public Object get(PropertySet props) {
            try {
                return this.getter.invoke((Object)props, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
            catch (InvocationTargetException e) {
                this.handle(e);
                return 0;
            }
        }

        @Override
        public void set(PropertySet props, Object value) {
            if (this.setter == null) {
                throw new ReadOnlyPropertyException(this.getName());
            }
            try {
                this.setter.invoke((Object)props, value);
            }
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
            catch (InvocationTargetException e) {
                this.handle(e);
            }
        }

        private Exception handle(InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new Error(e);
        }
    }

    static final class FieldAccessor
    implements Accessor {
        private final Field f;
        private final String name;

        protected FieldAccessor(Field f, String name) {
            this.f = f;
            f.setAccessible(true);
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean hasValue(PropertySet props) {
            return this.get(props) != null;
        }

        @Override
        public Object get(PropertySet props) {
            try {
                return this.f.get(props);
            }
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        @Override
        public void set(PropertySet props, Object value) {
            try {
                this.f.set(props, value);
            }
            catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
    }

    protected static interface Accessor {
        public String getName();

        public boolean hasValue(PropertySet var1);

        public Object get(PropertySet var1);

        public void set(PropertySet var1, Object var2);
    }

    public static class PropertyMapEntry {
        String key;
        Accessor value;

        public PropertyMapEntry(String k, Accessor v) {
            this.key = k;
            this.value = v;
        }
    }

    protected static class PropertyMap
    extends HashMap<String, Accessor> {
        transient PropertyMapEntry[] cachedEntries = null;

        protected PropertyMap() {
        }

        PropertyMapEntry[] getPropertyMapEntries() {
            if (this.cachedEntries == null) {
                this.cachedEntries = this.createPropertyMapEntries();
            }
            return this.cachedEntries;
        }

        private PropertyMapEntry[] createPropertyMapEntries() {
            PropertyMapEntry[] modelEntries = new PropertyMapEntry[this.size()];
            int i = 0;
            for (Map.Entry e : this.entrySet()) {
                modelEntries[i++] = new PropertyMapEntry((String)e.getKey(), (Accessor)e.getValue());
            }
            return modelEntries;
        }
    }
}

