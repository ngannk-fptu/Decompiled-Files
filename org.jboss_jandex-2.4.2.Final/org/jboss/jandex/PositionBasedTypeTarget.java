/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeTarget;

public abstract class PositionBasedTypeTarget
extends TypeTarget {
    private short position;
    private boolean adjusted;

    PositionBasedTypeTarget(AnnotationTarget enclosingTarget, int position) {
        super(enclosingTarget);
        this.position = (short)position;
    }

    PositionBasedTypeTarget(AnnotationTarget enclosingTarget, Type target, int position) {
        super(enclosingTarget, target);
        this.position = (short)position;
    }

    void adjustUp() {
        if (!this.adjusted) {
            this.position = (short)(this.position + 1);
            this.adjusted = true;
        }
    }

    public final int position() {
        return this.position & 0xFFFF;
    }
}

