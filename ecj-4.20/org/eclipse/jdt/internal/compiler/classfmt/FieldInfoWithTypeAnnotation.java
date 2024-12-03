/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfo;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfoWithAnnotation;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;

public final class FieldInfoWithTypeAnnotation
extends FieldInfoWithAnnotation {
    private TypeAnnotationInfo[] typeAnnotations;

    FieldInfoWithTypeAnnotation(FieldInfo info, AnnotationInfo[] annos, TypeAnnotationInfo[] typeAnnos) {
        super(info, annos);
        this.typeAnnotations = typeAnnos;
    }

    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return this.typeAnnotations;
    }

    @Override
    protected void initialize() {
        int i = 0;
        int max = this.typeAnnotations.length;
        while (i < max) {
            this.typeAnnotations[i].initialize();
            ++i;
        }
        super.initialize();
    }

    @Override
    protected void reset() {
        if (this.typeAnnotations != null) {
            int i = 0;
            int max = this.typeAnnotations.length;
            while (i < max) {
                this.typeAnnotations[i].reset();
                ++i;
            }
        }
        super.reset();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        if (this.typeAnnotations != null) {
            buffer.append('\n');
            buffer.append("type annotations:");
            int i = 0;
            while (i < this.typeAnnotations.length) {
                buffer.append(this.typeAnnotations[i]);
                buffer.append('\n');
                ++i;
            }
        }
        this.toStringContent(buffer);
        return buffer.toString();
    }
}

