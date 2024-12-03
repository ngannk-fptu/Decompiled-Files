/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

public abstract class ClassCodeExpressionTransformer
extends ClassCodeVisitorSupport
implements ExpressionTransformer {
    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        for (Parameter p : node.getParameters()) {
            if (!p.hasInitialExpression()) continue;
            Expression init = p.getInitialExpression();
            p.setInitialExpression(this.transform(init));
        }
        super.visitConstructorOrMethod(node, isConstructor);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        Expression exp = statement.getExpression();
        statement.setExpression(this.transform(exp));
        for (CaseStatement caseStatement : statement.getCaseStatements()) {
            caseStatement.visit(this);
        }
        statement.getDefaultStatement().visit(this);
    }

    @Override
    public void visitField(FieldNode node) {
        this.visitAnnotations(node);
        Expression init = node.getInitialExpression();
        node.setInitialValueExpression(this.transform(init));
    }

    @Override
    public void visitProperty(PropertyNode node) {
        this.visitAnnotations(node);
        Statement statement = node.getGetterBlock();
        this.visitClassCodeContainer(statement);
        statement = node.getSetterBlock();
        this.visitClassCodeContainer(statement);
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        ifElse.setBooleanExpression((BooleanExpression)this.transform(ifElse.getBooleanExpression()));
        ifElse.getIfBlock().visit(this);
        ifElse.getElseBlock().visit(this);
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp == null) {
            return null;
        }
        return exp.transformExpression(this);
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        List<AnnotationNode> annotations = node.getAnnotations();
        if (annotations.isEmpty()) {
            return;
        }
        for (AnnotationNode an : annotations) {
            if (an.isBuiltIn()) continue;
            for (Map.Entry<String, Expression> member : an.getMembers().entrySet()) {
                member.setValue(this.transform(member.getValue()));
            }
        }
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.setExpression(this.transform(statement.getExpression()));
    }

    @Override
    public void visitAssertStatement(AssertStatement as) {
        as.setBooleanExpression((BooleanExpression)this.transform(as.getBooleanExpression()));
        as.setMessageExpression(this.transform(as.getMessageExpression()));
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        statement.setExpression(this.transform(statement.getExpression()));
        statement.getCode().visit(this);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        loop.setBooleanExpression((BooleanExpression)this.transform(loop.getBooleanExpression()));
        super.visitDoWhileLoop(loop);
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        forLoop.setCollectionExpression(this.transform(forLoop.getCollectionExpression()));
        super.visitForLoop(forLoop);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement sync) {
        sync.setExpression(this.transform(sync.getExpression()));
        super.visitSynchronizedStatement(sync);
    }

    @Override
    public void visitThrowStatement(ThrowStatement ts) {
        ts.setExpression(this.transform(ts.getExpression()));
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        loop.setBooleanExpression((BooleanExpression)this.transform(loop.getBooleanExpression()));
        super.visitWhileLoop(loop);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement es) {
        es.setExpression(this.transform(es.getExpression()));
    }
}

