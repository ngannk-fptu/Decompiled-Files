/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$Handle;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$TypePath;

public abstract class $MethodVisitor {
    protected final int api;
    protected $MethodVisitor mv;

    public $MethodVisitor(int n) {
        this(n, null);
    }

    public $MethodVisitor(int n, $MethodVisitor $MethodVisitor) {
        if (n != 262144 && n != 327680) {
            throw new IllegalArgumentException();
        }
        this.api = n;
        this.mv = $MethodVisitor;
    }

    public void visitParameter(String string, int n) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.mv != null) {
            this.mv.visitParameter(string, n);
        }
    }

    public $AnnotationVisitor visitAnnotationDefault() {
        if (this.mv != null) {
            return this.mv.visitAnnotationDefault();
        }
        return null;
    }

    public $AnnotationVisitor visitAnnotation(String string, boolean bl) {
        if (this.mv != null) {
            return this.mv.visitAnnotation(string, bl);
        }
        return null;
    }

    public $AnnotationVisitor visitTypeAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.mv != null) {
            return this.mv.visitTypeAnnotation(n, $TypePath, string, bl);
        }
        return null;
    }

    public $AnnotationVisitor visitParameterAnnotation(int n, String string, boolean bl) {
        if (this.mv != null) {
            return this.mv.visitParameterAnnotation(n, string, bl);
        }
        return null;
    }

    public void visitAttribute($Attribute $Attribute) {
        if (this.mv != null) {
            this.mv.visitAttribute($Attribute);
        }
    }

    public void visitCode() {
        if (this.mv != null) {
            this.mv.visitCode();
        }
    }

    public void visitFrame(int n, int n2, Object[] objectArray, int n3, Object[] objectArray2) {
        if (this.mv != null) {
            this.mv.visitFrame(n, n2, objectArray, n3, objectArray2);
        }
    }

    public void visitInsn(int n) {
        if (this.mv != null) {
            this.mv.visitInsn(n);
        }
    }

    public void visitIntInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitIntInsn(n, n2);
        }
    }

    public void visitVarInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitVarInsn(n, n2);
        }
    }

    public void visitTypeInsn(int n, String string) {
        if (this.mv != null) {
            this.mv.visitTypeInsn(n, string);
        }
    }

    public void visitFieldInsn(int n, String string, String string2, String string3) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(n, string, string2, string3);
        }
    }

    public void visitMethodInsn(int n, String string, String string2, String string3) {
        if (this.mv != null) {
            this.mv.visitMethodInsn(n, string, string2, string3);
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, $Handle $Handle, Object ... objectArray) {
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(string, string2, $Handle, objectArray);
        }
    }

    public void visitJumpInsn(int n, $Label $Label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(n, $Label);
        }
    }

    public void visitLabel($Label $Label) {
        if (this.mv != null) {
            this.mv.visitLabel($Label);
        }
    }

    public void visitLdcInsn(Object object) {
        if (this.mv != null) {
            this.mv.visitLdcInsn(object);
        }
    }

    public void visitIincInsn(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitIincInsn(n, n2);
        }
    }

    public void visitTableSwitchInsn(int n, int n2, $Label $Label, $Label ... $LabelArray) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(n, n2, $Label, $LabelArray);
        }
    }

    public void visitLookupSwitchInsn($Label $Label, int[] nArray, $Label[] $LabelArray) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn($Label, nArray, $LabelArray);
        }
    }

    public void visitMultiANewArrayInsn(String string, int n) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(string, n);
        }
    }

    public $AnnotationVisitor visitInsnAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.mv != null) {
            return this.mv.visitInsnAnnotation(n, $TypePath, string, bl);
        }
        return null;
    }

    public void visitTryCatchBlock($Label $Label, $Label $Label2, $Label $Label3, String string) {
        if (this.mv != null) {
            this.mv.visitTryCatchBlock($Label, $Label2, $Label3, string);
        }
    }

    public $AnnotationVisitor visitTryCatchAnnotation(int n, $TypePath $TypePath, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.mv != null) {
            return this.mv.visitTryCatchAnnotation(n, $TypePath, string, bl);
        }
        return null;
    }

    public void visitLocalVariable(String string, String string2, String string3, $Label $Label, $Label $Label2, int n) {
        if (this.mv != null) {
            this.mv.visitLocalVariable(string, string2, string3, $Label, $Label2, n);
        }
    }

    public $AnnotationVisitor visitLocalVariableAnnotation(int n, $TypePath $TypePath, $Label[] $LabelArray, $Label[] $LabelArray2, int[] nArray, String string, boolean bl) {
        if (this.api < 327680) {
            throw new RuntimeException();
        }
        if (this.mv != null) {
            return this.mv.visitLocalVariableAnnotation(n, $TypePath, $LabelArray, $LabelArray2, nArray, string, bl);
        }
        return null;
    }

    public void visitLineNumber(int n, $Label $Label) {
        if (this.mv != null) {
            this.mv.visitLineNumber(n, $Label);
        }
    }

    public void visitMaxs(int n, int n2) {
        if (this.mv != null) {
            this.mv.visitMaxs(n, n2);
        }
    }

    public void visitEnd() {
        if (this.mv != null) {
            this.mv.visitEnd();
        }
    }
}

