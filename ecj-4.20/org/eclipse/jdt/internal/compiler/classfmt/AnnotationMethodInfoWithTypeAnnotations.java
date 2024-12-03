/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

class AnnotationMethodInfoWithTypeAnnotations
extends AnnotationMethodInfoWithAnnotations {
    private TypeAnnotationInfo[] typeAnnotations;

    AnnotationMethodInfoWithTypeAnnotations(MethodInfo methodInfo, Object defaultValue, AnnotationInfo[] annotations, TypeAnnotationInfo[] typeAnnotations) {
        super(methodInfo, defaultValue, annotations);
        this.typeAnnotations = typeAnnotations;
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.typeAnnotations;
    }

    @Override
    protected void initialize() {
        int i = 0;
        int l = this.typeAnnotations == null ? 0 : this.typeAnnotations.length;
        while (i < l) {
            this.typeAnnotations[i].initialize();
            ++i;
        }
        super.initialize();
    }

    @Override
    protected void reset() {
        int i = 0;
        int l = this.typeAnnotations == null ? 0 : this.typeAnnotations.length;
        while (i < l) {
            this.typeAnnotations[i].reset();
            ++i;
        }
        super.reset();
    }
}

