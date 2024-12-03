/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;

public final class ArrayType
extends Type {
    private final Type component;
    private final int dimensions;
    private int hash;

    public static ArrayType create(Type component, int dimensions) {
        return new ArrayType(component, dimensions);
    }

    ArrayType(Type component, int dimensions) {
        this(component, dimensions, null);
    }

    ArrayType(Type component, int dimensions, AnnotationInstance[] annotations) {
        super(DotName.OBJECT_NAME, annotations);
        this.dimensions = dimensions;
        this.component = component;
    }

    public Type component() {
        return this.component;
    }

    @Override
    public DotName name() {
        StringBuilder builder = new StringBuilder();
        int dimensions = this.dimensions;
        while (dimensions-- > 0) {
            builder.append('[');
        }
        if (this.component instanceof PrimitiveType) {
            builder.append(((PrimitiveType)this.component).toCode());
        } else {
            builder.append('L').append(this.component.name().toString()).append(';');
        }
        return DotName.createSimple(builder.toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.appendRootComponent(builder);
        this.appendArraySyntax(builder);
        return builder.toString();
    }

    private void appendRootComponent(StringBuilder builder) {
        if (this.component.kind() == Type.Kind.ARRAY) {
            this.component.asArrayType().appendRootComponent(builder);
        } else {
            builder.append(this.component);
        }
    }

    private void appendArraySyntax(StringBuilder builder) {
        if (this.annotationArray().length > 0) {
            builder.append(' ');
            this.appendAnnotations(builder);
        }
        for (int i = 0; i < this.dimensions; ++i) {
            builder.append("[]");
        }
        if (this.component.kind() == Type.Kind.ARRAY) {
            this.component.asArrayType().appendArraySyntax(builder);
        }
    }

    public int dimensions() {
        return this.dimensions;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.ARRAY;
    }

    @Override
    public ArrayType asArrayType() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArrayType)) {
            return false;
        }
        ArrayType arrayType = (ArrayType)o;
        return super.equals(o) && this.dimensions == arrayType.dimensions && this.component.equals(arrayType.component);
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash != 0) {
            return hash;
        }
        hash = super.hashCode();
        hash = 31 * hash + this.component.hashCode();
        this.hash = hash = 31 * hash + this.dimensions;
        return this.hash;
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new ArrayType(this.component, this.dimensions, newAnnotations);
    }

    Type copyType(Type component, int dimensions) {
        return new ArrayType(component, dimensions, this.annotationArray());
    }
}

