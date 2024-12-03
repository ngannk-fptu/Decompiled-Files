/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.annotation.Resource
 *  javax.ejb.EJB
 *  javax.persistence.PersistenceContext
 *  javax.persistence.PersistenceUnit
 *  javax.xml.ws.WebServiceRef
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.xml.ws.WebServiceRef;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.Introspection;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
import org.apache.tomcat.util.res.StringManager;

public class DefaultInstanceManager
implements InstanceManager {
    private static final AnnotationCacheEntry[] ANNOTATIONS_EMPTY = new AnnotationCacheEntry[0];
    protected static final StringManager sm = StringManager.getManager(DefaultInstanceManager.class);
    private static final boolean EJB_PRESENT;
    private static final boolean JPA_PRESENT;
    private static final boolean WS_PRESENT;
    private final javax.naming.Context context;
    private final Map<String, Map<String, String>> injectionMap;
    protected final ClassLoader classLoader;
    protected final ClassLoader containerClassLoader;
    protected final boolean privileged;
    protected final boolean ignoreAnnotations;
    private final Set<String> restrictedClasses;
    private final ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]> annotationCache = new ManagedConcurrentWeakHashMap();
    private final Map<String, String> postConstructMethods;
    private final Map<String, String> preDestroyMethods;

    public DefaultInstanceManager(javax.naming.Context context, Map<String, Map<String, String>> injectionMap, Context catalinaContext, ClassLoader containerClassLoader) {
        this.classLoader = catalinaContext.getLoader().getClassLoader();
        this.privileged = catalinaContext.getPrivileged();
        this.containerClassLoader = containerClassLoader;
        this.ignoreAnnotations = catalinaContext.getIgnoreAnnotations();
        Log log = catalinaContext.getLogger();
        HashSet<String> classNames = new HashSet<String>();
        DefaultInstanceManager.loadProperties(classNames, "org/apache/catalina/core/RestrictedServlets.properties", "defaultInstanceManager.restrictedServletsResource", log);
        DefaultInstanceManager.loadProperties(classNames, "org/apache/catalina/core/RestrictedListeners.properties", "defaultInstanceManager.restrictedListenersResource", log);
        DefaultInstanceManager.loadProperties(classNames, "org/apache/catalina/core/RestrictedFilters.properties", "defaultInstanceManager.restrictedFiltersResource", log);
        this.restrictedClasses = Collections.unmodifiableSet(classNames);
        this.context = context;
        this.injectionMap = injectionMap;
        this.postConstructMethods = catalinaContext.findPostConstructMethods();
        this.preDestroyMethods = catalinaContext.findPreDestroyMethods();
    }

    public Object newInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        return this.newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Class<?> clazz = this.loadClassMaybePrivileged(className, this.classLoader);
        return this.newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    public Object newInstance(String className, ClassLoader classLoader) throws IllegalAccessException, NamingException, InvocationTargetException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Class<?> clazz = classLoader.loadClass(className);
        return this.newInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]), clazz);
    }

    public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
        this.newInstance(o, o.getClass());
    }

    private Object newInstance(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (!this.ignoreAnnotations) {
            Map<String, String> injections = this.assembleInjectionsFromClassHierarchy(clazz);
            this.populateAnnotationsCache(clazz, injections);
            this.processAnnotations(instance, injections);
            this.postConstruct(instance, clazz);
        }
        return instance;
    }

    private Map<String, String> assembleInjectionsFromClassHierarchy(Class<?> clazz) {
        HashMap<String, String> injections = new HashMap<String, String>();
        Map<String, String> currentInjections = null;
        while (clazz != null) {
            currentInjections = this.injectionMap.get(clazz.getName());
            if (currentInjections != null) {
                injections.putAll(currentInjections);
            }
            clazz = clazz.getSuperclass();
        }
        return injections;
    }

    public void destroyInstance(Object instance) throws IllegalAccessException, InvocationTargetException {
        if (!this.ignoreAnnotations) {
            this.preDestroy(instance, instance.getClass());
        }
    }

    protected void postConstruct(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        AnnotationCacheEntry[] annotations;
        if (this.context == null) {
            return;
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            this.postConstruct(instance, superClass);
        }
        for (AnnotationCacheEntry entry : annotations = (AnnotationCacheEntry[])this.annotationCache.get(clazz)) {
            if (entry.getType() != AnnotationCacheEntryType.POST_CONSTRUCT) continue;
            Method postConstruct = DefaultInstanceManager.getMethod(clazz, entry);
            postConstruct.setAccessible(true);
            postConstruct.invoke(instance, new Object[0]);
        }
    }

    protected void preDestroy(Object instance, Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        AnnotationCacheEntry[] annotations;
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            this.preDestroy(instance, superClass);
        }
        if ((annotations = (AnnotationCacheEntry[])this.annotationCache.get(clazz)) == null) {
            return;
        }
        for (AnnotationCacheEntry entry : annotations) {
            if (entry.getType() != AnnotationCacheEntryType.PRE_DESTROY) continue;
            Method preDestroy = DefaultInstanceManager.getMethod(clazz, entry);
            preDestroy.setAccessible(true);
            preDestroy.invoke(instance, new Object[0]);
        }
    }

    public void backgroundProcess() {
        this.annotationCache.maintain();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void populateAnnotationsCache(Class<?> clazz, Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        ArrayList<AnnotationCacheEntry> annotations = null;
        HashSet<String> injectionsMatchedToSetter = new HashSet<String>();
        while (clazz != null) {
            AnnotationCacheEntry[] annotationsArray = (AnnotationCacheEntry[])this.annotationCache.get(clazz);
            if (annotationsArray == null) {
                if (annotations == null) {
                    annotations = new ArrayList<AnnotationCacheEntry>();
                } else {
                    annotations.clear();
                }
                ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]> methods = Introspection.getDeclaredMethods(clazz);
                Method postConstruct = null;
                String postConstructFromXml = this.postConstructMethods.get(clazz.getName());
                Method preDestroy = null;
                String preDestroyFromXml = this.preDestroyMethods.get(clazz.getName());
                for (Method method : methods) {
                    if (this.context != null) {
                        PersistenceUnit persistenceUnitAnnotation;
                        PersistenceContext persistenceContextAnnotation;
                        WebServiceRef webServiceRefAnnotation;
                        EJB ejbAnnotation;
                        Resource resourceAnnotation;
                        if (injections != null && Introspection.isValidSetter(method)) {
                            String fieldName = Introspection.getPropertyName(method);
                            injectionsMatchedToSetter.add(fieldName);
                            if (injections.containsKey(fieldName)) {
                                annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), injections.get(fieldName), AnnotationCacheEntryType.SETTER));
                                continue;
                            }
                        }
                        if ((resourceAnnotation = method.getAnnotation(Resource.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), resourceAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        } else if (EJB_PRESENT && (ejbAnnotation = method.getAnnotation(EJB.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), ejbAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        } else if (WS_PRESENT && (webServiceRefAnnotation = method.getAnnotation(WebServiceRef.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), webServiceRefAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        } else if (JPA_PRESENT && (persistenceContextAnnotation = method.getAnnotation(PersistenceContext.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), persistenceContextAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        } else if (JPA_PRESENT && (persistenceUnitAnnotation = method.getAnnotation(PersistenceUnit.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), persistenceUnitAnnotation.name(), AnnotationCacheEntryType.SETTER));
                        }
                    }
                    postConstruct = DefaultInstanceManager.findPostConstruct(postConstruct, postConstructFromXml, method);
                    preDestroy = DefaultInstanceManager.findPreDestroy(preDestroy, preDestroyFromXml, method);
                }
                if (postConstruct != null) {
                    annotations.add(new AnnotationCacheEntry(postConstruct.getName(), postConstruct.getParameterTypes(), null, AnnotationCacheEntryType.POST_CONSTRUCT));
                } else if (postConstructFromXml != null) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.postConstructNotFound", new Object[]{postConstructFromXml, clazz.getName()}));
                }
                if (preDestroy != null) {
                    annotations.add(new AnnotationCacheEntry(preDestroy.getName(), preDestroy.getParameterTypes(), null, AnnotationCacheEntryType.PRE_DESTROY));
                } else if (preDestroyFromXml != null) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.preDestroyNotFound", new Object[]{preDestroyFromXml, clazz.getName()}));
                }
                if (this.context != null) {
                    Field[] fields;
                    for (Field field : fields = Introspection.getDeclaredFields(clazz)) {
                        PersistenceUnit persistenceUnitAnnotation;
                        PersistenceContext persistenceContextAnnotation;
                        WebServiceRef webServiceRefAnnotation;
                        EJB ejbAnnotation;
                        String fieldName = field.getName();
                        if (injections != null && injections.containsKey(fieldName) && !injectionsMatchedToSetter.contains(fieldName)) {
                            annotations.add(new AnnotationCacheEntry(fieldName, null, injections.get(fieldName), AnnotationCacheEntryType.FIELD));
                            continue;
                        }
                        Resource resourceAnnotation = field.getAnnotation(Resource.class);
                        if (resourceAnnotation != null) {
                            annotations.add(new AnnotationCacheEntry(fieldName, null, resourceAnnotation.name(), AnnotationCacheEntryType.FIELD));
                            continue;
                        }
                        if (EJB_PRESENT && (ejbAnnotation = field.getAnnotation(EJB.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(fieldName, null, ejbAnnotation.name(), AnnotationCacheEntryType.FIELD));
                            continue;
                        }
                        if (WS_PRESENT && (webServiceRefAnnotation = field.getAnnotation(WebServiceRef.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(fieldName, null, webServiceRefAnnotation.name(), AnnotationCacheEntryType.FIELD));
                            continue;
                        }
                        if (JPA_PRESENT && (persistenceContextAnnotation = field.getAnnotation(PersistenceContext.class)) != null) {
                            annotations.add(new AnnotationCacheEntry(fieldName, null, persistenceContextAnnotation.name(), AnnotationCacheEntryType.FIELD));
                            continue;
                        }
                        if (!JPA_PRESENT || (persistenceUnitAnnotation = field.getAnnotation(PersistenceUnit.class)) == null) continue;
                        annotations.add(new AnnotationCacheEntry(fieldName, null, persistenceUnitAnnotation.name(), AnnotationCacheEntryType.FIELD));
                    }
                }
                annotationsArray = annotations.isEmpty() ? ANNOTATIONS_EMPTY : annotations.toArray(new AnnotationCacheEntry[0]);
                ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]> managedConcurrentWeakHashMap = this.annotationCache;
                synchronized (managedConcurrentWeakHashMap) {
                    this.annotationCache.put(clazz, (Object)annotationsArray);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    protected void processAnnotations(Object instance, Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (this.context == null) {
            return;
        }
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            AnnotationCacheEntry[] annotations;
            for (AnnotationCacheEntry entry : annotations = (AnnotationCacheEntry[])this.annotationCache.get(clazz)) {
                if (entry.getType() == AnnotationCacheEntryType.SETTER) {
                    DefaultInstanceManager.lookupMethodResource(this.context, instance, DefaultInstanceManager.getMethod(clazz, entry), entry.getName(), clazz);
                    continue;
                }
                if (entry.getType() != AnnotationCacheEntryType.FIELD) continue;
                DefaultInstanceManager.lookupFieldResource(this.context, instance, DefaultInstanceManager.getField(clazz, entry), entry.getName(), clazz);
            }
        }
    }

    protected int getAnnotationCacheSize() {
        return this.annotationCache.size();
    }

    protected Class<?> loadClassMaybePrivileged(String className, ClassLoader classLoader) throws ClassNotFoundException {
        Class clazz;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                clazz = (Class)AccessController.doPrivileged(new PrivilegedLoadClass(className, classLoader));
            }
            catch (PrivilegedActionException e) {
                Throwable t = e.getCause();
                if (t instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)t;
                }
                throw new RuntimeException(t);
            }
        } else {
            clazz = this.loadClass(className, classLoader);
        }
        this.checkAccess(clazz);
        return clazz;
    }

    protected Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (className.startsWith("org.apache.catalina")) {
            return this.containerClassLoader.loadClass(className);
        }
        try {
            Class<?> clazz = this.containerClassLoader.loadClass(className);
            if (ContainerServlet.class.isAssignableFrom(clazz)) {
                return clazz;
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        return classLoader.loadClass(className);
    }

    private void checkAccess(Class<?> clazz) {
        if (this.privileged) {
            return;
        }
        if (ContainerServlet.class.isAssignableFrom(clazz)) {
            throw new SecurityException(sm.getString("defaultInstanceManager.restrictedContainerServlet", new Object[]{clazz}));
        }
        while (clazz != null) {
            if (this.restrictedClasses.contains(clazz.getName())) {
                throw new SecurityException(sm.getString("defaultInstanceManager.restrictedClass", new Object[]{clazz}));
            }
            clazz = clazz.getSuperclass();
        }
    }

    protected static void lookupFieldResource(javax.naming.Context context, Object instance, Field field, String name, Class<?> clazz) throws NamingException, IllegalAccessException {
        String normalizedName = DefaultInstanceManager.normalize(name);
        Object lookedupResource = normalizedName != null && normalizedName.length() > 0 ? context.lookup(normalizedName) : context.lookup(clazz.getName() + "/" + field.getName());
        field.setAccessible(true);
        field.set(instance, lookedupResource);
    }

    protected static void lookupMethodResource(javax.naming.Context context, Object instance, Method method, String name, Class<?> clazz) throws NamingException, IllegalAccessException, InvocationTargetException {
        if (!Introspection.isValidSetter(method)) {
            throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidInjection"));
        }
        String normalizedName = DefaultInstanceManager.normalize(name);
        Object lookedupResource = normalizedName != null && normalizedName.length() > 0 ? context.lookup(normalizedName) : context.lookup(clazz.getName() + "/" + Introspection.getPropertyName(method));
        method.setAccessible(true);
        method.invoke(instance, lookedupResource);
    }

    private static void loadProperties(Set<String> classNames, String resourceName, String messageKey, Log log) {
        Properties properties = new Properties();
        ClassLoader cl = DefaultInstanceManager.class.getClassLoader();
        try (InputStream is = cl.getResourceAsStream(resourceName);){
            if (is == null) {
                log.error((Object)sm.getString(messageKey, new Object[]{resourceName}));
            } else {
                properties.load(is);
            }
        }
        catch (IOException ioe) {
            log.error((Object)sm.getString(messageKey, new Object[]{resourceName}), (Throwable)ioe);
        }
        if (properties.isEmpty()) {
            return;
        }
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            if ("restricted".equals(e.getValue())) {
                classNames.add(e.getKey().toString());
                continue;
            }
            log.warn((Object)sm.getString("defaultInstanceManager.restrictedWrongValue", new Object[]{resourceName, e.getKey(), e.getValue()}));
        }
    }

    private static String normalize(String jndiName) {
        if (jndiName != null && jndiName.startsWith("java:comp/env/")) {
            return jndiName.substring(14);
        }
        return jndiName;
    }

    private static Method getMethod(Class<?> clazz, AnnotationCacheEntry entry) {
        Method result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = AccessController.doPrivileged(new PrivilegedGetMethod(clazz, entry));
        } else {
            try {
                result = clazz.getDeclaredMethod(entry.getAccessibleObjectName(), entry.getParamTypes());
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return result;
    }

    private static Field getField(Class<?> clazz, AnnotationCacheEntry entry) {
        Field result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = AccessController.doPrivileged(new PrivilegedGetField(clazz, entry));
        } else {
            try {
                result = clazz.getDeclaredField(entry.getAccessibleObjectName());
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        return result;
    }

    private static Method findPostConstruct(Method currentPostConstruct, String postConstructFromXml, Method method) {
        return DefaultInstanceManager.findLifecycleCallback(currentPostConstruct, postConstructFromXml, method, PostConstruct.class);
    }

    private static Method findPreDestroy(Method currentPreDestroy, String preDestroyFromXml, Method method) {
        return DefaultInstanceManager.findLifecycleCallback(currentPreDestroy, preDestroyFromXml, method, PreDestroy.class);
    }

    private static Method findLifecycleCallback(Method currentMethod, String methodNameFromXml, Method method, Class<? extends Annotation> annotation) {
        Method result = currentMethod;
        if (methodNameFromXml != null) {
            if (method.getName().equals(methodNameFromXml)) {
                if (!Introspection.isValidLifecycleCallback(method)) {
                    throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidAnnotation", new Object[]{annotation.getName()}));
                }
                result = method;
            }
        } else if (method.isAnnotationPresent(annotation)) {
            if (currentMethod != null || !Introspection.isValidLifecycleCallback(method)) {
                throw new IllegalArgumentException(sm.getString("defaultInstanceManager.invalidAnnotation", new Object[]{annotation.getName()}));
            }
            result = method;
        }
        return result;
    }

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("javax.ejb.EJB");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        EJB_PRESENT = clazz != null;
        clazz = null;
        try {
            clazz = Class.forName("javax.persistence.PersistenceContext");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        JPA_PRESENT = clazz != null;
        clazz = null;
        try {
            clazz = Class.forName("javax.xml.ws.WebServiceRef");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        WS_PRESENT = clazz != null;
    }

    private static final class AnnotationCacheEntry {
        private final String accessibleObjectName;
        private final Class<?>[] paramTypes;
        private final String name;
        private final AnnotationCacheEntryType type;

        AnnotationCacheEntry(String accessibleObjectName, Class<?>[] paramTypes, String name, AnnotationCacheEntryType type) {
            this.accessibleObjectName = accessibleObjectName;
            this.paramTypes = paramTypes;
            this.name = name;
            this.type = type;
        }

        public String getAccessibleObjectName() {
            return this.accessibleObjectName;
        }

        public Class<?>[] getParamTypes() {
            return this.paramTypes;
        }

        public String getName() {
            return this.name;
        }

        public AnnotationCacheEntryType getType() {
            return this.type;
        }
    }

    private static enum AnnotationCacheEntryType {
        FIELD,
        SETTER,
        POST_CONSTRUCT,
        PRE_DESTROY;

    }

    private class PrivilegedLoadClass
    implements PrivilegedExceptionAction<Class<?>> {
        private final String className;
        private final ClassLoader classLoader;

        PrivilegedLoadClass(String className, ClassLoader classLoader) {
            this.className = className;
            this.classLoader = classLoader;
        }

        @Override
        public Class<?> run() throws Exception {
            return DefaultInstanceManager.this.loadClass(this.className, this.classLoader);
        }
    }

    private static class PrivilegedGetMethod
    implements PrivilegedAction<Method> {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;

        PrivilegedGetMethod(Class<?> clazz, AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }

        @Override
        public Method run() {
            Method result = null;
            try {
                result = this.clazz.getDeclaredMethod(this.entry.getAccessibleObjectName(), this.entry.getParamTypes());
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            return result;
        }
    }

    private static class PrivilegedGetField
    implements PrivilegedAction<Field> {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;

        PrivilegedGetField(Class<?> clazz, AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }

        @Override
        public Field run() {
            Field result = null;
            try {
                result = this.clazz.getDeclaredField(this.entry.getAccessibleObjectName());
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
            return result;
        }
    }
}

