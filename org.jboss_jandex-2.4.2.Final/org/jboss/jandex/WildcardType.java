/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public class WildcardType
extends Type {
    private static Type OBJECT = new ClassType(DotName.OBJECT_NAME);
    private final boolean isExtends;
    private final Type bound;
    private int hash;

    public static WildcardType create(Type bound, boolean isExtends) {
        return new WildcardType(bound, isExtends);
    }

    WildcardType(Type bound, boolean isExtends) {
        this(bound, isExtends, null);
    }

    WildcardType(Type bound, boolean isExtends, AnnotationInstance[] annotations) {
        super(isExtends && bound != null ? bound.name() : DotName.OBJECT_NAME, annotations);
        this.bound = isExtends && bound == null ? OBJECT : bound;
        this.isExtends = isExtends;
    }

    public Type extendsBound() {
        return this.isExtends ? this.bound : OBJECT;
    }

    public Type superBound() {
        return this.isExtends ? null : this.bound;
    }

    Type bound() {
        return this.bound;
    }

    boolean isExtends() {
        return this.isExtends;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.WILDCARD_TYPE;
    }

    @Override
    public WildcardType asWildcardType() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.appendAnnotations(builder);
        builder.append('?');
        if (this.isExtends && this.bound != OBJECT) {
            builder.append(" extends ").append(this.bound);
        }
        if (!this.isExtends && this.bound != null) {
            builder.append(" super ").append(this.bound);
        }
        return builder.toString();
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new WildcardType(this.bound, this.isExtends, newAnnotations);
    }

    Type copyType(Type bound) {
        return new WildcardType(bound, this.isExtends, this.annotationArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        WildcardType other = (WildcardType)o;
        return this.isExtends == other.isExtends && this.bound.equals(other.bound);
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash != 0) {
            return hash;
        }
        hash = super.hashCode();
        hash = 31 * hash + (this.isExtends ? 1 : 0);
        this.hash = hash = 31 * hash + this.bound.hashCode();
        return this.hash;
    }
}

