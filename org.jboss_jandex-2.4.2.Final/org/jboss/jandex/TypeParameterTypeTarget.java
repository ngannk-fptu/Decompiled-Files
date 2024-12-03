/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.PositionBasedTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public class TypeParameterTypeTarget
extends PositionBasedTypeTarget {
    TypeParameterTypeTarget(AnnotationTarget enclosingTarget, int position) {
        super(enclosingTarget, position);
    }

    TypeParameterTypeTarget(AnnotationTarget enclosingTarget, Type target, int position) {
        super(enclosingTarget, target, position);
    }

    @Override
    public TypeTarget.Usage usage() {
        return TypeTarget.Usage.TYPE_PARAMETER;
    }

    @Override
    public TypeParameterTypeTarget asTypeParameter() {
        return this;
    }
}

