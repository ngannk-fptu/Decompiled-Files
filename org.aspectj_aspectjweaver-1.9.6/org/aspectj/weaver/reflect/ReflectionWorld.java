/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.IWeavingSupport;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeakClassLoaderReference;
import org.aspectj.weaver.World;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.IReflectionWorld;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;

public class ReflectionWorld
extends World
implements IReflectionWorld {
    private static Map<WeakClassLoaderReference, ReflectionWorld> rworlds = Collections.synchronizedMap(new HashMap());
    private WeakClassLoaderReference classLoaderReference;
    private AnnotationFinder annotationFinder;
    private boolean mustUseOneFourDelegates = false;
    private Map<String, Class<?>> inProgressResolutionClasses = new HashMap();

    public static ReflectionWorld getReflectionWorldFor(WeakClassLoaderReference classLoaderReference) {
        return new ReflectionWorld(classLoaderReference);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void cleanUpWorlds() {
        Map<WeakClassLoaderReference, ReflectionWorld> map = rworlds;
        synchronized (map) {
            rworlds.clear();
        }
    }

    private ReflectionWorld() {
    }

    public ReflectionWorld(WeakClassLoaderReference classloaderRef) {
        this.setMessageHandler(new ExceptionBasedMessageHandler());
        this.setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        this.classLoaderReference = classloaderRef;
        this.annotationFinder = ReflectionWorld.makeAnnotationFinderIfAny(this.classLoaderReference.getClassLoader(), this);
    }

    public ReflectionWorld(ClassLoader aClassLoader) {
        this.setMessageHandler(new ExceptionBasedMessageHandler());
        this.setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        this.classLoaderReference = new WeakClassLoaderReference(aClassLoader);
        this.annotationFinder = ReflectionWorld.makeAnnotationFinderIfAny(this.classLoaderReference.getClassLoader(), this);
    }

    public ReflectionWorld(boolean forceUseOf14Delegates, ClassLoader aClassLoader) {
        this(aClassLoader);
        this.mustUseOneFourDelegates = forceUseOf14Delegates;
        if (forceUseOf14Delegates) {
            this.setBehaveInJava5Way(false);
        }
    }

    public static AnnotationFinder makeAnnotationFinderIfAny(ClassLoader loader, World world) {
        AnnotationFinder annotationFinder = null;
        try {
            if (LangUtil.is15VMOrGreater()) {
                Class<?> java15AnnotationFinder = Class.forName("org.aspectj.weaver.reflect.Java15AnnotationFinder");
                annotationFinder = (AnnotationFinder)java15AnnotationFinder.newInstance();
                annotationFinder.setClassLoader(loader);
                annotationFinder.setWorld(world);
            }
        }
        catch (ClassNotFoundException java15AnnotationFinder) {
        }
        catch (IllegalAccessException ex) {
            throw new BCException("AspectJ internal error", ex);
        }
        catch (InstantiationException ex) {
            throw new BCException("AspectJ internal error", ex);
        }
        return annotationFinder;
    }

    public ClassLoader getClassLoader() {
        return this.classLoaderReference.getClassLoader();
    }

    @Override
    public AnnotationFinder getAnnotationFinder() {
        return this.annotationFinder;
    }

    @Override
    public ResolvedType resolve(Class aClass) {
        return ReflectionWorld.resolve(this, aClass);
    }

    public static ResolvedType resolve(World world, Class<?> aClass) {
        String className = aClass.getName();
        if (aClass.isArray()) {
            return world.resolve(UnresolvedType.forSignature(className.replace('.', '/')));
        }
        return world.resolve(className);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResolvedType resolveUsingClass(Class<?> clazz) {
        String signature = UnresolvedType.forName(clazz.getName()).getSignature();
        try {
            this.inProgressResolutionClasses.put(signature, clazz);
            ResolvedType resolvedType = this.resolve(clazz.getName());
            return resolvedType;
        }
        finally {
            this.inProgressResolutionClasses.remove(signature);
        }
    }

    @Override
    protected ReferenceTypeDelegate resolveDelegate(ReferenceType ty) {
        Class<?> clazz;
        ReflectionBasedReferenceTypeDelegate result = this.mustUseOneFourDelegates ? ReflectionBasedReferenceTypeDelegateFactory.create14Delegate(ty, this, this.classLoaderReference.getClassLoader()) : ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty, (World)this, this.classLoaderReference.getClassLoader());
        if (result == null && this.inProgressResolutionClasses.size() != 0 && (clazz = this.inProgressResolutionClasses.get(ty.getSignature())) != null) {
            result = ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty, (World)this, clazz);
        }
        return result;
    }

    @Override
    public IWeavingSupport getWeavingSupport() {
        return null;
    }

    @Override
    public boolean isLoadtimeWeaving() {
        return true;
    }

    private static class ExceptionBasedMessageHandler
    implements IMessageHandler {
        private ExceptionBasedMessageHandler() {
        }

        @Override
        public boolean handleMessage(IMessage message) throws AbortException {
            throw new ReflectionWorldException(message.toString());
        }

        @Override
        public boolean isIgnoring(IMessage.Kind kind) {
            return kind == IMessage.INFO;
        }

        @Override
        public void dontIgnore(IMessage.Kind kind) {
        }

        @Override
        public void ignore(IMessage.Kind kind) {
        }
    }

    public static class ReflectionWorldException
    extends RuntimeException {
        private static final long serialVersionUID = -3432261918302793005L;

        public ReflectionWorldException(String message) {
            super(message);
        }
    }
}

