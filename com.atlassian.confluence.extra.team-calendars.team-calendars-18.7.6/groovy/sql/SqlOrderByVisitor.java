/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;

public class SqlOrderByVisitor
extends CodeVisitorSupport {
    private StringBuffer buffer = new StringBuffer();

    public String getOrderBy() {
        return this.buffer.toString();
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().visit(this);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        this.buffer.append(expression.getPropertyAsString());
    }
}

