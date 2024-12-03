/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public abstract class BinaryExpressionWriter {
    private WriterController controller;
    protected static final int[] stdCompareCodes = new int[]{153, 154, 153, 154, 156, 157, 158, 155};

    public BinaryExpressionWriter(WriterController controller) {
        this.controller = controller;
    }

    protected abstract int getCompareCode();

    protected boolean writeStdCompare(int type, boolean simulate) {
        if ((type -= 120) < 0 || type > 7) {
            return false;
        }
        if (!simulate) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            OperandStack operandStack = this.controller.getOperandStack();
            int bytecode = stdCompareCodes[type];
            mv.visitInsn(this.getCompareCode());
            Label l1 = new Label();
            mv.visitJumpInsn(bytecode, l1);
            mv.visitInsn(4);
            Label l2 = new Label();
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l1);
            mv.visitInsn(3);
            mv.visitLabel(l2);
            operandStack.replace(ClassHelper.boolean_TYPE, 2);
        }
        return true;
    }

    protected abstract void doubleTwoOperands(MethodVisitor var1);

    protected abstract void removeTwoOperands(MethodVisitor var1);

    protected boolean writeSpaceship(int type, boolean simulate) {
        if (type != 128) {
            return false;
        }
        if (!simulate) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            this.doubleTwoOperands(mv);
            Label l1 = new Label();
            mv.visitInsn(this.getCompareCode());
            mv.visitJumpInsn(156, l1);
            this.removeTwoOperands(mv);
            mv.visitInsn(2);
            Label l2 = new Label();
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l1);
            Label l3 = new Label();
            mv.visitInsn(this.getCompareCode());
            mv.visitJumpInsn(154, l3);
            mv.visitInsn(3);
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l3);
            mv.visitInsn(4);
            this.controller.getOperandStack().replace(ClassHelper.int_TYPE, 2);
        }
        return true;
    }

    protected abstract ClassNode getNormalOpResultType();

    protected abstract int getStandardOperationBytecode(int var1);

    protected boolean writeStdOperators(int type, boolean simulate) {
        if ((type -= 200) < 0 || type > 5 || type == 3) {
            return false;
        }
        if (!simulate) {
            int bytecode = this.getStandardOperationBytecode(type);
            this.controller.getMethodVisitor().visitInsn(bytecode);
            this.controller.getOperandStack().replace(this.getNormalOpResultType(), 2);
        }
        return true;
    }

    protected boolean writeDivision(boolean simulate) {
        if (!this.supportsDivision()) {
            return false;
        }
        if (!simulate) {
            int bytecode = this.getStandardOperationBytecode(3);
            this.controller.getMethodVisitor().visitInsn(bytecode);
            this.controller.getOperandStack().replace(this.getDevisionOpResultType(), 2);
        }
        return true;
    }

    protected boolean supportsDivision() {
        return false;
    }

    protected abstract ClassNode getDevisionOpResultType();

    protected abstract int getBitwiseOperationBytecode(int var1);

    protected boolean writeBitwiseOp(int type, boolean simulate) {
        if ((type -= 340) < 0 || type > 2) {
            return false;
        }
        if (!simulate) {
            int bytecode = this.getBitwiseOperationBytecode(type);
            this.controller.getMethodVisitor().visitInsn(bytecode);
            this.controller.getOperandStack().replace(this.getNormalOpResultType(), 2);
        }
        return true;
    }

    protected abstract int getShiftOperationBytecode(int var1);

    protected boolean writeShiftOp(int type, boolean simulate) {
        if ((type -= 280) < 0 || type > 2) {
            return false;
        }
        if (!simulate) {
            int bytecode = this.getShiftOperationBytecode(type);
            this.controller.getMethodVisitor().visitInsn(bytecode);
            this.controller.getOperandStack().replace(this.getNormalOpResultType(), 2);
        }
        return true;
    }

    public boolean write(int operation, boolean simulate) {
        return this.writeStdCompare(operation, simulate) || this.writeSpaceship(operation, simulate) || this.writeStdOperators(operation, simulate) || this.writeBitwiseOp(operation, simulate) || this.writeShiftOp(operation, simulate);
    }

    protected abstract MethodCaller getArrayGetCaller();

    protected ClassNode getArrayGetResultType() {
        return this.getNormalOpResultType();
    }

    protected abstract MethodCaller getArraySetCaller();

    public boolean arrayGet(int operation, boolean simulate) {
        if (operation != 30) {
            return false;
        }
        if (!simulate) {
            this.getArrayGetCaller().call(this.controller.getMethodVisitor());
        }
        return true;
    }

    public boolean arraySet(boolean simulate) {
        if (!simulate) {
            this.getArraySetCaller().call(this.controller.getMethodVisitor());
        }
        return true;
    }

    public boolean writePostOrPrefixMethod(int operation, boolean simulate) {
        if (operation != 250 && operation != 260) {
            return false;
        }
        if (!simulate) {
            MethodVisitor mv = this.controller.getMethodVisitor();
            if (operation == 250) {
                this.writePlusPlus(mv);
            } else {
                this.writeMinusMinus(mv);
            }
        }
        return true;
    }

    protected abstract void writePlusPlus(MethodVisitor var1);

    protected abstract void writeMinusMinus(MethodVisitor var1);
}

