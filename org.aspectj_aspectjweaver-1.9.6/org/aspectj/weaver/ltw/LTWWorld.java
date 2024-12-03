/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ltw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.Dump;
import org.aspectj.weaver.ICrossReferenceHandler;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.loadtime.IWeavingContext;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.IReflectionWorld;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.reflect.ReflectionWorld;

public class LTWWorld
extends BcelWorld
implements IReflectionWorld {
    private AnnotationFinder annotationFinder;
    private IWeavingContext weavingContext;
    private String classLoaderString;
    private String classLoaderParentString;
    protected static final Class concurrentMapClass = null;
    private static final boolean ShareBootstrapTypes = false;
    protected static Map bootstrapTypes;
    private static final long serialVersionUID = 1L;
    private boolean typeCompletionInProgress = false;
    private List typesForCompletion = new ArrayList();

    public LTWWorld(ClassLoader loader, IWeavingContext weavingContext, IMessageHandler handler, ICrossReferenceHandler xrefHandler) {
        super(loader, handler, xrefHandler);
        this.weavingContext = weavingContext;
        try {
            this.classLoaderString = loader.toString();
        }
        catch (Throwable t) {
            this.classLoaderString = loader.getClass().getName() + ":" + Integer.toString(System.identityHashCode(loader));
        }
        this.classLoaderParentString = loader.getParent() == null ? "<NullParent>" : loader.getParent().toString();
        this.setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        this.annotationFinder = ReflectionWorld.makeAnnotationFinderIfAny(loader, this);
    }

    public ClassLoader getClassLoader() {
        return this.weavingContext.getClassLoader();
    }

    @Override
    protected ReferenceTypeDelegate resolveDelegate(ReferenceType ty) {
        ReferenceTypeDelegate bootstrapLoaderDelegate = this.resolveIfBootstrapDelegate(ty);
        if (bootstrapLoaderDelegate != null) {
            return bootstrapLoaderDelegate;
        }
        return super.resolveDelegate(ty);
    }

    protected ReferenceTypeDelegate resolveIfBootstrapDelegate(ReferenceType ty) {
        return null;
    }

    private ReferenceTypeDelegate resolveReflectionTypeDelegate(ReferenceType ty, ClassLoader resolutionLoader) {
        ReflectionBasedReferenceTypeDelegate res = ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty, (World)this, resolutionLoader);
        return res;
    }

    public void loadedClass(Class clazz) {
    }

    @Override
    public AnnotationFinder getAnnotationFinder() {
        return this.annotationFinder;
    }

    @Override
    public ResolvedType resolve(Class aClass) {
        return ReflectionWorld.resolve(this, aClass);
    }

    private static Map makeConcurrentMap() {
        if (concurrentMapClass != null) {
            try {
                return (Map)concurrentMapClass.newInstance();
            }
            catch (InstantiationException instantiationException) {
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        return Collections.synchronizedMap(new HashMap());
    }

    private static Class makeConcurrentMapClass() {
        String[] betterChoices = new String[]{"java.util.concurrent.ConcurrentHashMap", "edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap", "EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap"};
        for (int i = 0; i < betterChoices.length; ++i) {
            try {
                return Class.forName(betterChoices[i]);
            }
            catch (ClassNotFoundException classNotFoundException) {
                continue;
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public boolean isRunMinimalMemory() {
        if (this.isRunMinimalMemorySet()) {
            return super.isRunMinimalMemory();
        }
        return false;
    }

    @Override
    protected void completeBinaryType(ResolvedType ret) {
        if (this.isLocallyDefined(ret.getName())) {
            if (this.typeCompletionInProgress) {
                this.typesForCompletion.add(ret);
            } else {
                try {
                    this.typeCompletionInProgress = true;
                    this.completeHierarchyForType(ret);
                }
                finally {
                    this.typeCompletionInProgress = false;
                }
                while (this.typesForCompletion.size() != 0) {
                    ResolvedType rt = (ResolvedType)this.typesForCompletion.get(0);
                    this.completeHierarchyForType(rt);
                    this.typesForCompletion.remove(0);
                }
            }
        } else if (!ret.needsModifiableDelegate()) {
            ret = this.completeNonLocalType(ret);
        }
    }

    private void completeHierarchyForType(ResolvedType ret) {
        this.getLint().typeNotExposedToWeaver.setSuppressed(true);
        this.weaveInterTypeDeclarations(ret);
        this.getLint().typeNotExposedToWeaver.setSuppressed(false);
    }

    protected boolean needsCompletion() {
        return true;
    }

    @Override
    public boolean isLocallyDefined(String classname) {
        return this.weavingContext.isLocallyDefined(classname);
    }

    protected ResolvedType completeNonLocalType(ResolvedType ret) {
        if (ret.isMissing()) {
            return ret;
        }
        ResolvedType toResolve = ret;
        if (ret.isParameterizedType() || ret.isGenericType()) {
            toResolve = toResolve.getGenericType();
        }
        ReferenceTypeDelegate rtd = this.resolveReflectionTypeDelegate((ReferenceType)toResolve, this.getClassLoader());
        ((ReferenceType)ret).setDelegate(rtd);
        return ret;
    }

    @Override
    public void storeClass(JavaClass clazz) {
        this.ensureRepositorySetup();
        this.delegate.storeClass(clazz);
    }

    @Override
    public void accept(Dump.IVisitor visitor) {
        visitor.visitObject("Class loader:");
        visitor.visitObject(this.classLoaderString);
        visitor.visitObject("Class loader parent:");
        visitor.visitObject(this.classLoaderParentString);
        super.accept(visitor);
    }

    @Override
    public boolean isLoadtimeWeaving() {
        return true;
    }
}

