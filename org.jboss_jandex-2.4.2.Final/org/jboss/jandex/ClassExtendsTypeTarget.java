/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.PositionBasedTypeTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public class ClassExtendsTypeTarget
extends PositionBasedTypeTarget {
    ClassExtendsTypeTarget(ClassInfo enclosingTarget, int position) {
        super((AnnotationTarget)enclosingTarget, position);
    }

    public ClassExtendsTypeTarget(AnnotationTarget enclosingTarget, Type target, int position) {
        super(enclosingTarget, target, position);
    }

    @Override
    public final TypeTarget.Usage usage() {
        return TypeTarget.Usage.CLASS_EXTENDS;
    }

    @Override
    public ClassInfo enclosingTarget() {
        return (ClassInfo)super.enclosingTarget();
    }

    @Override
    public ClassExtendsTypeTarget asClassExtends() {
        return this;
    }
}

