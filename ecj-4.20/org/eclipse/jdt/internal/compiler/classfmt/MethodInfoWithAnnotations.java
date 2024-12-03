/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class MethodInfoWithAnnotations
extends MethodInfo {
    protected AnnotationInfo[] annotations;

    MethodInfoWithAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations) {
        super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset, methodInfo.version);
        this.annotations = annotations;
        this.accessFlags = methodInfo.accessFlags;
        this.attributeBytes = methodInfo.attributeBytes;
        this.descriptor = methodInfo.descriptor;
        this.exceptionNames = methodInfo.exceptionNames;
        this.name = methodInfo.name;
        this.signature = methodInfo.signature;
        this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
        this.tagBits = methodInfo.tagBits;
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

