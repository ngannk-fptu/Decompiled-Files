/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Entity
 *  javax.persistence.EntityListeners
 *  javax.persistence.ExcludeDefaultListeners
 *  javax.persistence.ExcludeSuperclassListeners
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.PersistenceException
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XMethod
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.event.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.jpa.event.internal.EmbeddableCallback;
import org.hibernate.jpa.event.internal.EntityCallback;
import org.hibernate.jpa.event.internal.ListenerCallback;
import org.hibernate.jpa.event.spi.CallbackDefinition;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;
import org.jboss.logging.Logger;

public final class CallbackDefinitionResolverLegacyImpl {
    private static final Logger log = Logger.getLogger(CallbackDefinitionResolverLegacyImpl.class);
    private static boolean useAnnotationAnnotatedByListener = false;

    public static List<CallbackDefinition> resolveEntityCallbacks(ReflectionManager reflectionManager, XClass entityClass, CallbackType callbackType) {
        List defaultListeners;
        ArrayList<CallbackDefinition> callbackDefinitions = new ArrayList<CallbackDefinition>();
        ArrayList<String> callbacksMethodNames = new ArrayList<String>();
        ArrayList<Class> orderedListeners = new ArrayList<Class>();
        XClass currentClazz = entityClass;
        boolean stopListeners = false;
        boolean stopDefaultListeners = false;
        do {
            EntityCallback.Definition callbackDefinition = null;
            List methods = currentClazz.getDeclaredMethods();
            for (XMethod xMethod : methods) {
                Method method;
                String methodName;
                if (!xMethod.isAnnotationPresent(callbackType.getCallbackAnnotation()) || callbacksMethodNames.contains(methodName = (method = reflectionManager.toMethod(xMethod)).getName())) continue;
                if (callbackDefinition == null) {
                    callbackDefinition = new EntityCallback.Definition(method, callbackType);
                    Class<?> returnType = method.getReturnType();
                    Class<?>[] args = method.getParameterTypes();
                    if (returnType != Void.TYPE || args.length != 0) {
                        throw new RuntimeException("Callback methods annotated on the bean class must return void and take no arguments: " + callbackType.getCallbackAnnotation().getName() + " - " + xMethod);
                    }
                    ReflectHelper.ensureAccessibility(method);
                    if (log.isDebugEnabled()) {
                        log.debugf("Adding %s as %s callback for entity %s", (Object)methodName, (Object)callbackType.getCallbackAnnotation().getSimpleName(), (Object)entityClass.getName());
                    }
                    callbackDefinitions.add(0, callbackDefinition);
                    callbacksMethodNames.add(0, methodName);
                    continue;
                }
                throw new PersistenceException("You can only annotate one callback method with " + callbackType.getCallbackAnnotation().getName() + " in bean class: " + entityClass.getName());
            }
            if (!stopListeners) {
                CallbackDefinitionResolverLegacyImpl.getListeners(currentClazz, orderedListeners);
                stopListeners = currentClazz.isAnnotationPresent(ExcludeSuperclassListeners.class);
                stopDefaultListeners = currentClazz.isAnnotationPresent(ExcludeDefaultListeners.class);
            }
            while ((currentClazz = currentClazz.getSuperclass()) != null && !currentClazz.isAnnotationPresent(Entity.class) && !currentClazz.isAnnotationPresent(MappedSuperclass.class)) {
            }
        } while (currentClazz != null);
        if (!stopDefaultListeners && (defaultListeners = (List)reflectionManager.getDefaults().get(EntityListeners.class)) != null) {
            int defaultListenerSize = defaultListeners.size();
            for (int i = defaultListenerSize - 1; i >= 0; --i) {
                orderedListeners.add((Class)defaultListeners.get(i));
            }
        }
        for (Class listener : orderedListeners) {
            ListenerCallback.Definition callbackDefinition = null;
            if (listener == null) continue;
            XClass xListener = reflectionManager.toXClass(listener);
            callbacksMethodNames = new ArrayList();
            List methods = xListener.getDeclaredMethods();
            for (XMethod xMethod : methods) {
                Method method;
                String methodName;
                if (!xMethod.isAnnotationPresent(callbackType.getCallbackAnnotation()) || callbacksMethodNames.contains(methodName = (method = reflectionManager.toMethod(xMethod)).getName())) continue;
                if (callbackDefinition == null) {
                    callbackDefinition = new ListenerCallback.Definition(listener, method, callbackType);
                    Class<?> returnType = method.getReturnType();
                    Class<?>[] args = method.getParameterTypes();
                    if (returnType != Void.TYPE || args.length != 1) {
                        throw new PersistenceException("Callback methods annotated in a listener bean class must return void and take one argument: " + callbackType.getCallbackAnnotation().getName() + " - " + method);
                    }
                    ReflectHelper.ensureAccessibility(method);
                    if (log.isDebugEnabled()) {
                        log.debugf("Adding %s as %s callback for entity %s", (Object)methodName, (Object)callbackType.getCallbackAnnotation().getSimpleName(), (Object)entityClass.getName());
                    }
                    callbackDefinitions.add(0, callbackDefinition);
                    continue;
                }
                throw new PersistenceException("You can only annotate one callback method with " + callbackType.getCallbackAnnotation().getName() + " in bean class: " + entityClass.getName() + " and callback listener: " + listener.getName());
            }
        }
        return callbackDefinitions;
    }

