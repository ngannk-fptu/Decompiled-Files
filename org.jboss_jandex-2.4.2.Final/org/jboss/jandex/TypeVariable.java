/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public final class TypeVariable
extends Type {
    private static final int HASH_MASK = Integer.MAX_VALUE;
    private static final int IMPLICIT_MASK = Integer.MIN_VALUE;
    private final String name;
    private final Type[] bounds;
    private int hash;

    TypeVariable(String name) {
        this(name, EMPTY_ARRAY);
    }

    TypeVariable(String name, Type[] bounds) {
        this(name, bounds, null);
    }

    TypeVariable(String name, Type[] bounds, AnnotationInstance[] annotations) {
        this(name, bounds, annotations, false);
    }

    TypeVariable(String name, Type[] bounds, AnnotationInstance[] annotations, boolean implicitObjectBound) {
        super(bounds.length > 0 ? bounds[0].name() : DotName.OBJECT_NAME, annotations);
        this.name = name;
        this.bounds = bounds;
        this.hash = implicitObjectBound ? Integer.MIN_VALUE : 0;
    }

    public String identifier() {
        return this.name;
    }

    public List<Type> bounds() {
        return Collections.unmodifiableList(Arrays.asList(this.bounds));
    }

    Type[] boundArray() {
        return this.bounds;
    }

    boolean hasImplicitObjectBound() {
        return (this.hash & Integer.MIN_VALUE) != 0;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.TYPE_VARIABLE;
    }

    @Override
    public TypeVariable asTypeVariable() {
        return this;
    }

    @Override
    String toString(boolean simple) {
        StringBuilder builder = new StringBuilder();
        this.appendAnnotations(builder);
        builder.append(this.name);
        if (!(simple || this.bounds.length <= 0 || this.bounds.length == 1 && ClassType.OBJECT_TYPE.equals(this.bounds[0]))) {
            builder.append(" extends ").append(this.bounds[0].toString(true));
            for (int i = 1; i < this.bounds.length; ++i) {
                builder.append(" & ").append(this.bounds[i].toString(true));
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        TypeVariable that = (TypeVariable)o;
        return this.name.equals(that.name) && Arrays.equals(this.bounds, that.bounds) && this.hasImplicitObjectBound() == that.hasImplicitObjectBound();
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new TypeVariable(this.name, this.bounds, newAnnotations, this.hasImplicitObjectBound());
    }

    TypeVariable copyType(int boundIndex, Type bound) {
        if (boundIndex > this.bounds.length) {
            throw new IllegalArgumentException("Bound index outside of bounds");
        }
        Type[] bounds = (Type[])this.bounds.clone();
        bounds[boundIndex] = bound;
        return new TypeVariable(this.name, bounds, this.annotationArray(), this.hasImplicitObjectBound());
    }

    @Override
    public int hashCode() {
        int hash = this.hash & Integer.MAX_VALUE;
        if (hash != 0) {
            return hash;
        }
        hash = super.hashCode();
        hash = 31 * hash + this.name.hashCode();
        hash = 31 * hash + Arrays.hashCode(this.bounds);
        return this.hash |= hash & Integer.MAX_VALUE;
    }
}

