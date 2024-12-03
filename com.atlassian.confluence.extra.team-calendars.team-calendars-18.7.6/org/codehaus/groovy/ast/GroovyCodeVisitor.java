/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

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
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;

public interface GroovyCodeVisitor {
    public void visitBlockStatement(BlockStatement var1);

    public void visitForLoop(ForStatement var1);

    public void visitWhileLoop(WhileStatement var1);

    public void visitDoWhileLoop(DoWhileStatement var1);

    public void visitIfElse(IfStatement var1);

    public void visitExpressionStatement(ExpressionStatement var1);

    public void visitReturnStatement(ReturnStatement var1);

    public void visitAssertStatement(AssertStatement var1);

    public void visitTryCatchFinally(TryCatchStatement var1);

    public void visitSwitch(SwitchStatement var1);

    public void visitCaseStatement(CaseStatement var1);

    public void visitBreakStatement(BreakStatement var1);

    public void visitContinueStatement(ContinueStatement var1);

    public void visitThrowStatement(ThrowStatement var1);

    public void visitSynchronizedStatement(SynchronizedStatement var1);

    public void visitCatchStatement(CatchStatement var1);

    public void visitMethodCallExpression(MethodCallExpression var1);

    public void visitStaticMethodCallExpression(StaticMethodCallExpression var1);

    public void visitConstructorCallExpression(ConstructorCallExpression var1);

    public void visitTernaryExpression(TernaryExpression var1);

    public void visitShortTernaryExpression(ElvisOperatorExpression var1);

    public void visitBinaryExpression(BinaryExpression var1);

    public void visitPrefixExpression(PrefixExpression var1);

    public void visitPostfixExpression(PostfixExpression var1);

    public void visitBooleanExpression(BooleanExpression var1);

    public void visitClosureExpression(ClosureExpression var1);

    public void visitTupleExpression(TupleExpression var1);

    public void visitMapExpression(MapExpression var1);

    public void visitMapEntryExpression(MapEntryExpression var1);

    public void visitListExpression(ListExpression var1);

    public void visitRangeExpression(RangeExpression var1);

    public void visitPropertyExpression(PropertyExpression var1);

    public void visitAttributeExpression(AttributeExpression var1);

    public void visitFieldExpression(FieldExpression var1);

    public void visitMethodPointerExpression(MethodPointerExpression var1);

    public void visitConstantExpression(ConstantExpression var1);

    public void visitClassExpression(ClassExpression var1);

    public void visitVariableExpression(VariableExpression var1);

    public void visitDeclarationExpression(DeclarationExpression var1);

    public void visitGStringExpression(GStringExpression var1);

    public void visitArrayExpression(ArrayExpression var1);

    public void visitSpreadExpression(SpreadExpression var1);

    public void visitSpreadMapExpression(SpreadMapExpression var1);

    public void visitNotExpression(NotExpression var1);

    public void visitUnaryMinusExpression(UnaryMinusExpression var1);

    public void visitUnaryPlusExpression(UnaryPlusExpression var1);

    public void visitBitwiseNegationExpression(BitwiseNegationExpression var1);

    public void visitCastExpression(CastExpression var1);

    public void visitArgumentlistExpression(ArgumentListExpression var1);

    public void visitClosureListExpression(ClosureListExpression var1);

    public void visitBytecodeExpression(BytecodeExpression var1);
}

