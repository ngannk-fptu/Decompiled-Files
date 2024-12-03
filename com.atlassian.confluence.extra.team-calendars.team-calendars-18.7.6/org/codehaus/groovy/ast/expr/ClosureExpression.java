/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.AstToTextHelper;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.runtime.InvokerHelper;

public class ClosureExpression
extends Expression {
    private Parameter[] parameters;
    private Statement code;
    private VariableScope variableScope;

    public ClosureExpression(Parameter[] parameters, Statement code) {
        this.parameters = parameters;
        this.code = code;
        super.setType(ClassHelper.CLOSURE_TYPE.getPlainNodeReference());
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitClosureExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    public String toString() {
        return super.toString() + InvokerHelper.toString(this.parameters) + "{ " + this.code + " }";
    }

    public Statement getCode() {
        return this.code;
    }

    public void setCode(Statement code) {
        this.code = code;
    }

    public Parameter[] getParameters() {
        return this.parameters;
    }

    public boolean isParameterSpecified() {
        return this.parameters != null && this.parameters.length > 0;
    }

    public VariableScope getVariableScope() {
        return this.variableScope;
    }

    public void setVariableScope(VariableScope variableScope) {
        this.variableScope = variableScope;
    }

    @Override
    public String getText() {
        String paramText = AstToTextHelper.getParametersText(this.parameters);
        if (paramText.length() > 0) {
            return "{ " + paramText + " -> ... }";
        }
        return "{ -> ... }";
    }
}

