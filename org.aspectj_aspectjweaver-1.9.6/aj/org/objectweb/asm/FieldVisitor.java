/*
 * Decompiled with CFR 0.152.
 */
package aj.org.objectweb.asm;

import aj.org.objectweb.asm.AnnotationVisitor;
import aj.org.objectweb.asm.Attribute;
import aj.org.objectweb.asm.Constants;
import aj.org.objectweb.asm.TypePath;

public abstract class FieldVisitor {
    protected final int api;
    protected FieldVisitor fv;

    public FieldVisitor(int api) {
        this(api, null);
    }

    public FieldVisitor(int api, FieldVisitor fieldVisitor) {
        if (api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 0x1090000) {
            throw new IllegalArgumentException("Unsupported api " + api);
        }
        if (api == 0x1090000) {
            Constants.checkAsmExperimental(this);
        }
        this.api = api;
        this.fv = fieldVisitor;
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(descriptor, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException("This feature requires ASM5");
        }
        if (this.fv != null) {
            return this.fv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitAttribute(Attribute attribute) {
        if (this.fv != null) {
            this.fv.visitAttribute(attribute);
        }
    }

    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}

