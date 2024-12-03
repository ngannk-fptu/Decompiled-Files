/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.FieldInfo;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class FieldInfoWithAnnotation
extends FieldInfo {
    private AnnotationInfo[] annotations;

    FieldInfoWithAnnotation(FieldInfo info, AnnotationInfo[] annos) {
        super(info.reference, info.constantPoolOffsets, info.structOffset, info.version);
        this.accessFlags = info.accessFlags;
        this.attributeBytes = info.attributeBytes;
        this.constant = info.constant;
        this.constantPoolOffsets = info.constantPoolOffsets;
        this.descriptor = info.descriptor;
        this.name = info.name;
        this.signature = info.signature;
        this.signatureUtf8Offset = info.signatureUtf8Offset;
        this.tagBits = info.tagBits;
        this.wrappedConstantValue = info.wrappedConstantValue;
        this.annotations = annos;
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    protected void initialize() {
        if (this.annotations != null) {
            int i = 0;
            int max = this.annotations.length;
            while (i < max) {
                this.annotations[i].initialize();
                ++i;
            }
        }
        super.initialize();
    }

    @Override
    protected void reset() {
        if (this.annotations != null) {
            int i = 0;
            int max = this.annotations.length;
            while (i < max) {
                this.annotations[i].reset();
                ++i;
            }
        }
        super.reset();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        if (this.annotations != null) {
            buffer.append('\n');
            int i = 0;
            while (i < this.annotations.length) {
                buffer.append(this.annotations[i]);
                buffer.append('\n');
                ++i;
            }
        }
        this.toStringContent(buffer);
        return buffer.toString();
    }
}

