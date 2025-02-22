/*
 * Decompiled with CFR 0.152.
 */
package jersey.repackaged.org.objectweb.asm;

import jersey.repackaged.org.objectweb.asm.AnnotationVisitor;
import jersey.repackaged.org.objectweb.asm.Attribute;
import jersey.repackaged.org.objectweb.asm.TypePath;

public abstract class FieldVisitor {
    protected final int api;
    protected FieldVisitor fv;

    public FieldVisitor(int api) {
        this(api, null);
    }

    public FieldVisitor(int api, FieldVisitor fv) {
        if (api != 262144 && api != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.fv = fv;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(desc, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.fv != null) {
            return this.fv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return null;
    }

    public void visitAttribute(Attribute attr) {
        if (this.fv != null) {
            this.fv.visitAttribute(attr);
        }
    }

    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}

