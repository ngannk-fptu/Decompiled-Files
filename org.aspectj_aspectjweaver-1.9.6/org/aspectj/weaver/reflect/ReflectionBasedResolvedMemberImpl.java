/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import java.lang.reflect.Member;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.GenericSignatureInformationProvider;
import org.aspectj.weaver.reflect.Java14GenericSignatureInformationProvider;

public class ReflectionBasedResolvedMemberImpl
extends ResolvedMemberImpl {
    private AnnotationFinder annotationFinder = null;
    private GenericSignatureInformationProvider gsigInfoProvider = new Java14GenericSignatureInformationProvider();
    private boolean onlyRuntimeAnnotationsCached;
    private Member reflectMember;

    public ReflectionBasedResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes, Member reflectMember) {
        super(kind, declaringType, modifiers, returnType, name, parameterTypes);
        this.reflectMember = reflectMember;
    }

    public ReflectionBasedResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes, UnresolvedType[] checkedExceptions, Member reflectMember) {
        super(kind, declaringType, modifiers, returnType, name, parameterTypes, checkedExceptions);
        this.reflectMember = reflectMember;
    }

    public ReflectionBasedResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, UnresolvedType returnType, String name, UnresolvedType[] parameterTypes, UnresolvedType[] checkedExceptions, ResolvedMember backingGenericMember, Member reflectMember) {
        super(kind, declaringType, modifiers, returnType, name, parameterTypes, checkedExceptions, backingGenericMember);
        this.reflectMember = reflectMember;
    }

    public ReflectionBasedResolvedMemberImpl(MemberKind kind, UnresolvedType declaringType, int modifiers, String name, String signature, Member reflectMember) {
        super(kind, declaringType, modifiers, name, signature);
        this.reflectMember = reflectMember;
    }

    public Member getMember() {
        return this.reflectMember;
    }

    public void setGenericSignatureInformationProvider(GenericSignatureInformationProvider gsigProvider) {
        this.gsigInfoProvider = gsigProvider;
    }

    @Override
    public UnresolvedType[] getGenericParameterTypes() {
        return this.gsigInfoProvider.getGenericParameterTypes(this);
    }

    @Override
    public UnresolvedType getGenericReturnType() {
        return this.gsigInfoProvider.getGenericReturnType(this);
    }

    @Override
    public boolean isSynthetic() {
        return this.gsigInfoProvider.isSynthetic(this);
    }

    @Override
    public boolean isVarargsMethod() {
        return this.gsigInfoProvider.isVarArgs(this);
    }

    @Override
    public boolean isBridgeMethod() {
        return this.gsigInfoProvider.isBridge(this);
    }

    public void setAnnotationFinder(AnnotationFinder finder) {
        this.annotationFinder = finder;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        boolean areRuntimeRetentionAnnotationsSufficient = false;
        if (ofType instanceof ResolvedType) {
            areRuntimeRetentionAnnotationsSufficient = ((ResolvedType)ofType).isAnnotationWithRuntimeRetention();
        }
        this.unpackAnnotations(areRuntimeRetentionAnnotationsSufficient);
        return super.hasAnnotation(ofType);
    }

    @Override
    public boolean hasAnnotations() {
        this.unpackAnnotations(false);
        return super.hasAnnotations();
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        this.unpackAnnotations(false);
        return super.getAnnotationTypes();
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        this.unpackAnnotations(false);
        if (this.annotationFinder == null || this.annotationTypes == null) {
            return null;
        }
        for (ResolvedType type : this.annotationTypes) {
            if (!type.getSignature().equals(ofType.getSignature())) continue;
            return this.annotationFinder.getAnnotationOfType(ofType, this.reflectMember);
        }
        return null;
    }

    @Override
    public String getAnnotationDefaultValue() {
        if (this.annotationFinder == null) {
            return null;
        }
        return this.annotationFinder.getAnnotationDefaultValue(this.reflectMember);
    }

    @Override
    public ResolvedType[][] getParameterAnnotationTypes() {
        if (this.parameterAnnotationTypes == null && this.annotationFinder != null) {
            this.parameterAnnotationTypes = this.annotationFinder.getParameterAnnotationTypes(this.reflectMember);
        }
        return this.parameterAnnotationTypes;
    }

    private void unpackAnnotations(boolean areRuntimeRetentionAnnotationsSufficient) {
        if (this.annotationFinder != null && (this.annotationTypes == null || !areRuntimeRetentionAnnotationsSufficient && this.onlyRuntimeAnnotationsCached)) {
            this.annotationTypes = this.annotationFinder.getAnnotations(this.reflectMember, areRuntimeRetentionAnnotationsSufficient);
            this.onlyRuntimeAnnotationsCached = areRuntimeRetentionAnnotationsSufficient;
        }
    }
}

