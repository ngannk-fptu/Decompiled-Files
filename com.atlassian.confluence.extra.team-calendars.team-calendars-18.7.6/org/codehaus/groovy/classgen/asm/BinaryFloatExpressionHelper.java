/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BinaryExpressionWriter;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.BytecodeInterface8;

public class BinaryFloatExpressionHelper
extends BinaryExpressionWriter {
    private static final MethodCaller floatArrayGet = MethodCaller.newStatic(BytecodeInterface8.class, "fArrayGet");
    private static final MethodCaller floatArraySet = MethodCaller.newStatic(BytecodeInterface8.class, "fArraySet");
    private static final int[] stdOperations = new int[]{98, 102, 106, 0, 110, 114};

    public BinaryFloatExpressionHelper(WriterController controller) {
        super(controller);
    }

    @Override
    protected void doubleTwoOperands(MethodVisitor mv) {
        mv.visitInsn(92);
    }

    @Override
    protected MethodCaller getArrayGetCaller() {
        return floatArrayGet;
    }

    @Override
    protected MethodCaller getArraySetCaller() {
        return floatArraySet;
    }

    @Override
    protected boolean writeBitwiseOp(int type, boolean simulate) {
        if (!simulate) {
            throw new GroovyBugError("should not reach here");
        }
        return false;
    }

    @Override
    protected int getBitwiseOperationBytecode(int type) {
        return -1;
    }

    @Override
    protected int getCompareCode() {
        return 150;
    }

    @Override
    protected ClassNode getNormalOpResultType() {
        return ClassHelper.float_TYPE;
    }

    @Override
    protected boolean writeShiftOp(int type, boolean simulate) {
        if (!simulate) {
            throw new GroovyBugError("should not reach here");
        }
        return false;
    }

    @Override
    protected int getShiftOperationBytecode(int type) {
        return -1;
    }

    @Override
    protected int getStandardOperationBytecode(int type) {
        return stdOperations[type];
    }

    @Override
    protected void removeTwoOperands(MethodVisitor mv) {
        mv.visitInsn(88);
    }

    @Override
    protected void writeMinusMinus(MethodVisitor mv) {
        mv.visitInsn(12);
        mv.visitInsn(102);
    }

    @Override
    protected void writePlusPlus(MethodVisitor mv) {
        mv.visitInsn(12);
        mv.visitInsn(98);
    }

    @Override
    protected ClassNode getDevisionOpResultType() {
        return ClassHelper.BigDecimal_TYPE;
    }
}

