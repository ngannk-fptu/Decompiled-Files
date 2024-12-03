/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
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

public class GeneratedReferenceTypeDelegate
extends AbstractReferenceTypeDelegate {
    private ResolvedType superclass;

    public GeneratedReferenceTypeDelegate(ReferenceType backing) {
        super(backing, false);
    }

    @Override
    public boolean isAspect() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isAnnotationStyleAspect() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isInterface() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isEnum() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isAnnotation() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isAnnotationWithRuntimeRetention() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isAnonymous() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isNested() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public ResolvedType getOuterClass() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public String getRetentionPolicy() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean canAnnotationTargetType() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public AnnotationTargetKind[] getAnnotationTargetKinds() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean isGeneric() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public String getDeclaredGenericSignature() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public boolean hasAnnotations() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public ResolvedMember[] getDeclaredFields() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public ResolvedType[] getDeclaredInterfaces() {
        return ResolvedType.NONE;
    }

    @Override
    public ResolvedMember[] getDeclaredMethods() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public ResolvedMember[] getDeclaredPointcuts() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public PerClause getPerClause() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public Collection<Declare> getDeclares() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public Collection<ConcreteTypeMunger> getTypeMungers() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public Collection<ResolvedMember> getPrivilegedAccesses() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public int getModifiers() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    public void setSuperclass(ResolvedType superclass) {
        this.superclass = superclass;
    }

    @Override
    public ResolvedType getSuperclass() {
        return this.superclass;
    }

    @Override
    public WeaverStateInfo getWeaverState() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }

    @Override
    public TypeVariable[] getTypeVariables() {
        throw new UnsupportedOperationException("Not supported for GeneratedReferenceTypeDelegate");
    }
}

