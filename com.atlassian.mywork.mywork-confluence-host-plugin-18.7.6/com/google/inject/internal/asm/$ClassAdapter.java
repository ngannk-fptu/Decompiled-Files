/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$MethodVisitor;

public class $ClassAdapter
implements $ClassVisitor {
    protected $ClassVisitor cv;

    public $ClassAdapter($ClassVisitor $ClassVisitor) {
        this.cv = $ClassVisitor;
    }

    public void visit(int n, int n2, String string, String string2, String string3, String[] stringArray) {
        this.cv.visit(n, n2, string, string2, string3, stringArray);
    }

    public void visitSource(String string, String string2) {
        this.cv.visitSource(string, string2);
    }

    public void visitOuterClass(String string, String string2, String string3) {
        this.cv.visitOuterClass(string, string2, string3);
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        return this.cv.visitAnnotation(string, bl);
    }

    public void visitAttribute($Attribute $Attribute) {
        this.cv.visitAttribute($Attribute);
    }

    public void visitInnerClass(String string, String string2, String string3, int n) {
        this.cv.visitInnerClass(string, string2, string3, n);
    }

    public $FieldVisitor visitField(int n, String string, String string2, String string3, Object object) {
        return this.cv.visitField(n, string, string2, string3, object);
    }

    public $MethodVisitor visitMethod(int n, String string, String string2, String string3, String[] stringArray) {
        return this.cv.visitMethod(n, string, string2, string3, stringArray);
    }

    public void visitEnd() {
        this.cv.visitEnd();
    }
}

