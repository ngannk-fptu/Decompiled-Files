/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInternal;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.Modifiers;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.Utils;

public final class FieldInfo
implements AnnotationTarget {
    private ClassInfo clazz;
    private FieldInternal internal;

    FieldInfo() {
    }

    FieldInfo(ClassInfo clazz, FieldInternal internal) {
        this.clazz = clazz;
        this.internal = internal;
    }

    FieldInfo(ClassInfo clazz, byte[] name, Type type, short flags) {
        this(clazz, new FieldInternal(name, type, flags));
    }

    public static FieldInfo create(ClassInfo clazz, String name, Type type, short flags) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz can't be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        return new FieldInfo(clazz, Utils.toUTF8(name), type, flags);
    }

    public final String name() {
        return this.internal.name();
    }

    public final ClassInfo declaringClass() {
        return this.clazz;
    }

    public final Type type() {
        return this.internal.type();
    }

    @Override
    public final AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.FIELD;
    }

    public List<AnnotationInstance> annotations() {
        return this.internal.annotations();
    }

    public final AnnotationInstance annotation(DotName name) {
        return this.internal.annotation(name);
    }

    public final List<AnnotationInstance> annotationsWithRepeatable(DotName name, IndexView index) {
        AnnotationInstance ret = this.annotation(name);
        if (ret != null) {
            return Collections.singletonList(ret);
        }
        ClassInfo annotationClass = index.getClassByName(name);
        if (annotationClass == null) {
            throw new IllegalArgumentException("Index does not contain the annotation definition: " + name);
        }
        if (!annotationClass.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation type: " + annotationClass);
        }
        AnnotationInstance repeatable = annotationClass.classAnnotation(Index.REPEATABLE);
        if (repeatable == null) {
            return Collections.emptyList();
        }
        Type containingType = repeatable.value().asClass();
        AnnotationInstance containing = this.annotation(containingType.name());
        if (containing == null) {
            return Collections.emptyList();
        }
        AnnotationInstance[] values = containing.value().asNestedArray();
        ArrayList<AnnotationInstance> instances = new ArrayList<AnnotationInstance>(values.length);
        for (AnnotationInstance nestedInstance : values) {
            instances.add(nestedInstance);
        }
        return instances;
    }

    public final boolean hasAnnotation(DotName name) {
        return this.internal.hasAnnotation(name);
    }

    public boolean isEnumConstant() {
        return (this.flags() & 0x4000) != 0;
    }

    public final short flags() {
        return this.internal.flags();
    }

    public final boolean isSynthetic() {
        return Modifiers.isSynthetic(this.internal.flags());
    }

    public String toString() {
        return this.internal.toString(this.clazz);
    }

    @Override
    public final ClassInfo asClass() {
        throw new IllegalArgumentException("Not a class");
    }

    @Override
    public final FieldInfo asField() {
        return this;
    }

    @Override
    public final MethodInfo asMethod() {
        throw new IllegalArgumentException("Not a method");
    }

    @Override
    public final MethodParameterInfo asMethodParameter() {
        throw new IllegalArgumentException("Not a method parameter");
    }

    @Override
    public final TypeTarget asType() {
        throw new IllegalArgumentException("Not a type");
    }

    @Override
    public RecordComponentInfo asRecordComponent() {
        throw new IllegalArgumentException("Not a record component");
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.clazz.hashCode();
        result = 31 * result + this.internal.hashCode();
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FieldInfo other = (FieldInfo)o;
        return this.clazz.equals(other.clazz) && this.internal.equals(other.internal);
    }

    void setType(Type type) {
        this.internal.setType(type);
    }

    void setAnnotations(List<AnnotationInstance> annotations) {
        this.internal.setAnnotations(annotations);
    }

    FieldInternal fieldInternal() {
        return this.internal;
    }

    void setFieldInternal(FieldInternal internal) {
        this.internal = internal;
    }

    void setClassInfo(ClassInfo clazz) {
        this.clazz = clazz;
    }
}

