/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.syntax.Token;

public class CompareIdentityExpression
extends BinaryExpression
implements Opcodes {
    private final Expression leftExpression;
    private final Expression rightExpression;

    public CompareIdentityExpression(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, new Token(128, "==", -1, -1), rightExpression);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
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
            controller.getTypeChooser().resolveType(this.leftExpression, controller.getClassNode());
            controller.getTypeChooser().resolveType(this.rightExpression, controller.getClassNode());
            MethodVisitor mv = controller.getMethodVisitor();
            this.leftExpression.visit(acg);
            controller.getOperandStack().box();
            this.rightExpression.visit(acg);
            controller.getOperandStack().box();
            Label l1 = new Label();
            mv.visitJumpInsn(166, l1);
            mv.visitInsn(4);
            Label l2 = new Label();
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l1);
            mv.visitInsn(3);
            mv.visitLabel(l2);
            controller.getOperandStack().replace(ClassHelper.boolean_TYPE, 2);
        } else {
            super.visit(visitor);
        }
    }
}

