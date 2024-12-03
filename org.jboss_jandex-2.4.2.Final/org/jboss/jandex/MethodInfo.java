/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.Modifiers;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;
import org.jboss.jandex.TypeVariable;

public final class MethodInfo
implements AnnotationTarget {
    static final String[] EMPTY_PARAMETER_NAMES = new String[0];
    private MethodInternal methodInternal;
    private ClassInfo clazz;

    MethodInfo() {
    }

    MethodInfo(ClassInfo clazz, MethodInternal methodInternal) {
        this.methodInternal = methodInternal;
        this.clazz = clazz;
    }

    MethodInfo(ClassInfo clazz, byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags) {
        this(clazz, new MethodInternal(name, parameterNames, parameters, returnType, flags));
    }

    MethodInfo(ClassInfo clazz, byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags, Type[] typeParameters, Type[] exceptions) {
        this(clazz, new MethodInternal(name, parameterNames, parameters, returnType, flags, typeParameters, exceptions));
    }

    public static MethodInfo create(ClassInfo clazz, String name, Type[] args, Type returnType, short flags) {
        return MethodInfo.create(clazz, name, args, returnType, flags, null, null);
    }

    public static MethodInfo create(ClassInfo clazz, String name, Type[] args, Type returnType, short flags, TypeVariable[] typeParameters, Type[] exceptions) {
        return MethodInfo.create(clazz, name, EMPTY_PARAMETER_NAMES, args, returnType, flags, typeParameters, exceptions);
    }

    public static MethodInfo create(ClassInfo clazz, String name, String[] parameterNames, Type[] args, Type returnType, short flags, TypeVariable[] typeParameters, Type[] exceptions) {
        byte[][] parameterNameBytes;
        byte[] bytes;
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz can't be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        if (args == null) {
            throw new IllegalArgumentException("Values can't be null");
        }
        if (parameterNames == null) {
            throw new IllegalArgumentException("Parameter names can't be null");
        }
        if (returnType == null) {
            throw new IllegalArgumentException("returnType can't be null");
        }
        try {
            bytes = name.getBytes("UTF-8");
            parameterNameBytes = new byte[parameterNames.length][];
            for (int i = 0; i < parameterNames.length; ++i) {
                parameterNameBytes[i] = parameterNames[i].getBytes("UTF-8");
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return new MethodInfo(clazz, bytes, parameterNameBytes, args, returnType, flags, typeParameters, exceptions);
    }

    public final String name() {
        return this.methodInternal.name();
    }

    public final String parameterName(int i) {
        return this.methodInternal.parameterName(i);
    }

    @Override
    public final AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.METHOD;
    }

    public final ClassInfo declaringClass() {
        return this.clazz;
    }

    @Deprecated
    public final Type[] args() {
        return this.methodInternal.copyParameters();
    }

    final Type[] copyParameters() {
        return this.methodInternal.copyParameters();
    }

    public final List<Type> parameters() {
        return this.methodInternal.parameters();
    }

    public final Type returnType() {
        return this.methodInternal.returnType();
    }

    public final Type receiverType() {
        return this.methodInternal.receiverType(this.clazz);
    }

    public final List<Type> exceptions() {
        return this.methodInternal.exceptions();
    }

    final Type[] copyExceptions() {
        return this.methodInternal.copyExceptions();
    }

    public final List<TypeVariable> typeParameters() {
        return this.methodInternal.typeParameters();
    }

    public final List<AnnotationInstance> annotations() {
        return this.methodInternal.annotations();
    }

    public final AnnotationInstance annotation(DotName name) {
        return this.methodInternal.annotation(name);
    }

    public final List<AnnotationInstance> annotationsWithRepeatable(DotName name, IndexView index) {
        if (index == null) {
            throw new IllegalArgumentException("Index must not be null");
        }
        List<AnnotationInstance> instances = this.annotations(name);
        ClassInfo annotationClass = index.getClassByName(name);
        if (annotationClass == null) {
            throw new IllegalArgumentException("Index does not contain the annotation definition: " + name);
        }
        if (!annotationClass.isAnnotation()) {
            throw new IllegalArgumentException("Not an annotation type: " + annotationClass);
        }
        AnnotationInstance repeatable = annotationClass.classAnnotation(Index.REPEATABLE);
        if (repeatable != null) {
            Type containingType = repeatable.value().asClass();
            for (AnnotationInstance container : this.annotations(containingType.name())) {
                for (AnnotationInstance nestedInstance : container.value().asNestedArray()) {
                    instances.add(new AnnotationInstance(nestedInstance, container.target()));
                }
            }
        }
        return instances;
    }

    public final List<AnnotationInstance> annotations(DotName name) {
        ArrayList<AnnotationInstance> instances = new ArrayList<AnnotationInstance>();
        for (AnnotationInstance instance : this.methodInternal.annotationArray()) {
            if (!instance.name().equals(name)) continue;
            instances.add(instance);
        }
        return instances;
    }

    public final boolean hasAnnotation(DotName name) {
        return this.methodInternal.hasAnnotation(name);
    }

    public AnnotationValue defaultValue() {
        return this.methodInternal.defaultValue();
    }

    public final short flags() {
        return this.methodInternal.flags();
    }

    public final boolean isSynthetic() {
        return Modifiers.isSynthetic(this.methodInternal.flags());
    }

    public String toString() {
        return this.methodInternal.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MethodInfo that = (MethodInfo)o;
        return this.clazz.equals(that.clazz) && this.methodInternal.equals(that.methodInternal);
    }

    public int hashCode() {
        return 31 * this.clazz.hashCode() + this.methodInternal.hashCode();
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
        return this;
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

    final MethodInternal methodInternal() {
        return this.methodInternal;
    }

    final void setMethodInternal(MethodInternal methodInternal) {
        this.methodInternal = methodInternal;
    }

    final void setClassInfo(ClassInfo clazz) {
        this.clazz = clazz;
    }

    final Type[] typeParameterArray() {
        return this.methodInternal.typeParameterArray();
    }

    void setTypeParameters(Type[] typeParameters) {
        this.methodInternal.setTypeParameters(typeParameters);
    }

    void setParameters(Type[] parameters) {
        this.methodInternal.setParameters(parameters);
    }

    void setReturnType(Type returnType) {
        this.methodInternal.setReturnType(returnType);
    }

    void setExceptions(Type[] exceptions) {
        this.methodInternal.setExceptions(exceptions);
    }

    void setReceiverType(Type receiverType) {
        this.methodInternal.setReceiverType(receiverType);
    }

    void setAnnotations(List<AnnotationInstance> annotations) {
        this.methodInternal.setAnnotations(annotations);
    }

    void setDefaultValue(AnnotationValue defaultValue) {
        this.methodInternal.setDefaultValue(defaultValue);
    }
}

