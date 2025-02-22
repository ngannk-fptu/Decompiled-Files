/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELManager;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class BeanELResolver
extends ELResolver {
    private static final int CACHE_SIZE = System.getSecurityManager() == null ? Integer.getInteger("org.apache.el.BeanELResolver.CACHE_SIZE", 1000).intValue() : AccessController.doPrivileged(() -> Integer.getInteger(CACHE_SIZE_PROP, 1000)).intValue();
    private static final String CACHE_SIZE_PROP = "org.apache.el.BeanELResolver.CACHE_SIZE";
    private final boolean readOnly;
    private final ConcurrentCache<String, BeanProperties> cache = new ConcurrentCache(CACHE_SIZE);

    public BeanELResolver() {
        this.readOnly = false;
    }

    public BeanELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return null;
        }
        context.setPropertyResolved(base, property);
        return this.property(context, base, property).getPropertyType();
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return null;
        }
        context.setPropertyResolved(base, property);
        Method m = this.property(context, base, property).read(context, base);
        try {
            return m.invoke(base, (Object[])null);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(Util.message(context, "propertyReadError", base.getClass().getName(), property.toString()), cause);
        }
        catch (Exception e) {
            throw new ELException(e);
        }
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return;
        }
        context.setPropertyResolved(base, property);
        if (this.readOnly) {
            throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
        }
        Method m = this.property(context, base, property).write(context, base);
        try {
            m.invoke(base, value);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(Util.message(context, "propertyWriteError", base.getClass().getName(), property.toString()), cause);
        }
        catch (Exception e) {
            throw new ELException(e);
        }
    }

    @Override
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Objects.requireNonNull(context);
        if (base == null || method == null) {
            return null;
        }
        ExpressionFactory factory = ELManager.getExpressionFactory();
        String methodName = (String)factory.coerceToType(method, String.class);
        Method matchingMethod = Util.findMethod(context, base.getClass(), base, methodName, paramTypes, params);
        Object[] parameters = Util.buildParameters(context, matchingMethod.getParameterTypes(), matchingMethod.isVarArgs(), params);
        Object result = null;
        try {
            result = matchingMethod.invoke(base, parameters);
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ELException(e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(cause);
        }
        context.setPropertyResolved(base, method);
        return result;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return false;
        }
        context.setPropertyResolved(base, property);
        return this.readOnly || this.property(context, base, property).isReadOnly(base);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            return null;
        }
        try {
            PropertyDescriptor[] pds;
            BeanInfo info = Introspector.getBeanInfo(base.getClass());
            for (PropertyDescriptor pd : pds = info.getPropertyDescriptors()) {
                pd.setValue("resolvableAtDesignTime", Boolean.TRUE);
                pd.setValue("type", pd.getPropertyType());
            }
            return Arrays.asList((FeatureDescriptor[])pds).iterator();
        }
        catch (IntrospectionException introspectionException) {
            return null;
        }
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base != null) {
            return Object.class;
        }
        return null;
    }

    private BeanProperty property(ELContext ctx, Object base, Object property) {
        Class<?> type = base.getClass();
        String prop = property.toString();
        BeanProperties props = this.cache.get(type.getName());
        if (props == null || type != props.getType()) {
            props = new BeanProperties(type);
            this.cache.put(type.getName(), props);
        }
        return props.get(ctx, prop);
    }

    private static final class ConcurrentCache<K, V> {
        private final int size;
        private final Map<K, V> eden;
        private final Map<K, V> longterm;

        ConcurrentCache(int size) {
            this.size = size;
            this.eden = new ConcurrentHashMap(size);
            this.longterm = new WeakHashMap(size);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public V get(K key) {
            V value = this.eden.get(key);
            if (value == null) {
                Map<K, V> map = this.longterm;
                synchronized (map) {
                    value = this.longterm.get(key);
                }
                if (value != null) {
                    this.eden.put(key, value);
                }
            }
            return value;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void put(K key, V value) {
            if (this.eden.size() >= this.size) {
                Map<K, V> map = this.longterm;
                synchronized (map) {
                    this.longterm.putAll(this.eden);
                }
                this.eden.clear();
            }
            this.eden.put(key, value);
        }
    }

    static final class BeanProperty {
        private final Class<?> type;
        private final Class<?> owner;
        private final PropertyDescriptor descriptor;
        private Method read;
        private Method write;

        BeanProperty(Class<?> owner, PropertyDescriptor descriptor) {
            this.owner = owner;
            this.descriptor = descriptor;
            this.type = descriptor.getPropertyType();
        }

        public Class<?> getPropertyType() {
            return this.type;
        }

        public boolean isReadOnly(Object base) {
            return this.write == null && null == (this.write = Util.getMethod(this.owner, base, this.descriptor.getWriteMethod()));
        }

        private Method write(ELContext ctx, Object base) {
            if (this.write == null) {
                this.write = Util.getMethod(this.owner, base, this.descriptor.getWriteMethod());
                if (this.write == null) {
                    throw new PropertyNotWritableException(Util.message(ctx, "propertyNotWritable", this.owner.getName(), this.descriptor.getName()));
                }
            }
            return this.write;
        }

        private Method read(ELContext ctx, Object base) {
            if (this.read == null) {
                this.read = Util.getMethod(this.owner, base, this.descriptor.getReadMethod());
                if (this.read == null) {
                    throw new PropertyNotFoundException(Util.message(ctx, "propertyNotReadable", this.owner.getName(), this.descriptor.getName()));
                }
            }
            return this.read;
        }
    }

    static final class BeanProperties {
        private final Map<String, BeanProperty> properties;
        private final Class<?> type;

        BeanProperties(Class<?> type) throws ELException {
            this.type = type;
            this.properties = new HashMap<String, BeanProperty>();
            try {
                PropertyDescriptor[] pds;
                BeanInfo info = Introspector.getBeanInfo(this.type);
                for (PropertyDescriptor pd : pds = info.getPropertyDescriptors()) {
                    this.properties.put(pd.getName(), new BeanProperty(type, pd));
                }
                if (System.getSecurityManager() != null) {
                    this.populateFromInterfaces(type);
                }
            }
            catch (IntrospectionException ie) {
                throw new ELException(ie);
            }
        }

        private void populateFromInterfaces(Class<?> aClass) throws IntrospectionException {
            Class<?> superclass;
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces.length > 0) {
                for (Class<?> ifs : interfaces) {
                    PropertyDescriptor[] pds;
                    BeanInfo info = Introspector.getBeanInfo(ifs);
                    for (PropertyDescriptor pd : pds = info.getPropertyDescriptors()) {
                        if (this.properties.containsKey(pd.getName())) continue;
                        this.properties.put(pd.getName(), new BeanProperty(this.type, pd));
                    }
                    this.populateFromInterfaces(ifs);
                }
            }
            if ((superclass = aClass.getSuperclass()) != null) {
                this.populateFromInterfaces(superclass);
            }
        }

        private BeanProperty get(ELContext ctx, String name) {
            BeanProperty property = this.properties.get(name);
            if (property == null) {
                throw new PropertyNotFoundException(Util.message(ctx, "propertyNotFound", this.type.getName(), name));
            }
            return property;
        }

        private Class<?> getType() {
            return this.type;
        }
    }
}

