/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.asm.BinaryIntExpressionHelper;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.BytecodeInterface8;

public class BinaryBooleanExpressionHelper
extends BinaryIntExpressionHelper {
    private static final MethodCaller boolArrayGet = MethodCaller.newStatic(BytecodeInterface8.class, "zArrayGet");
    private static final MethodCaller boolArraySet = MethodCaller.newStatic(BytecodeInterface8.class, "zArraySet");

    public BinaryBooleanExpressionHelper(WriterController wc) {
        super(wc);
    }

    @Override
    protected MethodCaller getArrayGetCaller() {
        return boolArrayGet;
    }

    @Override
    protected MethodCaller getArraySetCaller() {
        return boolArraySet;
    }

    @Override
    protected ClassNode getArrayGetResultType() {
        return ClassHelper.boolean_TYPE;
    }

    @Override
    public boolean writePostOrPrefixMethod(int operation, boolean simulate) {
        if (simulate) {
            return false;
        }
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected boolean writeStdOperators(int type, boolean simulate) {
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
    protected ClassNode getNormalOpResultType() {
        return ClassHelper.boolean_TYPE;
    }

    @Override
    protected ClassNode getDevisionOpResultType() {
        return ClassHelper.boolean_TYPE;
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
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected void writePlusPlus(MethodVisitor mv) {
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected void writeMinusMinus(MethodVisitor mv) {
        throw new GroovyBugError("should not reach here");
    }

    @Override
    protected void doubleTwoOperands(MethodVisitor mv) {
        throw new GroovyBugError("should not reach here");
    }
}

