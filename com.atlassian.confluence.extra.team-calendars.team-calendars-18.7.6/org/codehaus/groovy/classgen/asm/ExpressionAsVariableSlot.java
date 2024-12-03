/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public class ExpressionAsVariableSlot
extends BytecodeExpression {
    private int index = -1;
    private Expression exp;
    private WriterController controller;
    private String name;

    public ExpressionAsVariableSlot(WriterController controller, Expression expression, String name) {
        this.exp = expression;
        this.controller = controller;
        this.name = name;
    }

    public ExpressionAsVariableSlot(WriterController controller, Expression expression) {
        this(controller, expression, "ExpressionAsVariableSlot_TEMP");
    }

    @Override
    public void visit(MethodVisitor mv) {
        OperandStack os = this.controller.getOperandStack();
        if (this.index == -1) {
            this.exp.visit(this.controller.getAcg());
            os.dup();
            this.setType(os.getTopOperand());
            CompileStack compileStack = this.controller.getCompileStack();
            this.index = compileStack.defineTemporaryVariable(this.name, this.getType(), true);
        } else {
            os.load(this.getType(), this.index);
        }
        os.remove(1);
    }

    public int getIndex() {
        if (this.index == -1) {
            throw new GroovyBugError("index requested before visit!");
        }
        return this.index;
    }

    @Override
    public String getText() {
        return this.exp.getText();
    }
}

