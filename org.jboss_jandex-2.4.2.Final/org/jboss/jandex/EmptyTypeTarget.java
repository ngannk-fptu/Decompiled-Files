/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public class EmptyTypeTarget
extends TypeTarget {
    private boolean receiver;

    EmptyTypeTarget(AnnotationTarget enclosingTarget, boolean receiver) {
        super(enclosingTarget);
        this.receiver = receiver;
    }

    EmptyTypeTarget(AnnotationTarget enclosingTarget, Type target, boolean receiver) {
        super(enclosingTarget, target);
        this.receiver = receiver;
    }

    public boolean isReceiver() {
        return this.receiver;
    }

    @Override
    public final TypeTarget.Usage usage() {
        return TypeTarget.Usage.EMPTY;
    }

    @Override
    public EmptyTypeTarget asEmpty() {
        return this;
    }
}

