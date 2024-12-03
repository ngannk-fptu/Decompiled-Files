/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$TypePath;

public abstract class $ClassVisitor {
    protected final int api;
    protected $ClassVisitor cv;

    public $ClassVisitor(int n) {
        this(n, null);
    }

    public $ClassVisitor(int n, $ClassVisitor $ClassVisitor) {
        if (n != 262144 && n != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = n;
        this.cv = $ClassVisitor;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] stringArray) {
        if (this.cv != null) {
            this.cv.visit(n, n2, string, string2, string3, stringArray);
        }
    }

    public void visitSource(String string, String string2) {
        if (this.cv != null) {
            this.cv.visitSource(string, string2);
        }
    }

    public void visitOuterClass(String string, String string2, String string3) {
        if (this.cv != null) {
            this.cv.visitOuterClass(string, string2, string3);
        }
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        if (this.cv != null) {
            return this.cv.visitAnnotation(string, bl);
        }
        return null;
    }

    public $AnnotationVisitor visitTypeAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.cv != null) {
            return this.cv.visitTypeAnnotation(n, $TypePath, string, bl);
        }
        return null;
    }

    public void visitAttribute($Attribute $Attribute) {
        if (this.cv != null) {
            this.cv.visitAttribute($Attribute);
        }
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        if (this.cv != null) {
            this.cv.visitInnerClass(string, string2, string3, n);
        }
    }

    public $FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        if (this.cv != null) {
            return this.cv.visitField(n, string, string2, string3, object);
        }
        return null;
    }

    public $MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] stringArray) {
        if (this.cv != null) {
            return this.cv.visitMethod(n, string, string2, string3, stringArray);
        }
        return null;
    }

    public void visitEnd() {
        if (this.cv != null) {
            this.cv.visitEnd();
        }
    }
}

