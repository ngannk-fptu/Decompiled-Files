/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class ConstructorCallTransformer {
    private final StaticCompilationTransformer staticCompilationTransformer;

    public ConstructorCallTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.staticCompilationTransformer = staticCompilationTransformer;
    }

    Expression transformConstructorCall(ConstructorCallExpression expr) {
        Expression expression;
        TupleExpression tupleExpression;
        List<Expression> expressions;
        Expression arguments;
        ConstructorNode node = (ConstructorNode)expr.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (node == null) {
            return expr;
        }
        Parameter[] params = node.getParameters();
        if ((params.length == 1 || params.length == 2) && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(params[params.length - 1].getType(), ClassHelper.MAP_TYPE) && node.getCode() == StaticTypeCheckingVisitor.GENERATED_EMPTY_STATEMENT && (arguments = expr.getArguments()) instanceof TupleExpression && ((expressions = (tupleExpression = (TupleExpression)arguments).getExpressions()).size() == 1 || expressions.size() == 2) && (expression = expressions.get(expressions.size() - 1)) instanceof MapExpression) {
            MapExpression map = (MapExpression)expression;
            ClassNode declaringClass = node.getDeclaringClass();
            for (ConstructorNode constructorNode : declaringClass.getDeclaredConstructors()) {
                if (constructorNode != node) continue;
                return this.staticCompilationTransformer.superTransform(expr);
            }
            MapStyleConstructorCall result = new MapStyleConstructorCall(this.staticCompilationTransformer, declaringClass, map, expr);
            return result;
        }
        return this.staticCompilationTransformer.superTransform(expr);
    }

    private static class MapStyleConstructorCall
    extends BytecodeExpression
    implements Opcodes {
        private StaticCompilationTransformer staticCompilationTransformer;
        private AsmClassGenerator acg;
        private final ClassNode declaringClass;
        private final MapExpression map;
        private final ConstructorCallExpression originalCall;
        private final boolean innerClassCall;

        public MapStyleConstructorCall(StaticCompilationTransformer transformer, ClassNode declaringClass, MapExpression map, ConstructorCallExpression originalCall) {
            this.staticCompilationTransformer = transformer;
            this.declaringClass = declaringClass;
            this.map = map;
            this.originalCall = originalCall;
            this.setSourcePosition(originalCall);
            this.copyNodeMetaData(originalCall);
            List<Expression> originalExpressions = originalCall.getArguments() instanceof TupleExpression ? ((TupleExpression)originalCall.getArguments()).getExpressions() : null;
            this.innerClassCall = originalExpressions != null && originalExpressions.size() == 2;
        }

        @Override
        public void visit(GroovyCodeVisitor visitor) {
            if (visitor instanceof AsmClassGenerator) {
                this.acg = (AsmClassGenerator)visitor;
            } else {
                this.originalCall.visit(visitor);
            }
            super.visit(visitor);
        }

        @Override
        public ClassNode getType() {
            return this.declaringClass;
        }

        @Override
        public void visit(MethodVisitor mv) {
            WriterController controller = this.acg.getController();
            OperandStack operandStack = controller.getOperandStack();
            CompileStack compileStack = controller.getCompileStack();
            final int tmpObj = compileStack.defineTemporaryVariable("tmpObj", this.declaringClass, false);
            String classInternalName = BytecodeHelper.getClassInternalName(this.declaringClass);
            mv.visitTypeInsn(187, classInternalName);
            mv.visitInsn(89);
            String desc = "()V";
            if (this.innerClassCall && this.declaringClass.isRedirectNode() && this.declaringClass.redirect() instanceof InnerClassNode) {
                mv.visitVarInsn(25, 0);
                InnerClassNode icn = (InnerClassNode)this.declaringClass.redirect();
                Parameter[] params = new Parameter[]{new Parameter(icn.getOuterClass(), "$p$")};
                desc = BytecodeHelper.getMethodDescriptor(ClassHelper.VOID_TYPE, params);
            }
            mv.visitMethodInsn(183, classInternalName, "<init>", desc, false);
            mv.visitVarInsn(58, tmpObj);
            for (MapEntryExpression entryExpression : this.map.getMapEntryExpressions()) {
                int line = entryExpression.getLineNumber();
                int col = entryExpression.getColumnNumber();
                Expression keyExpression = this.staticCompilationTransformer.transform(entryExpression.getKeyExpression());
                Expression valueExpression = this.staticCompilationTransformer.transform(entryExpression.getValueExpression());
                BinaryExpression bexp = new BinaryExpression(new PropertyExpression((Expression)new BytecodeExpression(){

                    @Override
                    public void visit(MethodVisitor mv) {
                        mv.visitVarInsn(25, tmpObj);
                    }

                    @Override
                    public ClassNode getType() {
                        return MapStyleConstructorCall.this.declaringClass;
                    }
                }, keyExpression), Token.newSymbol("=", line, col), valueExpression);
                bexp.setSourcePosition(entryExpression);
                bexp.visit(this.acg);
                operandStack.pop();
            }
            mv.visitVarInsn(25, tmpObj);
            compileStack.removeVar(tmpObj);
        }
    }
}

