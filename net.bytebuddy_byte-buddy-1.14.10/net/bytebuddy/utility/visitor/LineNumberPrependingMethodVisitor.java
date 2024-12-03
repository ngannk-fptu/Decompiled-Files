/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility.visitor;

import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.visitor.ExceptionTableSensitiveMethodVisitor;

public class LineNumberPrependingMethodVisitor
extends ExceptionTableSensitiveMethodVisitor {
    private final Label startOfMethod = new Label();
    private boolean prependLineNumber = true;

    public LineNumberPrependingMethodVisitor(MethodVisitor methodVisitor) {
        super(OpenedClassReader.ASM_API, methodVisitor);
    }

    protected void onAfterExceptionTable() {
        super.visitLabel(this.startOfMethod);
    }

    public void visitLineNumber(int line, Label start) {
        if (this.prependLineNumber) {
            start = this.startOfMethod;
            this.prependLineNumber = false;
        }
        super.visitLineNumber(line, start);
    }
}

