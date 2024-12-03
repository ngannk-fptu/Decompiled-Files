/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.MethodCaller;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public class StatementWriter {
    private static final MethodCaller iteratorNextMethod = MethodCaller.newInterface(Iterator.class, "next");
    private static final MethodCaller iteratorHasNextMethod = MethodCaller.newInterface(Iterator.class, "hasNext");
    private WriterController controller;

    public StatementWriter(WriterController controller) {
        this.controller = controller;
    }

    protected void writeStatementLabel(Statement statement) {
        String name = statement.getStatementLabel();
        if (name != null) {
            Label label = this.controller.getCompileStack().createLocalLabel(name);
            this.controller.getMethodVisitor().visitLabel(label);
        }
    }

    public void writeBlockStatement(BlockStatement block) {
        CompileStack compileStack = this.controller.getCompileStack();
        this.writeStatementLabel(block);
        int mark = this.controller.getOperandStack().getStackLength();
        compileStack.pushVariableScope(block.getVariableScope());
        for (Statement statement : block.getStatements()) {
            statement.visit(this.controller.getAcg());
        }
        compileStack.pop();
        this.controller.getOperandStack().popDownTo(mark);
    }

    public void writeForStatement(ForStatement loop) {
        Parameter loopVar = loop.getVariable();
        if (loopVar == ForStatement.FOR_LOOP_DUMMY) {
            this.writeForLoopWithClosureList(loop);
        } else {
            this.writeForInLoop(loop);
        }
    }

    protected void writeIteratorHasNext(MethodVisitor mv) {
        iteratorHasNextMethod.call(mv);
    }

    protected void writeIteratorNext(MethodVisitor mv) {
        iteratorNextMethod.call(mv);
    }

    protected void writeForInLoop(ForStatement loop) {
        this.controller.getAcg().onLineNumber(loop, "visitForLoop");
        this.writeStatementLabel(loop);
        CompileStack compileStack = this.controller.getCompileStack();
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        compileStack.pushLoop(loop.getVariableScope(), loop.getStatementLabels());
        BytecodeVariable variable = compileStack.defineVariable(loop.getVariable(), false);
        MethodCallExpression iterator = new MethodCallExpression(loop.getCollectionExpression(), "iterator", (Expression)new ArgumentListExpression());
        iterator.visit(this.controller.getAcg());
        operandStack.doGroovyCast(ClassHelper.Iterator_TYPE);
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
        compileStack.pop();
    }

    protected void writeForLoopWithClosureList(ForStatement loop) {
        this.controller.getAcg().onLineNumber(loop, "visitForLoop");
        this.writeStatementLabel(loop);
        MethodVisitor mv = this.controller.getMethodVisitor();
        this.controller.getCompileStack().pushLoop(loop.getVariableScope(), loop.getStatementLabels());
        ClosureListExpression clExpr = (ClosureListExpression)loop.getCollectionExpression();
        this.controller.getCompileStack().pushVariableScope(clExpr.getVariableScope());
        List<Expression> expressions = clExpr.getExpressions();
        int size = expressions.size();
        int condIndex = (size - 1) / 2;
        for (int i = 0; i < condIndex; ++i) {
            this.visitExpressionOrStatement(expressions.get(i));
        }
        Label continueLabel = this.controller.getCompileStack().getContinueLabel();
        Label breakLabel = this.controller.getCompileStack().getBreakLabel();
        Label cond = new Label();
        mv.visitLabel(cond);
        Expression condExpr = expressions.get(condIndex);
        int mark = this.controller.getOperandStack().getStackLength();
        condExpr.visit(this.controller.getAcg());
        this.controller.getOperandStack().castToBool(mark, true);
        this.controller.getOperandStack().jump(153, breakLabel);
        loop.getLoopBlock().visit(this.controller.getAcg());
        mv.visitLabel(continueLabel);
        for (int i = condIndex + 1; i < size; ++i) {
            this.visitExpressionOrStatement(expressions.get(i));
        }
        mv.visitJumpInsn(167, cond);
        mv.visitLabel(breakLabel);
        this.controller.getCompileStack().pop();
        this.controller.getCompileStack().pop();
    }

    private void visitExpressionOrStatement(Object o) {
        if (o == EmptyExpression.INSTANCE) {
            return;
        }
        if (o instanceof Expression) {
            Expression expr = (Expression)o;
            int mark = this.controller.getOperandStack().getStackLength();
            expr.visit(this.controller.getAcg());
            this.controller.getOperandStack().popDownTo(mark);
        } else {
            ((Statement)o).visit(this.controller.getAcg());
        }
    }

    public void writeWhileLoop(WhileStatement loop) {
        this.controller.getAcg().onLineNumber(loop, "visitWhileLoop");
        this.writeStatementLabel(loop);
        MethodVisitor mv = this.controller.getMethodVisitor();
        this.controller.getCompileStack().pushLoop(loop.getStatementLabels());
        Label continueLabel = this.controller.getCompileStack().getContinueLabel();
        Label breakLabel = this.controller.getCompileStack().getBreakLabel();
        mv.visitLabel(continueLabel);
        BooleanExpression bool = loop.getBooleanExpression();
        boolean boolHandled = false;
        if (bool.getExpression() instanceof ConstantExpression) {
            ConstantExpression constant = (ConstantExpression)bool.getExpression();
            if (constant.getValue() == Boolean.TRUE) {
                boolHandled = true;
            } else if (constant.getValue() == Boolean.FALSE) {
                boolHandled = true;
                mv.visitJumpInsn(167, breakLabel);
            }
        }
        if (!boolHandled) {
            bool.visit(this.controller.getAcg());
            this.controller.getOperandStack().jump(153, breakLabel);
        }
        loop.getLoopBlock().visit(this.controller.getAcg());
        mv.visitJumpInsn(167, continueLabel);
        mv.visitLabel(breakLabel);
        this.controller.getCompileStack().pop();
    }

    public void writeDoWhileLoop(DoWhileStatement loop) {
        this.controller.getAcg().onLineNumber(loop, "visitDoWhileLoop");
        this.writeStatementLabel(loop);
        MethodVisitor mv = this.controller.getMethodVisitor();
        this.controller.getCompileStack().pushLoop(loop.getStatementLabels());
        Label breakLabel = this.controller.getCompileStack().getBreakLabel();
        Label continueLabel = this.controller.getCompileStack().getContinueLabel();
        mv.visitLabel(continueLabel);
        loop.getLoopBlock().visit(this.controller.getAcg());
        loop.getBooleanExpression().visit(this.controller.getAcg());
        this.controller.getOperandStack().jump(153, continueLabel);
        mv.visitLabel(breakLabel);
        this.controller.getCompileStack().pop();
    }

    public void writeIfElse(IfStatement ifElse) {
        this.controller.getAcg().onLineNumber(ifElse, "visitIfElse");
        this.writeStatementLabel(ifElse);
        MethodVisitor mv = this.controller.getMethodVisitor();
        ifElse.getBooleanExpression().visit(this.controller.getAcg());
        Label l0 = this.controller.getOperandStack().jump(153);
        this.controller.getCompileStack().pushBooleanExpression();
        ifElse.getIfBlock().visit(this.controller.getAcg());
        this.controller.getCompileStack().pop();
        if (ifElse.getElseBlock() == EmptyStatement.INSTANCE) {
            mv.visitLabel(l0);
        } else {
            Label l1 = new Label();
            mv.visitJumpInsn(167, l1);
            mv.visitLabel(l0);
            this.controller.getCompileStack().pushBooleanExpression();
            ifElse.getElseBlock().visit(this.controller.getAcg());
            this.controller.getCompileStack().pop();
            mv.visitLabel(l1);
        }
    }

    public void writeTryCatchFinally(TryCatchStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitTryCatchFinally");
        this.writeStatementLabel(statement);
        MethodVisitor mv = this.controller.getMethodVisitor();
        CompileStack compileStack = this.controller.getCompileStack();
        OperandStack operandStack = this.controller.getOperandStack();
        Statement tryStatement = statement.getTryStatement();
        Statement finallyStatement = statement.getFinallyStatement();
        Label tryStart = new Label();
        mv.visitLabel(tryStart);
        CompileStack.BlockRecorder tryBlock = this.makeBlockRecorder(finallyStatement);
        tryBlock.startRange(tryStart);
        tryStatement.visit(this.controller.getAcg());
        Label finallyStart = new Label();
        mv.visitJumpInsn(167, finallyStart);
        Label tryEnd = new Label();
        mv.visitLabel(tryEnd);
        tryBlock.closeRange(tryEnd);
        this.controller.getCompileStack().pop();
        CompileStack.BlockRecorder catches = this.makeBlockRecorder(finallyStatement);
        for (CatchStatement catchStatement : statement.getCatchStatements()) {
            ClassNode exceptionType = catchStatement.getExceptionType();
            String exceptionTypeInternalName = BytecodeHelper.getClassInternalName(exceptionType);
            Label catchStart = new Label();
            mv.visitLabel(catchStart);
            catches.startRange(catchStart);
            Parameter exceptionVariable = catchStatement.getVariable();
            compileStack.pushState();
            compileStack.defineVariable(exceptionVariable, true);
            catchStatement.visit(this.controller.getAcg());
            mv.visitInsn(0);
            this.controller.getCompileStack().pop();
            Label catchEnd = new Label();
            mv.visitLabel(catchEnd);
            catches.closeRange(catchEnd);
            mv.visitJumpInsn(167, finallyStart);
            compileStack.writeExceptionTable(tryBlock, catchStart, exceptionTypeInternalName);
        }
        Label catchAny = new Label();
        compileStack.writeExceptionTable(tryBlock, catchAny, null);
        compileStack.writeExceptionTable(catches, catchAny, null);
        compileStack.pop();
        mv.visitLabel(finallyStart);
        finallyStatement.visit(this.controller.getAcg());
        mv.visitInsn(0);
        Label skipCatchAll = new Label();
        mv.visitJumpInsn(167, skipCatchAll);
        mv.visitLabel(catchAny);
        operandStack.push(ClassHelper.OBJECT_TYPE);
        int anyExceptionIndex = compileStack.defineTemporaryVariable("exception", true);
        finallyStatement.visit(this.controller.getAcg());
        mv.visitVarInsn(25, anyExceptionIndex);
        mv.visitInsn(191);
        mv.visitLabel(skipCatchAll);
        compileStack.removeVar(anyExceptionIndex);
    }

    private CompileStack.BlockRecorder makeBlockRecorder(final Statement finallyStatement) {
        Runnable tryRunner;
        final CompileStack.BlockRecorder block = new CompileStack.BlockRecorder();
        block.excludedStatement = tryRunner = new Runnable(){

            @Override
            public void run() {
                StatementWriter.this.controller.getCompileStack().pushBlockRecorderVisit(block);
                finallyStatement.visit(StatementWriter.this.controller.getAcg());
                StatementWriter.this.controller.getCompileStack().popBlockRecorderVisit(block);
            }
        };
        this.controller.getCompileStack().pushBlockRecorder(block);
        return block;
    }

    public void writeSwitch(SwitchStatement statement) {
        int i;
        this.controller.getAcg().onLineNumber(statement, "visitSwitch");
        this.writeStatementLabel(statement);
        statement.getExpression().visit(this.controller.getAcg());
        Label breakLabel = this.controller.getCompileStack().pushSwitch();
        int switchVariableIndex = this.controller.getCompileStack().defineTemporaryVariable("switch", true);
        List<CaseStatement> caseStatements = statement.getCaseStatements();
        int caseCount = caseStatements.size();
        Label[] labels = new Label[caseCount + 1];
        for (i = 0; i < caseCount; ++i) {
            labels[i] = new Label();
        }
        i = 0;
        for (CaseStatement caseStatement : caseStatements) {
            this.writeCaseStatement(caseStatement, switchVariableIndex, labels[i], labels[i + 1]);
            ++i;
        }
        statement.getDefaultStatement().visit(this.controller.getAcg());
        this.controller.getMethodVisitor().visitLabel(breakLabel);
        this.controller.getCompileStack().removeVar(switchVariableIndex);
        this.controller.getCompileStack().pop();
    }

    protected void writeCaseStatement(CaseStatement statement, int switchVariableIndex, Label thisLabel, Label nextLabel) {
        this.controller.getAcg().onLineNumber(statement, "visitCaseStatement");
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        mv.visitVarInsn(25, switchVariableIndex);
        statement.getExpression().visit(this.controller.getAcg());
        operandStack.box();
        this.controller.getBinaryExpressionHelper().getIsCaseMethod().call(mv);
        operandStack.replace(ClassHelper.boolean_TYPE);
        Label l0 = this.controller.getOperandStack().jump(153);
        mv.visitLabel(thisLabel);
        statement.getCode().visit(this.controller.getAcg());
        if (nextLabel != null) {
            mv.visitJumpInsn(167, nextLabel);
        }
        mv.visitLabel(l0);
    }

    public void writeBreak(BreakStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitBreakStatement");
        this.writeStatementLabel(statement);
        String name = statement.getLabel();
        Label breakLabel = this.controller.getCompileStack().getNamedBreakLabel(name);
        this.controller.getCompileStack().applyFinallyBlocks(breakLabel, true);
        this.controller.getMethodVisitor().visitJumpInsn(167, breakLabel);
    }

    public void writeContinue(ContinueStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitContinueStatement");
        this.writeStatementLabel(statement);
        String name = statement.getLabel();
        Label continueLabel = this.controller.getCompileStack().getContinueLabel();
        if (name != null) {
            continueLabel = this.controller.getCompileStack().getNamedContinueLabel(name);
        }
        this.controller.getCompileStack().applyFinallyBlocks(continueLabel, false);
        this.controller.getMethodVisitor().visitJumpInsn(167, continueLabel);
    }

    public void writeSynchronized(SynchronizedStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitSynchronizedStatement");
        this.writeStatementLabel(statement);
        final MethodVisitor mv = this.controller.getMethodVisitor();
        CompileStack compileStack = this.controller.getCompileStack();
        statement.getExpression().visit(this.controller.getAcg());
        this.controller.getOperandStack().box();
        final int index = compileStack.defineTemporaryVariable("synchronized", ClassHelper.OBJECT_TYPE, true);
        Label synchronizedStart = new Label();
        Label synchronizedEnd = new Label();
        Label catchAll = new Label();
        mv.visitVarInsn(25, index);
        mv.visitInsn(194);
        mv.visitLabel(synchronizedStart);
        mv.visitInsn(0);
        Runnable finallyPart = new Runnable(){

            @Override
            public void run() {
                mv.visitVarInsn(25, index);
                mv.visitInsn(195);
            }
        };
        CompileStack.BlockRecorder fb = new CompileStack.BlockRecorder(finallyPart);
        fb.startRange(synchronizedStart);
        compileStack.pushBlockRecorder(fb);
        statement.getCode().visit(this.controller.getAcg());
        fb.closeRange(catchAll);
        compileStack.writeExceptionTable(fb, catchAll, null);
        compileStack.pop();
        finallyPart.run();
        mv.visitJumpInsn(167, synchronizedEnd);
        mv.visitLabel(catchAll);
        finallyPart.run();
        mv.visitInsn(191);
        mv.visitLabel(synchronizedEnd);
        compileStack.removeVar(index);
    }

    public void writeAssert(AssertStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitAssertStatement");
        this.writeStatementLabel(statement);
        this.controller.getAssertionWriter().writeAssertStatement(statement);
    }

    public void writeThrow(ThrowStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitThrowStatement");
        this.writeStatementLabel(statement);
        MethodVisitor mv = this.controller.getMethodVisitor();
        statement.getExpression().visit(this.controller.getAcg());
        mv.visitTypeInsn(192, "java/lang/Throwable");
        mv.visitInsn(191);
        this.controller.getOperandStack().remove(1);
    }

    public void writeReturn(ReturnStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitReturnStatement");
        this.writeStatementLabel(statement);
        MethodVisitor mv = this.controller.getMethodVisitor();
        OperandStack operandStack = this.controller.getOperandStack();
        ClassNode returnType = this.controller.getReturnType();
        if (returnType == ClassHelper.VOID_TYPE) {
            if (!statement.isReturningNullOrVoid()) {
                this.controller.getAcg().throwException("Cannot use return statement with an expression on a method that returns void");
            }
            this.controller.getCompileStack().applyBlockRecorder();
            mv.visitInsn(177);
            return;
        }
        Expression expression = statement.getExpression();
        expression.visit(this.controller.getAcg());
        operandStack.doGroovyCast(returnType);
        if (this.controller.getCompileStack().hasBlockRecorder()) {
            ClassNode type = operandStack.getTopOperand();
            int returnValueIdx = this.controller.getCompileStack().defineTemporaryVariable("returnValue", returnType, true);
            this.controller.getCompileStack().applyBlockRecorder();
            operandStack.load(type, returnValueIdx);
            this.controller.getCompileStack().removeVar(returnValueIdx);
        }
        BytecodeHelper.doReturn(mv, returnType);
        operandStack.remove(1);
    }

    public void writeExpressionStatement(ExpressionStatement statement) {
        this.controller.getAcg().onLineNumber(statement, "visitExpressionStatement: " + statement.getExpression().getClass().getName());
        this.writeStatementLabel(statement);
        Expression expression = statement.getExpression();
        int mark = this.controller.getOperandStack().getStackLength();
        expression.visit(this.controller.getAcg());
        this.controller.getOperandStack().popDownTo(mark);
    }
}

