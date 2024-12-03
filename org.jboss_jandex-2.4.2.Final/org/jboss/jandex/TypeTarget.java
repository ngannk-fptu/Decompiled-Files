/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassExtendsTypeTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.EmptyTypeTarget;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.MethodParameterTypeTarget;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.ThrowsTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeParameterBoundTypeTarget;
import org.jboss.jandex.TypeParameterTypeTarget;

public abstract class TypeTarget
implements AnnotationTarget {
    private final AnnotationTarget enclosingTarget;
    private Type target;

    TypeTarget(AnnotationTarget enclosingTarget, Type target) {
        this.enclosingTarget = enclosingTarget;
        this.target = target;
    }

    TypeTarget(AnnotationTarget enclosingTarget) {
        this(enclosingTarget, null);
    }

    void setTarget(Type target) {
        this.target = target;
    }

    @Override
    public final AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.TYPE;
    }

    public AnnotationTarget enclosingTarget() {
        return this.enclosingTarget;
    }

    public Type target() {
        return this.target;
    }

    public abstract Usage usage();

    public EmptyTypeTarget asEmpty() {
        throw new IllegalArgumentException("Not an empty type target");
    }

    public ClassExtendsTypeTarget asClassExtends() {
        throw new IllegalArgumentException("Not a class extends type target");
    }

    public MethodParameterTypeTarget asMethodParameterType() {
        throw new IllegalArgumentException("Not a method parameter type target");
    }

    public TypeParameterTypeTarget asTypeParameter() {
        throw new IllegalArgumentException("Not a type parameter target");
    }

    public TypeParameterBoundTypeTarget asTypeParameterBound() {
        throw new IllegalArgumentException("Not a type parameter bound target");
    }

    public ThrowsTypeTarget asThrows() {
        throw new IllegalArgumentException("Not a throws type target");
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
        return this;
    }

    @Override
    public RecordComponentInfo asRecordComponent() {
        throw new IllegalArgumentException("Not a record component");
    }

    public static enum Usage {
        EMPTY,
        CLASS_EXTENDS,
        METHOD_PARAMETER,
        TYPE_PARAMETER,
        TYPE_PARAMETER_BOUND,
        THROWS;

    }
}

