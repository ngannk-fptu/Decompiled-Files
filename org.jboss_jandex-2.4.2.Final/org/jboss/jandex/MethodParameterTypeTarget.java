/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.PositionBasedTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public class MethodParameterTypeTarget
extends PositionBasedTypeTarget {
    MethodParameterTypeTarget(MethodInfo enclosingTarget, int position) {
        super((AnnotationTarget)enclosingTarget, position);
    }

    MethodParameterTypeTarget(AnnotationTarget enclosingTarget, Type target, int position) {
        super(enclosingTarget, target, position);
    }

    @Override
    public final TypeTarget.Usage usage() {
        return TypeTarget.Usage.METHOD_PARAMETER;
    }

    @Override
    public MethodInfo enclosingTarget() {
        return (MethodInfo)super.enclosingTarget();
    }

    @Override
    public MethodParameterTypeTarget asMethodParameterType() {
        return this;
    }
}

