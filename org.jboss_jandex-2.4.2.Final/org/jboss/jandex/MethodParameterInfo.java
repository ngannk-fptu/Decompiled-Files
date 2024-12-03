/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.TypeTarget;

public final class MethodParameterInfo
implements AnnotationTarget {
    private final MethodInfo method;
    private final short parameter;

    MethodParameterInfo(MethodInfo method, short parameter) {
        this.method = method;
        this.parameter = parameter;
    }

    public static MethodParameterInfo create(MethodInfo method, short parameter) {
        return new MethodParameterInfo(method, parameter);
    }

    public final MethodInfo method() {
        return this.method;
    }

    public final short position() {
        return this.parameter;
    }

    public final String name() {
        return this.method.parameterName(this.parameter);
    }

    public String toString() {
        return this.method + " #" + this.parameter;
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
        return this;
    }

    @Override
    public final TypeTarget asType() {
        throw new IllegalArgumentException("Not a type");
    }

    @Override
    public RecordComponentInfo asRecordComponent() {
        throw new IllegalArgumentException("Not a record component");
    }

    @Override
    public AnnotationTarget.Kind kind() {
        return AnnotationTarget.Kind.METHOD_PARAMETER;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.method.hashCode();
        result = 31 * result + this.parameter;
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MethodParameterInfo other = (MethodParameterInfo)o;
        return this.method.equals(other.method) && this.parameter == other.parameter;
    }
}

