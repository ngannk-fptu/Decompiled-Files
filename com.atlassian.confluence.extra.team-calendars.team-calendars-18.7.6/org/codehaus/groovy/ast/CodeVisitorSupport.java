/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.List;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
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
import org.codehaus.groovy.classgen.BytecodeExpression;

public abstract class CodeVisitorSupport
implements GroovyCodeVisitor {
    @Override
    public void visitBlockStatement(BlockStatement block) {
        for (Statement statement : block.getStatements()) {
            statement.visit(this);
        }
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        forLoop.getCollectionExpression().visit(this);
        forLoop.getLoopBlock().visit(this);
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        loop.getBooleanExpression().visit(this);
        loop.getLoopBlock().visit(this);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        loop.getLoopBlock().visit(this);
        loop.getBooleanExpression().visit(this);
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        ifElse.getBooleanExpression().visit(this);
        ifElse.getIfBlock().visit(this);
        Statement elseBlock = ifElse.getElseBlock();
        if (elseBlock instanceof EmptyStatement) {
            this.visitEmptyStatement((EmptyStatement)elseBlock);
        } else {
            elseBlock.visit(this);
        }
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        statement.getExpression().visit(this);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().visit(this);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        statement.getBooleanExpression().visit(this);
        statement.getMessageExpression().visit(this);
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        statement.getTryStatement().visit(this);
        for (CatchStatement catchStatement : statement.getCatchStatements()) {
            catchStatement.visit(this);
        }
        Statement finallyStatement = statement.getFinallyStatement();
        if (finallyStatement instanceof EmptyStatement) {
            this.visitEmptyStatement((EmptyStatement)finallyStatement);
        } else {
            finallyStatement.visit(this);
        }
    }

    protected void visitEmptyStatement(EmptyStatement statement) {
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        statement.getExpression().visit(this);
        for (CaseStatement caseStatement : statement.getCaseStatements()) {
            caseStatement.visit(this);
        }
        statement.getDefaultStatement().visit(this);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        statement.getExpression().visit(this);
        statement.getCode().visit(this);
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        statement.getExpression().visit(this);
        statement.getCode().visit(this);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        statement.getExpression().visit(this);
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        call.getObjectExpression().visit(this);
        call.getMethod().visit(this);
        call.getArguments().visit(this);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        call.getArguments().visit(this);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        call.getArguments().visit(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        expression.getLeftExpression().visit(this);
        expression.getRightExpression().visit(this);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        expression.getBooleanExpression().visit(this);
        expression.getTrueExpression().visit(this);
        expression.getFalseExpression().visit(this);
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        this.visitTernaryExpression(expression);
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        expression.getCode().visit(this);
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        this.visitListOfExpressions(expression.getExpressions());
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        this.visitListOfExpressions(expression.getExpressions());
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        this.visitListOfExpressions(expression.getExpressions());
        this.visitListOfExpressions(expression.getSizeExpression());
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        this.visitListOfExpressions(expression.getMapEntryExpressions());
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        expression.getKeyExpression().visit(this);
        expression.getValueExpression().visit(this);
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        expression.getFrom().visit(this);
        expression.getTo().visit(this);
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        expression.getExpression().visit(this);
        expression.getMethodName().visit(this);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        this.visitBinaryExpression(expression);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        expression.getObjectExpression().visit(this);
        expression.getProperty().visit(this);
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
        expression.getObjectExpression().visit(this);
        expression.getProperty().visit(this);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        this.visitListOfExpressions(expression.getStrings());
        this.visitListOfExpressions(expression.getValues());
    }

    protected void visitListOfExpressions(List<? extends Expression> list) {
        if (list == null) {
            return;
        }
        for (Expression expression : list) {
            if (expression instanceof SpreadExpression) {
                Expression spread = ((SpreadExpression)expression).getExpression();
                spread.visit(this);
                continue;
            }
            expression.visit(this);
        }
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        statement.getCode().visit(this);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
        this.visitTupleExpression(ale);
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression cle) {
        this.visitListOfExpressions(cle.getExpressions());
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression cle) {
    }
}

