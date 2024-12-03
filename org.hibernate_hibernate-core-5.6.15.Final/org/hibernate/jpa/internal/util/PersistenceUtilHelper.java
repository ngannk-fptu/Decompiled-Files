/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.LoadState
 */
package org.hibernate.jpa.internal.util;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.persistence.spi.LoadState;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public final class PersistenceUtilHelper {
    private PersistenceUtilHelper() {
    }

    public static LoadState isLoaded(Object reference) {
        if (reference instanceof HibernateProxy) {
            boolean isInitialized = !((HibernateProxy)reference).getHibernateLazyInitializer().isUninitialized();
            return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(reference)) {
            boolean isInitialized = PersistenceUtilHelper.isInitialized(ManagedTypeHelper.asPersistentAttributeInterceptable(reference));
            return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
        }
        if (reference instanceof PersistentCollection) {
            boolean isInitialized = ((PersistentCollection)reference).wasInitialized();
            return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
        }
        return LoadState.UNKNOWN;
    }

    private static boolean isInitialized(PersistentAttributeInterceptable interceptable) {
        BytecodeLazyAttributeInterceptor interceptor = PersistenceUtilHelper.extractInterceptor(interceptable);
        return interceptable == null || interceptor == null || !interceptor.hasAnyUninitializedAttributes();
    }

    private static BytecodeLazyAttributeInterceptor extractInterceptor(PersistentAttributeInterceptable interceptable) {
        return (BytecodeLazyAttributeInterceptor)interceptable.$$_hibernate_getInterceptor();
    }

    public static LoadState isLoadedWithoutReference(Object entity, String attributeName, MetadataCache cache) {
        boolean sureFromUs = false;
        if (entity instanceof HibernateProxy) {
            LazyInitializer li = ((HibernateProxy)entity).getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                return LoadState.NOT_LOADED;
            }
            entity = li.getImplementation();
            sureFromUs = true;
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity)) {
            LoadState state;
            boolean isInitialized;
            BytecodeLazyAttributeInterceptor interceptor = PersistenceUtilHelper.extractInterceptor(ManagedTypeHelper.asPersistentAttributeInterceptable(entity));
            boolean bl = isInitialized = interceptor == null || interceptor.isAttributeLoaded(attributeName);
            if (isInitialized && interceptor != null) {
                try {
                    Class<?> entityClass = entity.getClass();
                    Object attributeValue = cache.getClassMetadata(entityClass).getAttributeAccess(attributeName).extractValue(entity);
                    state = PersistenceUtilHelper.isLoaded(attributeValue);
                    if (state == LoadState.UNKNOWN) {
                        state = LoadState.LOADED;
                    }
                }
                catch (AttributeExtractionException ignore) {
                    state = LoadState.UNKNOWN;
                }
            } else if (interceptor != null) {
                state = LoadState.NOT_LOADED;
            } else if (sureFromUs) {
                try {
                    Class<?> entityClass = entity.getClass();
                    Object attributeValue = cache.getClassMetadata(entityClass).getAttributeAccess(attributeName).extractValue(entity);
                    state = PersistenceUtilHelper.isLoaded(attributeValue);
                    if (state == LoadState.UNKNOWN) {
                        state = LoadState.LOADED;
                    }
                }
                catch (AttributeExtractionException ignore) {
                    state = LoadState.UNKNOWN;
                }
            } else {
                state = LoadState.UNKNOWN;
            }
            return state;
        }
        return LoadState.UNKNOWN;
    }

    public static LoadState isLoadedWithReference(Object entity, String attributeName, MetadataCache cache) {
        if (entity instanceof HibernateProxy) {
            LazyInitializer li = ((HibernateProxy)entity).getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                return LoadState.NOT_LOADED;
            }
            entity = li.getImplementation();
        }
        try {
            Class<?> entityClass = entity.getClass();
            Object attributeValue = cache.getClassMetadata(entityClass).getAttributeAccess(attributeName).extractValue(entity);
            return PersistenceUtilHelper.isLoaded(attributeValue);
        }
        catch (AttributeExtractionException ignore) {
            return LoadState.UNKNOWN;
        }
    }

    private static Method getMethod(Class<?> clazz, String attributeName) {
        try {
            char[] string = attributeName.toCharArray();
            string[0] = Character.toUpperCase(string[0]);
            String casedAttributeName = new String(string);
            try {
                return clazz.getDeclaredMethod("get" + casedAttributeName, new Class[0]);
            }
            catch (NoSuchMethodException e) {
                return clazz.getDeclaredMethod("is" + casedAttributeName, new Class[0]);
            }
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static class MetadataCache
    implements Serializable {
        private transient Map<Class<?>, ClassMetadataCache> classCache = new WeakHashMap();

        private void readObject(ObjectInputStream stream) {
            this.classCache = new WeakHashMap();
        }

        ClassMetadataCache getClassMetadata(Class<?> clazz) {
            ClassMetadataCache classMetadataCache = this.classCache.get(clazz);
            if (classMetadataCache == null) {
                classMetadataCache = new ClassMetadataCache(clazz);
                this.classCache.put(clazz, classMetadataCache);
            }
            return classMetadataCache;
        }
    }

    public static class ClassMetadataCache {
        private final Class specifiedClass;
        private List<Class<?>> classHierarchy;
        private Map<String, AttributeAccess> attributeAccessMap = new HashMap<String, AttributeAccess>();

        public ClassMetadataCache(Class<?> clazz) {
            this.specifiedClass = clazz;
            this.classHierarchy = ClassMetadataCache.findClassHierarchy(clazz);
        }

        private static List<Class<?>> findClassHierarchy(Class<?> clazz) {
            ArrayList classes = new ArrayList();
            Class<?> current = clazz;
            do {
                classes.add(current);
            } while ((current = current.getSuperclass()) != null);
            return classes;
        }

        public AttributeAccess getAttributeAccess(String attributeName) {
            AttributeAccess attributeAccess = this.attributeAccessMap.get(attributeName);
            if (attributeAccess == null) {
                attributeAccess = this.buildAttributeAccess(attributeName);
                this.attributeAccessMap.put(attributeName, attributeAccess);
            }
            return attributeAccess;
        }

        private AttributeAccess buildAttributeAccess(final String attributeName) {
            PrivilegedAction<AttributeAccess> action = new PrivilegedAction<AttributeAccess>(){

                @Override
                public AttributeAccess run() {
                    for (Class clazz : classHierarchy) {
                        try {
                            Field field = clazz.getDeclaredField(attributeName);
                            if (field == null) continue;
                            return new FieldAttributeAccess(field);
                        }
                        catch (NoSuchFieldException e) {
                            Method method = PersistenceUtilHelper.getMethod(clazz, attributeName);
                            if (method == null) continue;
                            return new MethodAttributeAccess(attributeName, method);
                        }
                    }
                    return new NoSuchAttributeAccess(specifiedClass, attributeName);
                }
            };
            return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : (AttributeAccess)action.run();
        }
    }

    private static class NoSuchAttributeAccess
    implements AttributeAccess {
        private final Class clazz;
        private final String attributeName;

        public NoSuchAttributeAccess(Class clazz, String attributeName) {
            this.clazz = clazz;
            this.attributeName = attributeName;
        }

        @Override
        public Object extractValue(Object owner) throws AttributeExtractionException {
            throw new AttributeExtractionException("No such attribute : " + this.clazz.getName() + "#" + this.attributeName);
        }
    }

    public static class MethodAttributeAccess
    implements AttributeAccess {
        private final String name;
        private final Method method;

        public MethodAttributeAccess(String attributeName, Method method) {
            this.name = attributeName;
            try {
                ReflectHelper.ensureAccessibility(method);
            }
            catch (Exception e) {
                this.method = null;
                return;
            }
            this.method = method;
        }

        @Override
        public Object extractValue(Object owner) {
            if (this.method == null) {
                throw new AttributeExtractionException("Attribute (method) " + this.name + " is not accessible");
            }
            try {
                return this.method.invoke(owner, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new AttributeExtractionException("Unable to access attribute (method): " + this.method.getDeclaringClass().getName() + "#" + this.name, e);
            }
            catch (InvocationTargetException e) {
                throw new AttributeExtractionException("Unable to access attribute (method): " + this.method.getDeclaringClass().getName() + "#" + this.name, e.getCause());
            }
        }
    }

    public static class FieldAttributeAccess
    implements AttributeAccess {
        private final String name;
        private final Field field;

        public FieldAttributeAccess(Field field) {
            this.name = field.getName();
            try {
                ReflectHelper.ensureAccessibility(field);
            }
            catch (Exception e) {
                this.field = null;
                return;
            }
            this.field = field;
        }

        @Override
        public Object extractValue(Object owner) {
            if (this.field == null) {
                throw new AttributeExtractionException("Attribute (field) " + this.name + " is not accessible");
            }
            try {
                return this.field.get(owner);
            }
            catch (IllegalAccessException e) {
                throw new AttributeExtractionException("Unable to access attribute (field): " + this.field.getDeclaringClass().getName() + "#" + this.name, e);
            }
        }
    }

    public static interface AttributeAccess {
        public Object extractValue(Object var1) throws AttributeExtractionException;
    }

    public static class AttributeExtractionException
    extends HibernateException {
        public AttributeExtractionException(String message) {
            super(message);
        }

        public AttributeExtractionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

