/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

public final class ClassType
extends Type {
    public static final ClassType OBJECT_TYPE = new ClassType(DotName.OBJECT_NAME);

    ClassType(DotName name) {
        this(name, null);
    }

    ClassType(DotName name, AnnotationInstance[] annotations) {
        super(name, annotations);
    }

    @Override
    public Type.Kind kind() {
        return Type.Kind.CLASS;
    }

    @Override
    public ClassType asClassType() {
        return this;
    }

    @Override
    Type copyType(AnnotationInstance[] newAnnotations) {
        return new ClassType(this.name(), newAnnotations);
    }

    ParameterizedType toParameterizedType() {
        return new ParameterizedType(this.name(), null, null, this.annotationArray());
    }
}

