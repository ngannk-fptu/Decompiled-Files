/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.OperandStack;

public class VariableSlotLoader
extends BytecodeExpression {
    private int idx;
    private OperandStack operandStack;

    public VariableSlotLoader(ClassNode type, int index, OperandStack os) {
        super(type);
        this.idx = index;
        this.operandStack = os;
    }

    public VariableSlotLoader(int index, OperandStack os) {
        this.idx = index;
        this.operandStack = os;
    }

    @Override
    public void visit(MethodVisitor mv) {
        this.operandStack.load(this.getType(), this.idx);
        this.operandStack.remove(1);
    }

    public int getIndex() {
        return this.idx;
    }
}

