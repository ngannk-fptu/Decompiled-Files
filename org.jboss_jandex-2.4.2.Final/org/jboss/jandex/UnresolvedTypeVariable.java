/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public final class UnresolvedTypeVariable
extends Type {
    private final String name;
    private int hash;

    UnresolvedTypeVariable(String name) {
        this(name, null);
    }

    UnresolvedTypeVariable(String name, AnnotationInstance[] annotations) {
        super(DotName.OBJECT_NAME, annotations);
        this.name = name;
    }

    public String identifier() {
        return this.name;
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.UNRESOLVED_TYPE_VARIABLE;
    }

    @Override
    public UnresolvedTypeVariable asUnresolvedTypeVariable() {
        return this;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new UnresolvedTypeVariable(this.name, newAnnotations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnresolvedTypeVariable)) {
            return false;
        }
        UnresolvedTypeVariable other = (UnresolvedTypeVariable)o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}

