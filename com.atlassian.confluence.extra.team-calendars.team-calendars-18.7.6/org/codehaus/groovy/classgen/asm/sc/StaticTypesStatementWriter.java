/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.Enumeration;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.StatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterController;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;

public class StaticTypesStatementWriter
extends StatementWriter {
    private static final ClassNode ITERABLE_CLASSNODE = ClassHelper.make(Iterable.class);
    private static final ClassNode ENUMERATION_CLASSNODE = ClassHelper.make(Enumeration.class);
    private static final MethodCaller ENUMERATION_NEXT_METHOD = MethodCaller.newInterface(Enumeration.class, "nextElement");
    private static final MethodCaller ENUMERATION_HASMORE_METHOD = MethodCaller.newInterface(Enumeration.class, "hasMoreElements");
    private StaticTypesWriterController controller;

    public StaticTypesStatementWriter(StaticTypesWriterController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public void writeBlockStatement(BlockStatement statement) {
        this.controller.switchToFastPath();
        super.writeBlockStatement(statement);
        this.controller.switchToSlowPath();
    }

    @Override
    protected void writeForInLoop(ForStatement loop) {
        this.controller.getAcg().onLineNumber(loop, "visitForLoop");
        this.writeStatementLabel(loop);
        CompileStack compileStack = this.controller.getCompileStack();
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        compileStack.pushLoop(loop.getVariableScope(), loop.getStatementLabels());
        TypeChooser typeChooser = this.controller.getTypeChooser();
        Expression collectionExpression = loop.getCollectionExpression();
        ClassNode collectionType = typeChooser.resolveType(collectionExpression, this.controller.getClassNode());
        Parameter loopVariable = loop.getVariable();
        int size = operandStack.getStackLength();
        if (collectionType.isArray() && loopVariable.getOriginType().equals(collectionType.getComponentType())) {
            this.writeOptimizedForEachLoop(compileStack, operandStack, mv, loop, collectionExpression, collectionType, loopVariable);
        } else if (ENUMERATION_CLASSNODE.equals(collectionType)) {
            this.writeEnumerationBasedForEachLoop(compileStack, operandStack, mv, loop, collectionExpression, collectionType, loopVariable);
        } else {
            this.writeIteratorBasedForEachLoop(compileStack, operandStack, mv, loop, collectionExpression, collectionType, loopVariable);
        }
        operandStack.popDownTo(size);
        compileStack.pop();
    }

    private void writeOptimizedForEachLoop(CompileStack compileStack, OperandStack operandStack, MethodVisitor mv, ForStatement loop, Expression collectionExpression, ClassNode collectionType, Parameter loopVariable) {
        BytecodeVariable variable = compileStack.defineVariable(loopVariable, false);
        Label continueLabel = compileStack.getContinueLabel();
        Label breakLabel = compileStack.getBreakLabel();
        AsmClassGenerator acg = this.controller.getAcg();
        collectionExpression.visit(acg);
        mv.visitInsn(89);
        int array = compileStack.defineTemporaryVariable("$arr", collectionType, true);
        mv.visitJumpInsn(198, breakLabel);
        mv.visitVarInsn(25, array);
        mv.visitInsn(190);
        operandStack.push(ClassHelper.int_TYPE);
        int arrayLen = compileStack.defineTemporaryVariable("$len", ClassHelper.int_TYPE, true);
        mv.visitInsn(3);
        operandStack.push(ClassHelper.int_TYPE);
        int loopIdx = compileStack.defineTemporaryVariable("$idx", ClassHelper.int_TYPE, true);
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(21, loopIdx);
        mv.visitVarInsn(21, arrayLen);
        mv.visitJumpInsn(162, breakLabel);
        this.loadFromArray(mv, variable, array, loopIdx);
        mv.visitIincInsn(loopIdx, 1);
        loop.getLoopBlock().visit(acg);
        mv.visitJumpInsn(167, continueLabel);
        mv.visitLabel(breakLabel);
        compileStack.removeVar(loopIdx);
        compileStack.removeVar(arrayLen);
        compileStack.removeVar(array);
    }

    private void loadFromArray(MethodVisitor mv, BytecodeVariable variable, int array, int iteratorIdx) {
        OperandStack os = this.controller.getOperandStack();
        mv.visitVarInsn(25, array);
        mv.visitVarInsn(21, iteratorIdx);
        ClassNode varType = variable.getType();
        boolean primitiveType = ClassHelper.isPrimitiveType(varType);
        boolean isByte = ClassHelper.byte_TYPE.equals(varType);
        boolean isShort = ClassHelper.short_TYPE.equals(varType);
        boolean isInt = ClassHelper.int_TYPE.equals(varType);
        boolean isLong = ClassHelper.long_TYPE.equals(varType);
        boolean isFloat = ClassHelper.float_TYPE.equals(varType);
        boolean isDouble = ClassHelper.double_TYPE.equals(varType);
        boolean isChar = ClassHelper.char_TYPE.equals(varType);
        boolean isBoolean = ClassHelper.boolean_TYPE.equals(varType);
        if (primitiveType) {
            if (isByte) {
                mv.visitInsn(51);
            }
            if (isShort) {
                mv.visitInsn(53);
            }
            if (isInt || isChar || isBoolean) {
                mv.visitInsn(isChar ? 52 : (isBoolean ? 51 : 46));
            }
            if (isLong) {
                mv.visitInsn(47);
            }
            if (isFloat) {
                mv.visitInsn(48);
            }
            if (isDouble) {
                mv.visitInsn(49);
            }
        } else {
            mv.visitInsn(50);
        }
        os.push(varType);
        os.storeVar(variable);
    }

    private void writeIteratorBasedForEachLoop(CompileStack compileStack, OperandStack operandStack, MethodVisitor mv, ForStatement loop, Expression collectionExpression, ClassNode collectionType, Parameter loopVariable) {
        BytecodeVariable variable = compileStack.defineVariable(loopVariable, false);
        if (StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(collectionType, ITERABLE_CLASSNODE)) {
            MethodCallExpression iterator = new MethodCallExpression(collectionExpression, "iterator", (Expression)new ArgumentListExpression());
            iterator.setMethodTarget(collectionType.getMethod("iterator", Parameter.EMPTY_ARRAY));
            iterator.setImplicitThis(false);
            iterator.visit(this.controller.getAcg());
        } else {
            collectionExpression.visit(this.controller.getAcg());
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/DefaultGroovyMethods", "iterator", "(Ljava/lang/Object;)Ljava/util/Iterator;", false);
            operandStack.replace(ClassHelper.Iterator_TYPE);
        }
        int iteratorIdx = compileStack.defineTemporaryVariable("iterator", ClassHelper.Iterator_TYPE, true);
        Label continueLabel = compileStack.getContinueLabel();
        Label breakLabel = compileStack.getBreakLabel();
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(25, iteratorIdx);
        this.writeIteratorHasNext(mv);
        mv.visitJumpInsn(153, breakLabel);
        mv.visitVarInsn(25, iteratorIdx);
        this.writeIteratorNext(mv);
        operandStack.push(ClassHelper.OBJECT_TYPE);
        operandStack.storeVar(variable);
        loop.getLoopBlock().visit(this.controller.getAcg());
        mv.visitJumpInsn(167, continueLabel);
        mv.visitLabel(breakLabel);
        compileStack.removeVar(iteratorIdx);
    }

    private void writeEnumerationBasedForEachLoop(CompileStack compileStack, OperandStack operandStack, MethodVisitor mv, ForStatement loop, Expression collectionExpression, ClassNode collectionType, Parameter loopVariable) {
        BytecodeVariable variable = compileStack.defineVariable(loopVariable, false);
        collectionExpression.visit(this.controller.getAcg());
        int enumIdx = compileStack.defineTemporaryVariable("$enum", ENUMERATION_CLASSNODE, true);
        Label continueLabel = compileStack.getContinueLabel();
        Label breakLabel = compileStack.getBreakLabel();
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(25, enumIdx);
        ENUMERATION_HASMORE_METHOD.call(mv);
        mv.visitJumpInsn(153, breakLabel);
        mv.visitVarInsn(25, enumIdx);
        ENUMERATION_NEXT_METHOD.call(mv);
        operandStack.push(ClassHelper.OBJECT_TYPE);
        operandStack.storeVar(variable);
        loop.getLoopBlock().visit(this.controller.getAcg());
        mv.visitJumpInsn(167, continueLabel);
        mv.visitLabel(breakLabel);
    }
}

