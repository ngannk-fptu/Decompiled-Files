/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BinaryExpressionWriter;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.BytecodeInterface8;

public class BinaryLongExpressionHelper
extends BinaryExpressionWriter {
    private static final MethodCaller longArrayGet = MethodCaller.newStatic(BytecodeInterface8.class, "lArrayGet");
    private static final MethodCaller longArraySet = MethodCaller.newStatic(BytecodeInterface8.class, "lArraySet");
    private static final int[] bitOp = new int[]{129, 127, 131};
    private static final int[] shiftOp = new int[]{121, 123, 125};
    private static final int[] stdOperations = new int[]{97, 101, 105, 109, 109, 113};

    public BinaryLongExpressionHelper(WriterController controller) {
        super(controller);
    }

    @Override
    protected void doubleTwoOperands(MethodVisitor mv) {
        mv.visitInsn(93);
        mv.visitInsn(88);
        mv.visitInsn(93);
        mv.visitInsn(93);
        mv.visitInsn(88);
        mv.visitInsn(93);
    }

    @Override
    protected void removeTwoOperands(MethodVisitor mv) {
        mv.visitInsn(88);
        mv.visitInsn(88);
    }

    @Override
    protected MethodCaller getArrayGetCaller() {
        return longArrayGet;
    }

    @Override
    protected MethodCaller getArraySetCaller() {
        return longArraySet;
    }

    @Override
    protected int getBitwiseOperationBytecode(int type) {
        return bitOp[type];
    }

    @Override
    protected int getCompareCode() {
        return 148;
    }

    @Override
    protected ClassNode getNormalOpResultType() {
        return ClassHelper.long_TYPE;
    }

    @Override
    protected int getShiftOperationBytecode(int type) {
        return shiftOp[type];
    }

    @Override
    protected int getStandardOperationBytecode(int type) {
        return stdOperations[type];
    }

    @Override
    protected void writeMinusMinus(MethodVisitor mv) {
        mv.visitInsn(10);
        mv.visitInsn(101);
    }

    @Override
    protected void writePlusPlus(MethodVisitor mv) {
        mv.visitInsn(10);
        mv.visitInsn(97);
    }

    @Override
    protected ClassNode getDevisionOpResultType() {
        return ClassHelper.long_TYPE;
    }

    @Override
    protected boolean supportsDivision() {
        return true;
    }
}

