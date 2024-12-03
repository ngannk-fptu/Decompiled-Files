/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ResolveVisitor;

public class JavaAwareResolveVisitor
extends ResolveVisitor {
    public JavaAwareResolveVisitor(CompilationUnit cu) {
        super(cu);
    }

    private static Expression getConstructorCall(Statement code) {
        if (code == null) {
            return null;
        }
        if (code instanceof BlockStatement) {
            BlockStatement bs = (BlockStatement)code;
            if (bs.isEmpty()) {
                return null;
            }
            return JavaAwareResolveVisitor.getConstructorCall(bs.getStatements().get(0));
        }
        if (!(code instanceof ExpressionStatement)) {
            return null;
        }
        ExpressionStatement es = (ExpressionStatement)code;
        Expression exp = es.getExpression();
        if (!(exp instanceof ConstructorCallExpression)) {
            return null;
        }
        ConstructorCallExpression cce = (ConstructorCallExpression)exp;
        if (!cce.isSpecialCall()) {
            return null;
        }
        return cce;
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        super.visitConstructor(node);
        Statement code = node.getCode();
        Expression cce = JavaAwareResolveVisitor.getConstructorCall(code);
        if (cce == null) {
            return;
        }
        cce.visit(this);
    }

    @Override
    protected void visitClassCodeContainer(Statement code) {
    }

    @Override
    protected void addError(String msg, ASTNode expr) {
    }
}