    public static List<CallbackDefinition> resolveEmbeddableCallbacks(ReflectionManager reflectionManager, Class<?> entityClass, Property embeddableProperty, CallbackType callbackType) {
        Class embeddableClass = embeddableProperty.getType().getReturnedClass();
        XClass embeddableXClass = reflectionManager.toXClass(embeddableClass);
        Getter embeddableGetter = embeddableProperty.getGetter(entityClass);
        ArrayList<CallbackDefinition> callbackDefinitions = new ArrayList<CallbackDefinition>();
        ArrayList<String> callbacksMethodNames = new ArrayList<String>();
        XClass currentClazz = embeddableXClass;
        do {
            EmbeddableCallback.Definition callbackDefinition = null;
            List methods = currentClazz.getDeclaredMethods();
            for (XMethod xMethod : methods) {
                Method method;
                String methodName;
                if (!xMethod.isAnnotationPresent(callbackType.getCallbackAnnotation()) || callbacksMethodNames.contains(methodName = (method = reflectionManager.toMethod(xMethod)).getName())) continue;
                if (callbackDefinition == null) {
                    callbackDefinition = new EmbeddableCallback.Definition(embeddableGetter, method, callbackType);
                    Class<?> returnType = method.getReturnType();
                    Class<?>[] args = method.getParameterTypes();
                    if (returnType != Void.TYPE || args.length != 0) {
                        throw new RuntimeException("Callback methods annotated on the bean class must return void and take no arguments: " + callbackType.getCallbackAnnotation().getName() + " - " + xMethod);
                    }
                    ReflectHelper.ensureAccessibility(method);
                    if (log.isDebugEnabled()) {
                        log.debugf("Adding %s as %s callback for entity %s", (Object)methodName, (Object)callbackType.getCallbackAnnotation().getSimpleName(), (Object)embeddableXClass.getName());
                    }
                    callbackDefinitions.add(0, callbackDefinition);
                    callbacksMethodNames.add(0, methodName);
                    continue;
                }
                throw new PersistenceException("You can only annotate one callback method with " + callbackType.getCallbackAnnotation().getName() + " in bean class: " + embeddableXClass.getName());
            }
            while ((currentClazz = currentClazz.getSuperclass()) != null && !currentClazz.isAnnotationPresent(MappedSuperclass.class)) {
            }
        } while (currentClazz != null);
        return callbackDefinitions;
    }

    private static void getListeners(XClass currentClazz, List<Class> orderedListeners) {
        EntityListeners entityListeners = (EntityListeners)currentClazz.getAnnotation(EntityListeners.class);
        if (entityListeners != null) {
            Class[] classes = entityListeners.value();
            int size = classes.length;
            for (int index = size - 1; index >= 0; --index) {
                orderedListeners.add(classes[index]);
            }
        }
        if (useAnnotationAnnotatedByListener) {
            Annotation[] annotations;
            for (Annotation annot : annotations = currentClazz.getAnnotations()) {
                entityListeners = annot.getClass().getAnnotation(EntityListeners.class);
                if (entityListeners == null) continue;
                Class[] classes = entityListeners.value();
                int size = classes.length;
                for (int index = size - 1; index >= 0; --index) {
                    orderedListeners.add(classes[index]);
                }
            }
        }
    }

    static {
        Target target = EntityListeners.class.getAnnotation(Target.class);
        if (target != null) {
            for (ElementType type : target.value()) {
                if (!type.equals((Object)ElementType.ANNOTATION_TYPE)) continue;
                useAnnotationAnnotatedByListener = true;
            }
        }
    }
}

