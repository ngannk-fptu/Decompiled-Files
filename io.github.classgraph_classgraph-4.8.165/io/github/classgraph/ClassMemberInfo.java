/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.HasName;
import io.github.classgraph.HierarchicalTypeSignature;
import io.github.classgraph.ScanResultObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import nonapi.io.github.classgraph.utils.Assert;

public abstract class ClassMemberInfo
extends ScanResultObject
implements HasName {
    protected String declaringClassName;
    protected String name;
    protected int modifiers;
    protected String typeDescriptorStr;
    protected String typeSignatureStr;
    protected AnnotationInfoList annotationInfo;

    ClassMemberInfo() {
    }

    public ClassMemberInfo(String definingClassName, String memberName, int modifiers, String typeDescriptorStr, String typeSignatureStr, AnnotationInfoList annotationInfo) {
        this.declaringClassName = definingClassName;
        this.name = memberName;
        this.modifiers = modifiers;
        this.typeDescriptorStr = typeDescriptorStr;
        this.typeSignatureStr = typeSignatureStr;
        this.annotationInfo = annotationInfo == null || annotationInfo.isEmpty() ? null : annotationInfo;
    }

    @Override
    public ClassInfo getClassInfo() {
        return super.getClassInfo();
    }

    @Override
    public String getClassName() {
        return this.declaringClassName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public abstract String getModifiersStr();

    public boolean isPublic() {
        return Modifier.isPublic(this.modifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.modifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.modifiers);
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifiers);
    }

    public boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    public abstract HierarchicalTypeSignature getTypeDescriptor();

    public String getTypeDescriptorStr() {
        return this.typeDescriptorStr;
    }

    public abstract HierarchicalTypeSignature getTypeSignature();

    public String getTypeSignatureStr() {
        return this.typeSignatureStr;
    }

    public abstract HierarchicalTypeSignature getTypeSignatureOrTypeDescriptor();

    public String getTypeSignatureOrTypeDescriptorStr() {
        if (this.typeSignatureStr != null) {
            return this.typeSignatureStr;
        }
        return this.typeDescriptorStr;
    }

    public AnnotationInfoList getAnnotationInfo() {
        if (!this.scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        return this.annotationInfo == null ? AnnotationInfoList.EMPTY_LIST : AnnotationInfoList.getIndirectAnnotations(this.annotationInfo, null);
    }

    public AnnotationInfo getAnnotationInfo(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getAnnotationInfo(annotation.getName());
    }

    public AnnotationInfo getAnnotationInfo(String annotationName) {
        return (AnnotationInfo)this.getAnnotationInfo().get(annotationName);
    }

    public AnnotationInfoList getAnnotationInfoRepeatable(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getAnnotationInfoRepeatable(annotation.getName());
    }

    public AnnotationInfoList getAnnotationInfoRepeatable(String annotationName) {
        return this.getAnnotationInfo().getRepeatable(annotationName);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.hasAnnotation(annotation.getName());
    }

    public boolean hasAnnotation(String annotationName) {
        return this.getAnnotationInfo().containsName(annotationName);
    }
}

