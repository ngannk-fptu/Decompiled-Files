/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesTypeChooser;
import org.codehaus.groovy.transform.sc.transformers.CompareToNullExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;

public class BooleanExpressionTransformer {
    private final StaticCompilationTransformer transformer;

    public BooleanExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.transformer = staticCompilationTransformer;
    }

    Expression transformBooleanExpression(BooleanExpression booleanExpression) {
        if (booleanExpression instanceof NotExpression) {
            return this.transformer.superTransform(booleanExpression);
        }
        Expression expression = booleanExpression.getExpression();
        if (!(expression instanceof BinaryExpression)) {
            StaticTypesTypeChooser typeChooser = this.transformer.getTypeChooser();
            ClassNode type = typeChooser.resolveType(expression, this.transformer.getClassNode());
            OptimizingBooleanExpression transformed = new OptimizingBooleanExpression(this.transformer.transform(expression), type);
            transformed.setSourcePosition(booleanExpression);
            transformed.copyNodeMetaData(booleanExpression);
            return transformed;
        }
        return this.transformer.superTransform(booleanExpression);
    }

    private static boolean isExtended(ClassNode owner, Iterator<InnerClassNode> classes) {
        while (classes.hasNext()) {
            InnerClassNode next = classes.next();
            if (next == owner || !next.isDerivedFrom(owner)) continue;
            return true;
        }
        if (owner.getInnerClasses().hasNext()) {
            return BooleanExpressionTransformer.isExtended(owner, owner.getInnerClasses());
        }
        return false;
    }

    private static class OptimizingBooleanExpression
    extends BooleanExpression {
        private final Expression expression;
        private final ClassNode type;

        public OptimizingBooleanExpression(Expression expression, ClassNode type) {
            super(expression);
            this.expression = expression;
            this.type = type.redirect();
        }

        @Override
        public Expression transformExpression(ExpressionTransformer transformer) {
            OptimizingBooleanExpression ret = new OptimizingBooleanExpression(transformer.transform(this.expression), this.type);
            ret.setSourcePosition(this);
            ret.copyNodeMetaData(this);
            return ret;
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            if (visitor instanceof AsmClassGenerator) {
                MethodNode dgmNode;
                ClassNode owner;
                MethodNode node;
                List<MethodNode> asBoolean;
                AsmClassGenerator acg = (AsmClassGenerator)visitor;
                WriterController controller = acg.getController();
                OperandStack os = controller.getOperandStack();
                if (this.type.equals(ClassHelper.boolean_TYPE)) {
                    this.expression.visit(visitor);
                    os.doGroovyCast(ClassHelper.boolean_TYPE);
                    return;
                }
                if (this.type.equals(ClassHelper.Boolean_TYPE)) {
                    MethodVisitor mv = controller.getMethodVisitor();
                    this.expression.visit(visitor);
                    Label unbox = new Label();
                    Label exit = new Label();
                    mv.visitInsn(89);
                    mv.visitJumpInsn(199, unbox);
                    mv.visitInsn(87);
                    mv.visitInsn(3);
                    mv.visitJumpInsn(167, exit);
                    mv.visitLabel(unbox);
                    if (!os.getTopOperand().equals(this.type)) {
                        BytecodeHelper.doCast(mv, this.type);
                    }
                    mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
                    mv.visitLabel(exit);
                    os.replace(ClassHelper.boolean_TYPE);
                    return;
                }
                ClassNode top = this.type;
                if (ClassHelper.isPrimitiveType(top)) {
                    this.expression.visit(visitor);
                    top = controller.getOperandStack().getTopOperand();
                    if (ClassHelper.isPrimitiveType(top)) {
                        if (!(top.equals(ClassHelper.int_TYPE) || top.equals(ClassHelper.byte_TYPE) || top.equals(ClassHelper.short_TYPE) || top.equals(ClassHelper.char_TYPE))) {
                            if (top.equals(ClassHelper.long_TYPE)) {
                                MethodVisitor mv = controller.getMethodVisitor();
                                mv.visitInsn(9);
                                mv.visitInsn(148);
                                controller.getOperandStack().replace(ClassHelper.boolean_TYPE);
                            } else if (top.equals(ClassHelper.float_TYPE)) {
                                MethodVisitor mv = controller.getMethodVisitor();
                                mv.visitInsn(141);
                                mv.visitInsn(14);
                                mv.visitInsn(152);
                                controller.getOperandStack().replace(ClassHelper.boolean_TYPE);
                            } else if (top.equals(ClassHelper.double_TYPE)) {
                                MethodVisitor mv = controller.getMethodVisitor();
                                mv.visitInsn(14);
                                mv.visitInsn(152);
                                controller.getOperandStack().replace(ClassHelper.boolean_TYPE);
                            }
                        }
                        return;
                    }
                }
                if ((asBoolean = StaticTypeCheckingSupport.findDGMMethodsByNameAndArguments(controller.getSourceUnit().getClassLoader(), top, "asBoolean", ClassNode.EMPTY_ARRAY)).size() == 1 && (node = asBoolean.get(0)) instanceof ExtensionMethodNode && ClassHelper.OBJECT_TYPE.equals(owner = (dgmNode = ((ExtensionMethodNode)node).getExtensionMethodNode()).getParameters()[0].getType()) && (Modifier.isFinal(top.getModifiers()) || top instanceof InnerClassNode && Modifier.isPrivate(top.getModifiers()) && !BooleanExpressionTransformer.isExtended(top, top.getOuterClass().getInnerClasses()))) {
                    CompareToNullExpression expr = new CompareToNullExpression(this.expression, false);
                    expr.visit(acg);
                    return;
                }
                super.visit(visitor);
            } else {
                super.visit(visitor);
            }
        }
    }
}

