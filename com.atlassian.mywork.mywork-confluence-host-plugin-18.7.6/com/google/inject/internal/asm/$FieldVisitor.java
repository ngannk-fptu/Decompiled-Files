/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$Attribute;

public interface $FieldVisitor {
    public .AnnotationVisitor visitAnnotation(String var1, boolean var2);

    public void visitAttribute($Attribute var1);

    public void visitEnd();
}

