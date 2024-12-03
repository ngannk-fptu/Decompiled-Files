/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import java.util.Arrays;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;

class AutoNewLineTransformer
extends ClassCodeVisitorSupport {
    private final SourceUnit unit;
    private boolean inBuilderMethod;

    public AutoNewLineTransformer(SourceUnit unit) {
        this.unit = unit;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        boolean old = this.inBuilderMethod;
        this.inBuilderMethod = false;
        if (call.isImplicitThis() && call.getArguments() instanceof TupleExpression) {
            Expression lastArg;
            List<Expression> expressions = ((TupleExpression)call.getArguments()).getExpressions();
            if (!expressions.isEmpty() && (lastArg = expressions.get(expressions.size() - 1)) instanceof ClosureExpression) {
                call.getObjectExpression().visit(this);
                call.getMethod().visit(this);
                for (Expression expression : expressions) {
                    this.inBuilderMethod = expression == lastArg;
                    expression.visit(this);
                }
            }
        } else {
            super.visitMethodCallExpression(call);
        }
        this.inBuilderMethod = old;
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        super.visitClosureExpression(expression);
        if (this.inBuilderMethod) {
            Statement oldCode = expression.getCode();
            BlockStatement block = oldCode instanceof BlockStatement ? (BlockStatement)oldCode : new BlockStatement(Arrays.asList(oldCode), new VariableScope());
            List<Statement> statements = block.getStatements();
            if (!statements.isEmpty()) {
                Statement first = statements.get(0);
                Statement last = statements.get(statements.size() - 1);
                if (expression.getLineNumber() < first.getLineNumber()) {
                    statements.add(0, this.createNewLine(expression));
                }
                if (expression.getLastLineNumber() > last.getLastLineNumber()) {
                    statements.add(this.createNewLine(expression));
                }
            }
            expression.setCode(block);
        }
    }

    private Statement createNewLine(ASTNode node) {
        MethodCallExpression mce = new MethodCallExpression((Expression)new VariableExpression("this"), "newLine", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        mce.setImplicitThis(true);
        mce.setSourcePosition(node);
        ExpressionStatement stmt = new ExpressionStatement(mce);
        stmt.setSourcePosition(node);
        return stmt;
    }
}

