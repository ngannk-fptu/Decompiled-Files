/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.util.RuntimeServicesAware
 *  org.apache.velocity.util.introspection.Info
 *  org.apache.velocity.util.introspection.UberspectImpl
 *  org.apache.velocity.util.introspection.VelMethod
 *  org.apache.velocity.util.introspection.VelPropertyGet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AllowlistSecureUberspector;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValueHelper;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValueHelperFactory;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValueIterator;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValueStringHandler;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingMethod;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingPropertyGet;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationPreservingInvocationHandler;
import com.atlassian.velocity.htmlsafe.introspection.BoxingUtils;
import com.atlassian.velocity.htmlsafe.introspection.InterfaceMethods;
import com.atlassian.velocity.htmlsafe.introspection.InterfaceMethodsSet;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.ObjectClassResolver;
import com.atlassian.velocity.htmlsafe.introspection.ProxiedMethod;
import com.atlassian.velocity.htmlsafe.introspection.ReturnValueAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.UnboxingMethod;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationBoxingUberspect
extends AllowlistSecureUberspector
implements RuntimeServicesAware {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final Logger log = LoggerFactory.getLogger(AnnotationBoxingUberspect.class);
    private static final InterfaceMethodsSet ANNOTATION_PRESERVING_METHODS = AnnotationBoxingUberspect.getAnnotationPreservingCollectionMethods();
    private static final MethodAnnotator RETURN_VALUE_ANNOTATOR = new ReturnValueAnnotator();
    @VisibleForTesting
    static final String ATLASSIAN_DEVMODE = "atlassian.dev.mode";
    @VisibleForTesting
    static final String ATLASSIAN_VELOCITY_DEPRECATION_STRICTMODE = "atlassian.velocity.deprecation.strictmode";
    @VisibleForTesting
    static final String LEGACY_CONFLUENCE_VELOCITY_DEPRECATION_STRICTMODE = "confluence.velocity.deprecation.strictmode";
    private RuntimeServices runtimeServices;
    private final ObjectClassResolver CLASS_RESOLVER = new ObjectClassResolver(){

        @Override
        public Class<?> resolveClass(Object object) {
            return AnnotationBoxingUberspect.this.getClassForTargetObject(object);
        }
    };

    @Override
    public void init() {
        try {
            super.init();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.runtimeServices.getApplicationEventCartridge().addReferenceInsertionEventHandler((ReferenceInsertionEventHandler)new AnnotatedValueStringHandler());
    }

    private static InterfaceMethodsSet getAnnotationPreservingCollectionMethods() {
        try {
            InterfaceMethods preservingMapMethods = new InterfaceMethods(Map.class.getMethod("get", Object.class));
            InterfaceMethods preservingListMethods = new InterfaceMethods(List.class.getMethod("get", Integer.TYPE));
            HashSet<InterfaceMethods> preservingMethods = new HashSet<InterfaceMethods>(2);
            preservingMethods.add(preservingListMethods);
            preservingMethods.add(preservingMapMethods);
            return new InterfaceMethodsSet(preservingMethods);
        }
        catch (NoSuchMethodException ex) {
            log.error("Could not find collection method via reflection. Collection inheritance will not function.", (Throwable)ex);
            return new InterfaceMethodsSet();
        }
    }

    public final VelMethod getMethod(Object obj, String methodName, Object[] args, Info info) throws Exception {
        AnnotatedValueHelper valueHelper = AnnotatedValueHelperFactory.getValueHelper(obj, this.CLASS_RESOLVER);
        Object[] unboxedArgs = BoxingUtils.unboxArrayElements(args);
        VelMethod method = super.getMethod(valueHelper.unbox(), methodName, unboxedArgs, info);
        if (method == null) {
            return null;
        }
        method = this.checkAndGenerateAnnotationPreservingProxy(valueHelper, method, unboxedArgs, info);
        Method refMethod = this.lookupMethod(methodName, valueHelper, unboxedArgs, info);
        Collection<Annotation> returnValueAnnotations = this.getMethodAnnotations(refMethod);
        if (!returnValueAnnotations.isEmpty()) {
            method = new AnnotationBoxingMethod(method, returnValueAnnotations);
        }
        return new UnboxingMethod(method);
    }

    public final Iterator<?> getIterator(Object obj, Info info) throws Exception {
        if (!(obj instanceof AnnotatedValue)) {
            return super.getIterator(obj, info);
        }
        AnnotatedValue annotatedValue = (AnnotatedValue)obj;
        Iterator iterator = super.getIterator(annotatedValue.unbox(), info);
        if (iterator == null) {
            return null;
        }
        Collection<Annotation> inheritedAnnotations = annotatedValue.getCollectionInheritableAnnotations();
        if (inheritedAnnotations.isEmpty()) {
            return iterator;
        }
        return new AnnotatedValueIterator(iterator, inheritedAnnotations);
    }

    public final VelPropertyGet getPropertyGet(Object obj, String identifier, Info info) throws Exception {
        AnnotatedValueHelper valueHelper = AnnotatedValueHelperFactory.getValueHelper(obj, this.CLASS_RESOLVER);
        VelPropertyGet getter = super.getPropertyGet(valueHelper.unbox(), identifier, info);
        if (getter == null) {
            return null;
        }
        log.debug("Getting introspector method for getter: {}", (Object)getter.getMethodName());
        Method refMethod = this.lookupMethod(getter.getMethodName(), valueHelper, EMPTY_ARRAY, info);
        log.debug("got method: {}", (Object)refMethod);
        if (refMethod == null) {
            return getter;
        }
        Collection<Annotation> annotations = this.getMethodAnnotations(refMethod);
        log.debug("Got return annotations: {}", annotations);
        if (annotations.isEmpty()) {
            return getter;
        }
        return new AnnotationBoxingPropertyGet(getter, annotations);
    }

    private Method lookupMethod(String methodName, AnnotatedValueHelper valueHelper, Object[] unboxedArgs, Info info) {
        Method targetMethod = this.introspector.getMethod(valueHelper.getTargetClass(), methodName, unboxedArgs);
        if (targetMethod != null) {
            this.logOrThrowDeprecationWarningsIfNecessary(valueHelper, info, targetMethod);
        }
        return targetMethod;
    }

    private void logOrThrowDeprecationWarningsIfNecessary(AnnotatedValueHelper valueHelper, Info info, Method targetMethod) {
        boolean shouldLogOrThrow;
        boolean methodDeprecated = targetMethod.isAnnotationPresent(Deprecated.class);
        boolean classDeprecated = valueHelper.getTargetClass().isAnnotationPresent(Deprecated.class);
        boolean hasDeprecation = methodDeprecated || classDeprecated;
        boolean isDevMode = this.isDevModeEnabled();
        boolean shouldThrow = this.shouldThrowExceptionOnDeprecatedMethodAccess();
        boolean shouldLogWarning = log.isWarnEnabled() && isDevMode;
        boolean shouldLogDebug = log.isDebugEnabled() && !isDevMode;
        boolean bl = shouldLogOrThrow = shouldThrow || shouldLogDebug || shouldLogWarning;
        if (hasDeprecation && shouldLogOrThrow) {
            String prefix = classDeprecated ? "Velocity template accessing method on deprecated class" : "Velocity template accessing deprecated method";
            String error = AnnotationBoxingUberspect.buildDeprecatedMethodAccessMessage(prefix, valueHelper.getTargetClass(), targetMethod, info);
            if (shouldThrow) {
                throw new UnsupportedOperationException("(This exception is only thrown when atlassian.velocity.deprecation.strictmode is enabled) " + error);
            }
            if (shouldLogWarning) {
                log.warn(error);
            }
            if (shouldLogDebug) {
                log.debug(error);
            }
        }
    }

    static String buildDeprecatedMethodAccessMessage(String prefix, Class targetClass, Method method, Info info) {
        return String.format("%s %s#%s - %s", prefix, targetClass.getCanonicalName(), method.getName(), info.toString());
    }

    private boolean isDevModeEnabled() {
        return Boolean.getBoolean(ATLASSIAN_DEVMODE);
    }

    private boolean shouldThrowExceptionOnDeprecatedMethodAccess() {
        String strictModeProperty = System.getProperty(ATLASSIAN_VELOCITY_DEPRECATION_STRICTMODE);
        String confluenceStrictModeProperty = System.getProperty(LEGACY_CONFLUENCE_VELOCITY_DEPRECATION_STRICTMODE);
        if (strictModeProperty == null && confluenceStrictModeProperty == null) {
            return false;
        }
        if (confluenceStrictModeProperty != null) {
            log.warn("System property {} is deprecated. Use {} instead.", (Object)LEGACY_CONFLUENCE_VELOCITY_DEPRECATION_STRICTMODE, (Object)ATLASSIAN_VELOCITY_DEPRECATION_STRICTMODE);
            return Boolean.valueOf(confluenceStrictModeProperty);
        }
        return Boolean.valueOf(strictModeProperty);
    }

    private VelMethod checkAndGenerateAnnotationPreservingProxy(AnnotatedValueHelper valueHelper, VelMethod velocityMethod, Object[] unboxedArgs, Info info) throws Exception {
        if (!valueHelper.isBoxedValue()) {
            return velocityMethod;
        }
        AnnotationBoxedElement annotatedValue = valueHelper.getBoxedValueWithInheritedAnnotations();
        if (annotatedValue != null) {
            return this.methodProxy(annotatedValue, ANNOTATION_PRESERVING_METHODS, info, unboxedArgs, velocityMethod);
        }
        return velocityMethod;
    }

    private VelMethod methodProxy(AnnotationBoxedElement<?> value, InterfaceMethodsSet interfaceMethodsSet, Info info, Object[] unboxedArgs, VelMethod velMethod) throws Exception {
        InterfaceMethodsSet implementedInterfaceMethods = interfaceMethodsSet.getImplementedMethods(value.unbox().getClass());
        if (implementedInterfaceMethods.isEmpty()) {
            return velMethod;
        }
        Set<Class<?>> implementedInterfaces = implementedInterfaceMethods.getInterfaces();
        log.debug("Object implements: {}", implementedInterfaces);
        Object proxiedObject = Proxy.newProxyInstance(((Object)((Object)this)).getClass().getClassLoader(), implementedInterfaces.toArray(new Class[implementedInterfaces.size()]), (InvocationHandler)new AnnotationPreservingInvocationHandler(value, implementedInterfaceMethods.getMethods()));
        VelMethod proxiedMethod = super.getMethod(proxiedObject, velMethod.getMethodName(), unboxedArgs, info);
        if (proxiedMethod == null) {
            return velMethod;
        }
        log.debug("Proxying method: {}", (Object)proxiedMethod);
        return new ProxiedMethod(proxiedMethod, proxiedObject);
    }

    public void setRuntimeServices(RuntimeServices runtimeServices) {
        this.runtimeServices = runtimeServices;
        if (RuntimeServicesAware.class.isAssignableFrom(UberspectImpl.class)) {
            super.setRuntimeServices(runtimeServices);
        }
    }

    protected Collection<Annotation> getMethodAnnotations(Method method) {
        return ImmutableSet.copyOf(RETURN_VALUE_ANNOTATOR.getAnnotationsForMethod(method));
    }

    protected Class<?> getClassForTargetObject(Object targetObject) {
        return targetObject.getClass();
    }
}

