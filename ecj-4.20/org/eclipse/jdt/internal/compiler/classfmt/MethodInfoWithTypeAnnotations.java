/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithParameterAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

class MethodInfoWithTypeAnnotations
extends MethodInfoWithParameterAnnotations {
    private TypeAnnotationInfo[] typeAnnotations;

    MethodInfoWithTypeAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations, AnnotationInfo[][] parameterAnnotations, TypeAnnotationInfo[] typeAnnotations) {
        super(methodInfo, annotations, parameterAnnotations);
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

