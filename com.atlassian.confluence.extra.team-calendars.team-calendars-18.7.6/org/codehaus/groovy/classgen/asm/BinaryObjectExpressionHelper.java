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

public class BinaryObjectExpressionHelper
extends BinaryExpressionWriter {
    private static final MethodCaller arrayGet = MethodCaller.newStatic(BytecodeInterface8.class, "objectArrayGet");
    private static final MethodCaller arraySet = MethodCaller.newStatic(BytecodeInterface8.class, "objectArraySet");

    public BinaryObjectExpressionHelper(WriterController controller) {
        super(controller);
    }

    @Override
    protected MethodCaller getArrayGetCaller() {
        return arrayGet;
    }

    @Override
    protected MethodCaller getArraySetCaller() {
        return arraySet;
    }

    @Override
    public boolean writePostOrPrefixMethod(int operation, boolean simulate) {
        if (simulate) {
            return false;
        }
        throw new GroovyBugError("should not reach here");
    }

    @Override
    public boolean write(int operation, boolean simulate) {
        if (simulate) {
            return false;
        }
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected boolean writeDivision(boolean simulate) {
        if (simulate) {
            return false;
        }
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected int getBitwiseOperationBytecode(int type) {
        return -1;
    }

    @Override
    protected int getCompareCode() {
        return -1;
    }

    @Override
    protected ClassNode getNormalOpResultType() {
        return null;
    }

    @Override
    protected ClassNode getDevisionOpResultType() {
        return null;
    }

    @Override
    protected int getShiftOperationBytecode(int type) {
        return -1;
    }

    @Override
    protected int getStandardOperationBytecode(int type) {
        return -1;
    }

    @Override
    protected void removeTwoOperands(MethodVisitor mv) {
    }

    @Override
    protected void writePlusPlus(MethodVisitor mv) {
    }

    @Override
    protected void writeMinusMinus(MethodVisitor mv) {
    }

    @Override
    protected void doubleTwoOperands(MethodVisitor mv) {
    }

    @Override
    protected ClassNode getArrayGetResultType() {
        return ClassHelper.OBJECT_TYPE;
    }
}

