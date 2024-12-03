/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.PositionBasedTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public class ThrowsTypeTarget
extends PositionBasedTypeTarget {
    ThrowsTypeTarget(MethodInfo enclosingTarget, int position) {
        super((AnnotationTarget)enclosingTarget, position);
    }

    ThrowsTypeTarget(AnnotationTarget enclosingTarget, Type target, int position) {
        super(enclosingTarget, target, position);
    }

    @Override
    public TypeTarget.Usage usage() {
        return TypeTarget.Usage.THROWS;
    }

    @Override
    public MethodInfo enclosingTarget() {
        return (MethodInfo)super.enclosingTarget();
    }

    @Override
    public ThrowsTypeTarget asThrows() {
        return this;
    }
}

