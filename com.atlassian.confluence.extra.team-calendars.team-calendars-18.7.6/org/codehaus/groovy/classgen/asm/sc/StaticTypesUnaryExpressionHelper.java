/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.UnaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.WriterController;

public class StaticTypesUnaryExpressionHelper
extends UnaryExpressionHelper
implements Opcodes {
    private static final UnaryMinusExpression EMPTY_UNARY_MINUS = new UnaryMinusExpression(EmptyExpression.INSTANCE);
    private static final UnaryPlusExpression EMPTY_UNARY_PLUS = new UnaryPlusExpression(EmptyExpression.INSTANCE);
    private static final BitwiseNegationExpression EMPTY_BITWISE_NEGATE = new BitwiseNegationExpression(EmptyExpression.INSTANCE);
    private final WriterController controller;

    public StaticTypesUnaryExpressionHelper(WriterController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void writeBitwiseNegate(BitwiseNegationExpression expression) {
        ClassNode top;
        expression.getExpression().visit(this.controller.getAcg());
        if (this.isPrimitiveOnTop() && ((top = this.getTopOperand()) == ClassHelper.int_TYPE || top == ClassHelper.short_TYPE || top == ClassHelper.byte_TYPE || top == ClassHelper.char_TYPE || top == ClassHelper.long_TYPE)) {
            BytecodeExpression bytecodeExpression = new BytecodeExpression(){

                @Override
                public void visit(MethodVisitor mv) {
                    if (ClassHelper.long_TYPE == top) {
                        mv.visitLdcInsn(-1);
                        mv.visitInsn(131);
                    } else {
                        mv.visitInsn(2);
                        mv.visitInsn(130);
                        if (ClassHelper.byte_TYPE == top) {
                            mv.visitInsn(145);
                        } else if (ClassHelper.char_TYPE == top) {
                            mv.visitInsn(146);
                        } else if (ClassHelper.short_TYPE == top) {
                            mv.visitInsn(147);
                        }
                    }
                }
            };
            bytecodeExpression.visit(this.controller.getAcg());
            this.controller.getOperandStack().remove(1);
            return;
        }
        super.writeBitwiseNegate(EMPTY_BITWISE_NEGATE);
    }

    @Override
    public void writeNotExpression(NotExpression expression) {
        ClassNode classNode;
        Expression subExpression;
        TypeChooser typeChooser = this.controller.getTypeChooser();
        if (typeChooser.resolveType(subExpression = expression.getExpression(), classNode = this.controller.getClassNode()) == ClassHelper.boolean_TYPE) {
            subExpression.visit(this.controller.getAcg());
            this.controller.getOperandStack().doGroovyCast(ClassHelper.boolean_TYPE);
            BytecodeExpression bytecodeExpression = new BytecodeExpression(){

                @Override
                public void visit(MethodVisitor mv) {
                    Label ne = new Label();
                    mv.visitJumpInsn(154, ne);
                    mv.visitInsn(4);
                    Label out = new Label();
                    mv.visitJumpInsn(167, out);
                    mv.visitLabel(ne);
                    mv.visitInsn(3);
                    mv.visitLabel(out);
                }
            };
            bytecodeExpression.visit(this.controller.getAcg());
            this.controller.getOperandStack().remove(1);
            return;
        }
        super.writeNotExpression(expression);
    }

    @Override
    public void writeUnaryMinus(UnaryMinusExpression expression) {
        ClassNode top;
        expression.getExpression().visit(this.controller.getAcg());
        if (this.isPrimitiveOnTop() && (top = this.getTopOperand()) != ClassHelper.boolean_TYPE) {
            BytecodeExpression bytecodeExpression = new BytecodeExpression(){

                @Override
                public void visit(MethodVisitor mv) {
                    if (ClassHelper.int_TYPE == top || ClassHelper.short_TYPE == top || ClassHelper.byte_TYPE == top || ClassHelper.char_TYPE == top) {
                        mv.visitInsn(116);
                        if (ClassHelper.byte_TYPE == top) {
                            mv.visitInsn(145);
                        } else if (ClassHelper.char_TYPE == top) {
                            mv.visitInsn(146);
                        } else if (ClassHelper.short_TYPE == top) {
                            mv.visitInsn(147);
                        }
                    } else if (ClassHelper.long_TYPE == top) {
                        mv.visitInsn(117);
                    } else if (ClassHelper.float_TYPE == top) {
                        mv.visitInsn(118);
                    } else if (ClassHelper.double_TYPE == top) {
                        mv.visitInsn(119);
                    }
                }
            };
            bytecodeExpression.visit(this.controller.getAcg());
            this.controller.getOperandStack().remove(1);
            return;
        }
        super.writeUnaryMinus(EMPTY_UNARY_MINUS);
    }

    @Override
    public void writeUnaryPlus(UnaryPlusExpression expression) {
        expression.getExpression().visit(this.controller.getAcg());
        if (this.isPrimitiveOnTop()) {
            return;
        }
        super.writeUnaryPlus(EMPTY_UNARY_PLUS);
    }

    private boolean isPrimitiveOnTop() {
        return ClassHelper.isPrimitiveType(this.getTopOperand());
    }

    private ClassNode getTopOperand() {
        return this.controller.getOperandStack().getTopOperand();
    }
}

