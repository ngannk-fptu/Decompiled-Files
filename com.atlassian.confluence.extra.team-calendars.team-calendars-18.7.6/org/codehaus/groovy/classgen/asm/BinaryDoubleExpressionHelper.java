/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BinaryLongExpressionHelper;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.BytecodeInterface8;

public class BinaryDoubleExpressionHelper
extends BinaryLongExpressionHelper {
    private static final MethodCaller doubleArrayGet = MethodCaller.newStatic(BytecodeInterface8.class, "dArrayGet");
    private static final MethodCaller doubleArraySet = MethodCaller.newStatic(BytecodeInterface8.class, "dArraySet");
    private static final int[] stdOperations = new int[]{99, 103, 107, 111, 111, 115};

    public BinaryDoubleExpressionHelper(WriterController controller) {
        super(controller);
    }

    @Override
    protected MethodCaller getArrayGetCaller() {
        return doubleArrayGet;
    }

    @Override
    protected MethodCaller getArraySetCaller() {
        return doubleArraySet;
    }

    @Override
    protected boolean writeBitwiseOp(int op, boolean simulate) {
        if (!simulate) {
            throw new GroovyBugError("should not reach here");
        }
        return false;
    }

    @Override
    protected int getBitwiseOperationBytecode(int op) {
        return -1;
    }

    @Override
    protected int getCompareCode() {
        return 152;
    }

    @Override
    protected ClassNode getNormalOpResultType() {
        return ClassHelper.double_TYPE;
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
    protected void writeMinusMinus(MethodVisitor mv) {
        mv.visitInsn(15);
        mv.visitInsn(103);
    }

    @Override
    protected void writePlusPlus(MethodVisitor mv) {
        mv.visitInsn(15);
        mv.visitInsn(99);
    }

    @Override
    protected ClassNode getDevisionOpResultType() {
        return ClassHelper.double_TYPE;
    }

    @Override
    protected boolean supportsDivision() {
        return true;
    }
}

