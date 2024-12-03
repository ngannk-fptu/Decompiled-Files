/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
import java.util.Collections;
import org.aspectj.weaver.AbstractReferenceTypeDelegate;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationTargetKind;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;

class BoundedReferenceTypeDelegate
extends AbstractReferenceTypeDelegate {
    public BoundedReferenceTypeDelegate(ReferenceType backing) {
        super(backing, false);
    }

    @Override
    public boolean isAspect() {
        return this.resolvedTypeX.isAspect();
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        return this.resolvedTypeX.isAnnotationStyleAspect();
    }

    @Override
    public boolean isInterface() {
        return this.resolvedTypeX.isInterface();
    }

    @Override
    public boolean isEnum() {
        return this.resolvedTypeX.isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return this.resolvedTypeX.isAnnotation();
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        return this.resolvedTypeX.isAnnotationWithRuntimeRetention();
    }

    @Override
    public boolean isAnonymous() {
        return this.resolvedTypeX.isAnonymous();
    }

    @Override
    public boolean isNested() {
        return this.resolvedTypeX.isNested();
    }

    @Override
    public ResolvedType getOuterClass() {
        return this.resolvedTypeX.getOuterClass();
    }

    @Override
    public String getRetentionPolicy() {
        return this.resolvedTypeX.getRetentionPolicy();
    }

    @Override
    public boolean canAnnotationTargetType() {
        return this.resolvedTypeX.canAnnotationTargetType();
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        return this.resolvedTypeX.getAnnotationTargetKinds();
    }

    @Override
    public boolean isGeneric() {
        return this.resolvedTypeX.isGenericType();
    }

    @Override
    public String getDeclaredGenericSignature() {
        return this.resolvedTypeX.getDeclaredGenericSignature();
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        return this.resolvedTypeX.hasAnnotation(ofType);
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        return this.resolvedTypeX.getAnnotations();
    }

    @Override
    public boolean hasAnnotations() {
        return this.resolvedTypeX.hasAnnotations();
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        return this.resolvedTypeX.getAnnotationTypes();
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        return this.resolvedTypeX.getDeclaredFields();
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        return this.resolvedTypeX.getDeclaredInterfaces();
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        return this.resolvedTypeX.getDeclaredMethods();
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        return this.resolvedTypeX.getDeclaredPointcuts();
    }

    @Override
    public PerClause getPerClause() {
        return this.resolvedTypeX.getPerClause();
    }

    @Override
    public Collection<Declare> getDeclares() {
        return this.resolvedTypeX.getDeclares();
    }

    @Override
    public Collection<ConcreteTypeMunger> getTypeMungers() {
        return this.resolvedTypeX.getTypeMungers();
    }

    @Override
    public Collection<ResolvedMember> getPrivilegedAccesses() {
        return Collections.emptyList();
    }

    @Override
    public int getModifiers() {
        return this.resolvedTypeX.getModifiers();
    }

    @Override
    public ResolvedType getSuperclass() {
        return this.resolvedTypeX.getSuperclass();
    }

    @Override
    public WeaverStateInfo getWeaverState() {
        return null;
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        return this.resolvedTypeX.getTypeVariables();
    }
}

