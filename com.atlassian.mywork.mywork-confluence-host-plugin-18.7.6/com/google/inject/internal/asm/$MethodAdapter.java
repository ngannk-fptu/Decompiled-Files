/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;

public class $MethodAdapter
implements $MethodVisitor {
    protected $MethodVisitor mv;

    public $MethodAdapter($MethodVisitor $MethodVisitor) {
        this.mv = $MethodVisitor;
    }

    public $AnnotationVisitor visitAnnotationDefault() {
        return this.mv.visitAnnotationDefault();
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        return this.mv.visitAnnotation(string, bl);
    }

    public $AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        return this.mv.visitParameterAnnotation(n, string, bl);
    }

    public void visitAttribute($Attribute $Attribute) {
        this.mv.visitAttribute($Attribute);
    }

    public void visitCode() {
        this.mv.visitCode();
    }

    public void visitFrame(int n, int n2, Object[] objectArray, int n3, Object[] objectArray2) {
        this.mv.visitFrame(n, n2, objectArray, n3, objectArray2);
    }

    public void visitInsn(int n) {
        this.mv.visitInsn(n);
    }

    public void visitIntInsn(int n, int n2) {
        this.mv.visitIntInsn(n, n2);
    }

    public void visitVarInsn(int n, int n2) {
        this.mv.visitVarInsn(n, n2);
    }

    public void visitTypeInsn(int n, String string) {
        this.mv.visitTypeInsn(n, string);
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        this.mv.visitFieldInsn(n, string, string2, string3);
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        this.mv.visitMethodInsn(n, string, string2, string3);
    }

    public void visitJumpInsn(int n, $Label $Label) {
        this.mv.visitJumpInsn(n, $Label);
    }

    public void visitLabel($Label $Label) {
        this.mv.visitLabel($Label);
    }

    public void visitLdcInsn(Object object) {
        this.mv.visitLdcInsn(object);
    }

    public void visitIincInsn(int n, int n2) {
        this.mv.visitIincInsn(n, n2);
    }

    public void visitTableSwitchInsn(int n, int n2, $Label $Label, $Label[] $LabelArray) {
        this.mv.visitTableSwitchInsn(n, n2, $Label, $LabelArray);
    }

    public void visitLookupSwitchInsn($Label $Label, int[] nArray, $Label[] $LabelArray) {
        this.mv.visitLookupSwitchInsn($Label, nArray, $LabelArray);
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        this.mv.visitMultiANewArrayInsn(string, n);
    }

    public void visitTryCatchBlock($Label $Label, $Label $Label2, $Label $Label3, String string) {
        this.mv.visitTryCatchBlock($Label, $Label2, $Label3, string);
    }

    public void visitLocalVariable(String string, String string2, String string3, $Label $Label, $Label $Label2, int n) {
        this.mv.visitLocalVariable(string, string2, string3, $Label, $Label2, n);
    }

    public void visitLineNumber(int n, $Label $Label) {
        this.mv.visitLineNumber(n, $Label);
    }

    public void visitMaxs(int n, int n2) {
        this.mv.visitMaxs(n, n2);
    }

    public void visitEnd() {
        this.mv.visitEnd();
    }
}

