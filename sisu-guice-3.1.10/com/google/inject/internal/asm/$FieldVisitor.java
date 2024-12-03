/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$TypePath;

public abstract class $FieldVisitor {
    protected final int api;
    protected $FieldVisitor fv;

    public $FieldVisitor(int n) {
        this(n, null);
    }

    public $FieldVisitor(int n, $FieldVisitor $FieldVisitor) {
        if (n != 262144 && n != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = n;
        this.fv = $FieldVisitor;
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        if (this.fv != null) {
            return this.fv.visitAnnotation(string, bl);
        }
        return null;
    }

    public $AnnotationVisitor visitTypeAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.fv != null) {
            return this.fv.visitTypeAnnotation(n, $TypePath, string, bl);
        }
        return null;
    }

    public void visitAttribute($Attribute $Attribute) {
        if (this.fv != null) {
            this.fv.visitAttribute($Attribute);
        }
    }

    public void visitEnd() {
        if (this.fv != null) {
            this.fv.visitEnd();
        }
    }
}

