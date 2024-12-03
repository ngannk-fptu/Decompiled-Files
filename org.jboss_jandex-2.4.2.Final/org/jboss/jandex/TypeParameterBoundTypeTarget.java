/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeParameterTypeTarget;
import org.jboss.jandex.TypeTarget;

public class TypeParameterBoundTypeTarget
extends TypeParameterTypeTarget {
    private short boundPosition;
    private boolean adjusted;

    TypeParameterBoundTypeTarget(AnnotationTarget enclosingTarget, int position, int boundPosition) {
        super(enclosingTarget, position);
        this.boundPosition = (short)boundPosition;
    }

    TypeParameterBoundTypeTarget(AnnotationTarget enclosingTarget, Type target, int position, int boundPosition) {
        super(enclosingTarget, target, position);
        this.boundPosition = (short)boundPosition;
    }

    public final int boundPosition() {
        return this.boundPosition & 0xFFFF;
    }

    void adjustBoundDown() {
        if (!this.adjusted) {
            this.boundPosition = (short)(this.boundPosition - 1);
            this.adjusted = true;
        }
    }

    @Override
    public final TypeTarget.Usage usage() {
        return TypeTarget.Usage.TYPE_PARAMETER_BOUND;
    }

    @Override
    public TypeParameterBoundTypeTarget asTypeParameterBound() {
        return this;
    }
}

