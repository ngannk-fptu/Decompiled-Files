/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.syntax.Token;

public class CompareToNullExpression
extends BinaryExpression
implements Opcodes {
    private final boolean equalsNull;
    private final Expression objectExpression;

    public CompareToNullExpression(Expression objectExpression, boolean compareToNull) {
        super(objectExpression, new Token(128, compareToNull ? "==" : "!=", -1, -1), ConstantExpression.NULL);
        this.objectExpression = objectExpression;
        this.equalsNull = compareToNull;
    }

    public Expression getObjectExpression() {
        return this.objectExpression;
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        if (visitor instanceof AsmClassGenerator) {
            AsmClassGenerator acg = (AsmClassGenerator)visitor;
            WriterController controller = acg.getController();
            MethodVisitor mv = controller.getMethodVisitor();
            this.objectExpression.visit(acg);
            ClassNode top = controller.getOperandStack().getTopOperand();
            if (ClassHelper.isPrimitiveType(top)) {
                controller.getOperandStack().pop();
                mv.visitInsn(this.equalsNull ? 3 : 4);
                controller.getOperandStack().push(ClassHelper.boolean_TYPE);
                return;
            }
            Label zero = new Label();
            mv.visitJumpInsn(this.equalsNull ? 199 : 198, zero);
            mv.visitInsn(4);
            Label end = new Label();
            mv.visitJumpInsn(167, end);
            mv.visitLabel(zero);
            mv.visitInsn(3);
            mv.visitLabel(end);
            controller.getOperandStack().replace(ClassHelper.boolean_TYPE);
        } else {
            super.visit(visitor);
        }
    }
}

