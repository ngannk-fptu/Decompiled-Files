/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public class VoidType
extends Type {
    static final VoidType VOID = new VoidType(null);

    private VoidType(AnnotationInstance[] annotations) {
        super(new DotName(null, "void", true, false), annotations);
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.VOID;
    }

    @Override
    public VoidType asVoidType() {
        return this;
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new VoidType(newAnnotations);
    }
}

