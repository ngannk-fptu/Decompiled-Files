/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.asm.ExpressionAsVariableSlot;
import org.codehaus.groovy.classgen.asm.WriterController;

public class TemporaryVariableExpression
extends Expression {
    private Expression expression;
    private ExpressionAsVariableSlot variable;

    public TemporaryVariableExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        TemporaryVariableExpression result = new TemporaryVariableExpression(this.expression.transformExpression(transformer));
        result.copyNodeMetaData(this);
        return result;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        if (visitor instanceof AsmClassGenerator) {
            if (this.variable == null) {
                AsmClassGenerator acg = (AsmClassGenerator)visitor;
                WriterController controller = acg.getController();
                this.variable = new ExpressionAsVariableSlot(controller, this.expression);
            }
            this.variable.visit(visitor);
        } else {
            this.expression.visit(visitor);
        }
    }

    public void remove(WriterController controller) {
        controller.getCompileStack().removeVar(this.variable.getIndex());
        this.variable = null;
    }

    @Override
    public ClassNode getType() {
        return this.expression.getType();
    }
}

