/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility.visitor;

import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;

public abstract class ExceptionTableSensitiveMethodVisitor
extends MethodVisitor {
    private boolean trigger = true;

    protected ExceptionTableSensitiveMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    private void considerEndOfExceptionTable() {
        if (this.trigger) {
            this.trigger = false;
            this.onAfterExceptionTable();
        }
    }

    protected abstract void onAfterExceptionTable();

    public final void visitFrame(int type, int localVariableLength, Object[] localVariable, int stackSize, Object[] stack) {
        this.considerEndOfExceptionTable();
        this.onVisitFrame(type, localVariableLength, localVariable, stackSize, stack);
    }

    protected void onVisitFrame(int type, int localVariableLength, Object[] localVariable, int stackSize, Object[] stack) {
        super.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
    }

    public final void visitLabel(Label label) {
        this.considerEndOfExceptionTable();
        this.onVisitLabel(label);
    }

    protected void onVisitLabel(Label label) {
        super.visitLabel(label);
    }

    public final void visitIntInsn(int opcode, int operand) {
        this.considerEndOfExceptionTable();
        this.onVisitIntInsn(opcode, operand);
    }

    protected void onVisitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
    }

    public final void visitVarInsn(int opcode, int offset) {
        this.considerEndOfExceptionTable();
        this.onVisitVarInsn(opcode, offset);
    }

    protected void onVisitVarInsn(int opcode, int offset) {
        super.visitVarInsn(opcode, offset);
    }

    public final void visitTypeInsn(int opcode, String type) {
        this.considerEndOfExceptionTable();
        this.onVisitTypeInsn(opcode, type);
    }

    protected void onVisitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
    }

    public final void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.considerEndOfExceptionTable();
        this.onVisitFieldInsn(opcode, owner, name, descriptor);
    }

    protected void onVisitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    public final void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        this.considerEndOfExceptionTable();
        this.onVisitMethodInsn(opcode, owner, name, descriptor);
    }

    @Deprecated
    protected void onVisitMethodInsn(int opcode, String owner, String name, String descriptor) {
        super.visitMethodInsn(opcode, owner, name, descriptor);
    }

    public final void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.considerEndOfExceptionTable();
        this.onVisitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    protected void onVisitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    public final void visitInvokeDynamicInsn(String name, String descriptor, Handle handle, Object ... argument) {
        this.considerEndOfExceptionTable();
        this.onVisitInvokeDynamicInsn(name, descriptor, handle, argument);
    }

    protected void onVisitInvokeDynamicInsn(String name, String descriptor, Handle handle, Object ... argument) {
        super.visitInvokeDynamicInsn(name, descriptor, handle, argument);
    }

    public final void visitJumpInsn(int opcode, Label label) {
        this.considerEndOfExceptionTable();
        this.onVisitJumpInsn(opcode, label);
    }

    protected void onVisitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
    }

    public final void visitLdcInsn(Object constant) {
        this.considerEndOfExceptionTable();
        this.onVisitLdcInsn(constant);
    }

    protected void onVisitLdcInsn(Object constant) {
        super.visitLdcInsn(constant);
    }

    public final void visitIincInsn(int offset, int increment) {
        this.considerEndOfExceptionTable();
        this.onVisitIincInsn(offset, increment);
    }

    protected void onVisitIincInsn(int offset, int increment) {
        super.visitIincInsn(offset, increment);
    }

    public final void visitTableSwitchInsn(int minimum, int maximum, Label defaultTarget, Label ... label) {
        this.considerEndOfExceptionTable();
        this.onVisitTableSwitchInsn(minimum, maximum, defaultTarget, label);
    }

    protected void onVisitTableSwitchInsn(int minimum, int maximum, Label defaultTarget, Label ... label) {
        super.visitTableSwitchInsn(minimum, maximum, defaultTarget, label);
    }

    public final void visitLookupSwitchInsn(Label dflt, int[] key, Label[] label) {
        this.considerEndOfExceptionTable();
        this.onVisitLookupSwitchInsn(dflt, key, label);
    }

    protected void onVisitLookupSwitchInsn(Label defaultTarget, int[] key, Label[] label) {
        super.visitLookupSwitchInsn(defaultTarget, key, label);
    }

    public final void visitMultiANewArrayInsn(String descriptor, int dimensions) {
        this.considerEndOfExceptionTable();
        this.onVisitMultiANewArrayInsn(descriptor, dimensions);
    }

    protected void onVisitMultiANewArrayInsn(String descriptor, int dimensions) {
        super.visitMultiANewArrayInsn(descriptor, dimensions);
    }

    public final void visitInsn(int opcode) {
        this.considerEndOfExceptionTable();
        this.onVisitInsn(opcode);
    }

    protected void onVisitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}

