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
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.RecordComponentInternal;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.Utils;

public final class RecordComponentInfo
implements AnnotationTarget {
    private ClassInfo clazz;
    private RecordComponentInternal internal;

    RecordComponentInfo() {
    }

    RecordComponentInfo(ClassInfo clazz, RecordComponentInternal internal) {
        this.clazz = clazz;
        this.internal = internal;
    }

    RecordComponentInfo(ClassInfo clazz, byte[] name, Type type) {
        this(clazz, new RecordComponentInternal(name, type));
    }

    public static RecordComponentInfo create(ClassInfo clazz, String name, Type type) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz can't be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        return new RecordComponentInfo(clazz, Utils.toUTF8(name), type);
    }

    public final ClassInfo declaringClass() {
        return this.clazz;
    }

    public final FieldInfo field() {
        return this.clazz.field(this.internal.name());
    }

    public final MethodInfo accessor() {
        return this.clazz.method(this.internal.name(), new Type[0]);
    }

    public final String name() {
        return this.internal.name();
    }

    public Type type() {
        return this.internal.type();
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

    public String toString() {
        return this.name();
    }

    @Override
    public final ClassInfo asClass() {
        throw new IllegalArgumentException("Not a class");
    }

    @Override
    public final FieldInfo asField() {
        throw new IllegalArgumentException("Not a field");
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
        return this;
    }

    @Override
    public AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.RECORD_COMPONENT;
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
        RecordComponentInfo other = (RecordComponentInfo)o;
        return this.clazz.equals(other.clazz) && this.internal.equals(other.internal);
    }

    void setType(Type type) {
        this.internal.setType(type);
    }

    void setAnnotations(List<AnnotationInstance> annotations) {
        this.internal.setAnnotations(annotations);
    }

    RecordComponentInternal recordComponentInternal() {
        return this.internal;
    }

    void setRecordComponentInternal(RecordComponentInternal internal) {
        this.internal = internal;
    }

    void setClassInfo(ClassInfo clazz) {
        this.clazz = clazz;
    }
}

