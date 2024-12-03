/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.SourceContextImpl;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeakClassLoaderReference;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.reflect.ReflectionBasedResolvedMemberImpl;

public class ReflectionBasedReferenceTypeDelegate
implements ReferenceTypeDelegate {
    private static final ClassLoader bootClassLoader = new URLClassLoader(new URL[0]);
    protected Class myClass = null;
    protected WeakClassLoaderReference classLoaderReference = null;
    protected World world;
    private ReferenceType resolvedType;
    private ResolvedMember[] fields = null;
    private ResolvedMember[] methods = null;
    private ResolvedType[] interfaces = null;

    public ReflectionBasedReferenceTypeDelegate(Class forClass, ClassLoader aClassLoader, World inWorld, ReferenceType resolvedType) {
        this.initialize(resolvedType, forClass, aClassLoader, inWorld);
    }

    public ReflectionBasedReferenceTypeDelegate() {
    }

    public void initialize(ReferenceType aType, Class<?> aClass, ClassLoader aClassLoader, World aWorld) {
        this.myClass = aClass;
        this.resolvedType = aType;
        this.world = aWorld;
        this.classLoaderReference = new WeakClassLoaderReference(aClassLoader != null ? aClassLoader : bootClassLoader);
    }

    public Class<?> getClazz() {
        return this.myClass;
    }

    protected Class getBaseClass() {
        return this.myClass;
    }

    protected World getWorld() {
        return this.world;
    }

    public ReferenceType buildGenericType() {
        throw new UnsupportedOperationException("Shouldn't be asking for generic type at 1.4 source level or lower");
    }

    @Override
    public boolean isAspect() {
        return false;
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return this.myClass.isInterface();
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public String getRetentionPolicy() {
        return null;
    }

    @Override
    public boolean canAnnotationTargetType() {
        return false;
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        return null;
    }

    @Override
    public boolean isClass() {
        return !this.myClass.isInterface() && !this.myClass.isPrimitive() && !this.myClass.isArray();
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    @Override
    public boolean isAnonymous() {
        return this.myClass.isAnonymousClass();
    }

    @Override
    public boolean isNested() {
        return this.myClass.isMemberClass();
    }

    @Override
    public ResolvedType getOuterClass() {
        return ReflectionBasedReferenceTypeDelegateFactory.resolveTypeInWorld(this.myClass.getEnclosingClass(), this.world);
    }

    @Override
    public boolean isExposedToWeaver() {
        return false;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        return false;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return AnnotationAJ.EMPTY_ARRAY;
    }

    @Override
    public boolean hasAnnotations() {
        return false;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        return new ResolvedType[0];
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        if (this.fields == null) {
            Field[] reflectFields = this.myClass.getDeclaredFields();
            ResolvedMember[] rFields = new ResolvedMember[reflectFields.length];
            for (int i = 0; i < reflectFields.length; ++i) {
                rFields[i] = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(reflectFields[i], this.world);
            }
            this.fields = rFields;
        }
        return this.fields;
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        if (this.interfaces == null) {
            Class<?>[] reflectInterfaces = this.myClass.getInterfaces();
            ResolvedType[] rInterfaces = new ResolvedType[reflectInterfaces.length];
            for (int i = 0; i < reflectInterfaces.length; ++i) {
                rInterfaces[i] = ReflectionBasedReferenceTypeDelegateFactory.resolveTypeInWorld(reflectInterfaces[i], this.world);
            }
            this.interfaces = rInterfaces;
        }
        return this.interfaces;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        if (this.methods == null) {
            int i;
            Method[] reflectMethods = this.myClass.getDeclaredMethods();
            Constructor<?>[] reflectCons = this.myClass.getDeclaredConstructors();
            ResolvedMember[] rMethods = new ResolvedMember[reflectMethods.length + reflectCons.length];
            for (i = 0; i < reflectMethods.length; ++i) {
                rMethods[i] = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(reflectMethods[i], this.world);
            }
            for (i = 0; i < reflectCons.length; ++i) {
                rMethods[i + reflectMethods.length] = ReflectionBasedReferenceTypeDelegateFactory.createResolvedMember(reflectCons[i], this.world);
            }
            this.methods = rMethods;
        }
        return this.methods;
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        return new ResolvedMember[0];
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        return new TypeVariable[0];
    }

    @Override
    public PerClause getPerClause() {
        return null;
    }

    @Override
    public Collection<Declare> getDeclares() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ConcreteTypeMunger> getTypeMungers() {
        return Collections.emptySet();
    }

    public Collection getPrivilegedAccesses() {
        return Collections.EMPTY_SET;
    }

    @Override
    public int getModifiers() {
        return this.myClass.getModifiers();
    }

    @Override
    public ResolvedType getSuperclass() {
        if (this.myClass.getSuperclass() == null) {
            if (this.myClass == Object.class) {
                return null;
            }
            return this.world.resolve(UnresolvedType.OBJECT);
        }
        return ReflectionBasedReferenceTypeDelegateFactory.resolveTypeInWorld(this.myClass.getSuperclass(), this.world);
    }

    @Override
    public WeaverStateInfo getWeaverState() {
        return null;
    }

    @Override
    public ReferenceType getResolvedTypeX() {
        return this.resolvedType;
    }

    @Override
    public boolean doesNotExposeShadowMungers() {
        return false;
    }

    @Override
    public String getDeclaredGenericSignature() {
        return null;
    }

    public ReflectionBasedResolvedMemberImpl createResolvedMemberFor(Member aMember) {
        return null;
    }

    @Override
    public String getSourcefilename() {
        return this.resolvedType.getName() + ".class";
    }

    @Override
    public ISourceContext getSourceContext() {
        return SourceContextImpl.UNKNOWN_SOURCE_CONTEXT;
    }

    @Override
    public boolean copySourceContext() {
        return true;
    }

    @Override
    public int getCompilerVersion() {
        return AjAttribute.WeaverVersionInfo.getCurrentWeaverMajorVersion();
    }

    @Override
    public void ensureConsistent() {
    }

    @Override
    public boolean isWeavable() {
        return false;
    }

    @Override
    public boolean hasBeenWoven() {
        return false;
    }
}

