/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type.classreading;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.classreading.AbstractRecursiveAnnotationVisitor;
import org.springframework.lang.Nullable;

@Deprecated
class RecursiveAnnotationAttributesVisitor
extends AbstractRecursiveAnnotationVisitor {
    protected final String annotationType;

    public RecursiveAnnotationAttributesVisitor(String annotationType, AnnotationAttributes attributes, @Nullable ClassLoader classLoader) {
        super(classLoader, attributes);
        this.annotationType = annotationType;
    }

    @Override
    public void visitEnd() {
        AnnotationUtils.registerDefaultValues(this.attributes);
    }
}

