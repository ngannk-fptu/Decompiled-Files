/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class AnnotationMethodInfoWithAnnotations
extends AnnotationMethodInfo {
    private AnnotationInfo[] annotations;

    AnnotationMethodInfoWithAnnotations(MethodInfo methodInfo, Object defaultValue, AnnotationInfo[] annotations) {
        super(methodInfo, defaultValue);
        this.annotations = annotations;
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    protected void initialize() {
        int i = 0;
        int l = this.annotations == null ? 0 : this.annotations.length;
        while (i < l) {
            if (this.annotations[i] != null) {
                this.annotations[i].initialize();
            }
            ++i;
        }
        super.initialize();
    }

    @Override
    protected void reset() {
        int i = 0;
        int l = this.annotations == null ? 0 : this.annotations.length;
        while (i < l) {
            if (this.annotations[i] != null) {
                this.annotations[i].reset();
            }
            ++i;
        }
        super.reset();
    }
}

