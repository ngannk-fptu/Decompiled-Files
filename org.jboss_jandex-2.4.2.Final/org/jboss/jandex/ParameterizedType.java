/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public class ParameterizedType
extends Type {
    private final Type[] arguments;
    private final Type owner;
    private int hash;

    public static ParameterizedType create(DotName name, Type[] arguments, Type owner) {
        return new ParameterizedType(name, arguments, owner);
    }

    ParameterizedType(DotName name, Type[] arguments, Type owner) {
        this(name, arguments, owner, null);
    }

    ParameterizedType(DotName name, Type[] arguments, Type owner, AnnotationInstance[] annotations) {
        super(name, annotations);
        this.arguments = arguments == null ? EMPTY_ARRAY : arguments;
        this.owner = owner;
    }

    public List<Type> arguments() {
        return Collections.unmodifiableList(Arrays.asList(this.arguments));
    }

    Type[] argumentsArray() {
        return this.arguments;
    }

    public Type owner() {
        return this.owner;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.PARAMETERIZED_TYPE;
    }

    @Override
    public ParameterizedType asParameterizedType() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.owner != null) {
            builder.append(this.owner);
            builder.append('.');
            this.appendAnnotations(builder);
            builder.append(this.name().local());
        } else {
            this.appendAnnotations(builder);
            builder.append(this.name());
        }
        if (this.arguments.length > 0) {
            builder.append('<');
            builder.append(this.arguments[0]);
            for (int i = 1; i < this.arguments.length; ++i) {
                builder.append(", ").append(this.arguments[i]);
            }
            builder.append('>');
        }
        return builder.toString();
    }

    @Override
    ParameterizedType copyType(AnnotationInstance[] newAnnotations) {
        return new ParameterizedType(this.name(), this.arguments, this.owner, newAnnotations);
    }

    ParameterizedType copyType(Type[] parameters) {
        return new ParameterizedType(this.name(), parameters, this.owner, this.annotationArray());
    }

    ParameterizedType copyType(Type owner) {
        return new ParameterizedType(this.name(), this.arguments, owner, this.annotationArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        ParameterizedType other = (ParameterizedType)o;
        return (this.owner == other.owner || this.owner != null && this.owner.equals(other.owner)) && Arrays.equals(this.arguments, other.arguments);
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash != 0) {
            return hash;
        }
        hash = super.hashCode();
        hash = 31 * hash + Arrays.hashCode(this.arguments);
        this.hash = hash = 31 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        return this.hash;
    }
}

