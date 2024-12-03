/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;

public class UnaryExpressionHelper {
    static final MethodCaller unaryPlus = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "unaryPlus");
    static final MethodCaller unaryMinus = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "unaryMinus");
    static final MethodCaller bitwiseNegate = MethodCaller.newStatic(ScriptBytecodeAdapter.class, "bitwiseNegate");
    private final WriterController controller;

    public UnaryExpressionHelper(WriterController controller) {
        this.controller = controller;
    }

    public void writeUnaryPlus(UnaryPlusExpression expression) {
        Expression subExpression = expression.getExpression();
        subExpression.visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        unaryPlus.call(this.controller.getMethodVisitor());
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
        this.controller.getAssertionWriter().record(expression);
    }

    public void writeUnaryMinus(UnaryMinusExpression expression) {
        Expression subExpression = expression.getExpression();
        subExpression.visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        unaryMinus.call(this.controller.getMethodVisitor());
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
        this.controller.getAssertionWriter().record(expression);
    }

    public void writeBitwiseNegate(BitwiseNegationExpression expression) {
        Expression subExpression = expression.getExpression();
        subExpression.visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        bitwiseNegate.call(this.controller.getMethodVisitor());
        this.controller.getOperandStack().replace(ClassHelper.OBJECT_TYPE);
        this.controller.getAssertionWriter().record(expression);
    }

    public void writeNotExpression(NotExpression expression) {
        Expression subExpression = expression.getExpression();
        int mark = this.controller.getOperandStack().getStackLength();
        subExpression.visit(this.controller.getAcg());
        this.controller.getOperandStack().castToBool(mark, true);
        BytecodeHelper.negateBoolean(this.controller.getMethodVisitor());
        this.controller.getAssertionWriter().record(expression);
    }
}

