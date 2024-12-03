/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedTypeMunger;

public class AnnotationOnTypeMunger
extends ResolvedTypeMunger {
    AnnotationAJ newAnnotation;
    private volatile int hashCode = 0;

    public AnnotationOnTypeMunger(AnnotationAJ anno) {
        super(AnnotationOnType, null);
        this.newAnnotation = anno;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new RuntimeException("unimplemented");
    }

    public AnnotationAJ getNewAnnotation() {
        return this.newAnnotation;
    }

    public boolean equals(Object other) {
        if (!(other instanceof AnnotationOnTypeMunger)) {
            return false;
        }
        AnnotationOnTypeMunger o = (AnnotationOnTypeMunger)other;
        return this.newAnnotation.getTypeSignature().equals(o.newAnnotation.getTypeSignature());
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            this.hashCode = result = 37 * result + this.newAnnotation.getTypeSignature().hashCode();
        }
        return this.hashCode;
    }
}

