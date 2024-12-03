/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.ListExpression;

public class ClosureListExpression
extends ListExpression {
    private VariableScope scope = new VariableScope();

    public ClosureListExpression(List<Expression> expressions) {
        super(expressions);
    }

    public ClosureListExpression() {
        this(new ArrayList<Expression>(3));
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitClosureListExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        ClosureListExpression ret = new ClosureListExpression(this.transformExpressions(this.getExpressions(), transformer));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public void setVariableScope(VariableScope scope) {
        this.scope = scope;
    }

    public VariableScope getVariableScope() {
        return this.scope;
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("(");
        boolean first = true;
        for (Expression expression : this.getExpressions()) {
            if (first) {
                first = false;
            } else {
                buffer.append("; ");
            }
            buffer.append(expression.getText());
        }
        buffer.append(")");
        return buffer.toString();
    }
}

