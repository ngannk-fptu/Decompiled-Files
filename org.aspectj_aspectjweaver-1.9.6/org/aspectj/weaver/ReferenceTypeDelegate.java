/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;

public interface ReferenceTypeDelegate {
    public boolean isAspect();

    public boolean isAnnotationStyleAspect();

    public boolean isInterface();

    public boolean isEnum();

    public boolean isAnnotation();

    public String getRetentionPolicy();

    public boolean canAnnotationTargetType();

    public AnnotationTargetKind[] getAnnotationTargetKinds();

    public boolean isAnnotationWithRuntimeRetention();

    public boolean isClass();

    public boolean isGeneric();

    public boolean isAnonymous();

    public boolean isNested();

    public boolean hasAnnotation(UnresolvedType var1);

    public AnnotationAJ[] getAnnotations();

    public ResolvedType[] getAnnotationTypes();

    public ResolvedMember[] getDeclaredFields();

    public ResolvedType[] getDeclaredInterfaces();

    public ResolvedMember[] getDeclaredMethods();

    public ResolvedMember[] getDeclaredPointcuts();

    public TypeVariable[] getTypeVariables();

    public int getModifiers();

    public PerClause getPerClause();

    public Collection<Declare> getDeclares();

    public Collection<ConcreteTypeMunger> getTypeMungers();

    public Collection<ResolvedMember> getPrivilegedAccesses();

    public ResolvedType getSuperclass();

    public WeaverStateInfo getWeaverState();

    public ReferenceType getResolvedTypeX();

    public boolean isExposedToWeaver();

    public boolean doesNotExposeShadowMungers();

    public ISourceContext getSourceContext();

    public String getSourcefilename();

    public String getDeclaredGenericSignature();

    public ResolvedType getOuterClass();

    public boolean copySourceContext();

    public boolean isCacheable();

    public int getCompilerVersion();

    public void ensureConsistent();

    public boolean isWeavable();

    public boolean hasBeenWoven();

    public boolean hasAnnotations();
}

