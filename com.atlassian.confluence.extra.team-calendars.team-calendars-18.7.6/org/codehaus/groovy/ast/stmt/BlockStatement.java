/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.stmt.Statement;

public class BlockStatement
extends Statement {
    private List<Statement> statements = new ArrayList<Statement>();
    private VariableScope scope;

    public BlockStatement() {
        this(new ArrayList<Statement>(), new VariableScope());
    }

    public BlockStatement(List<Statement> statements, VariableScope scope) {
        this.statements = statements;
        this.scope = scope;
    }

    public BlockStatement(Statement[] statements, VariableScope scope) {
        this.statements.addAll(Arrays.asList(statements));
        this.scope = scope;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitBlockStatement(this);
    }

    public List<Statement> getStatements() {
        return this.statements;
    }

    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }

    public void addStatements(List<Statement> listOfStatements) {
        this.statements.addAll(listOfStatements);
    }

    public String toString() {
        return super.toString() + this.statements;
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("{ ");
        boolean first = true;
        for (Statement statement : this.statements) {
            if (first) {
                first = false;
            } else {
                buffer.append("; ");
            }
            buffer.append(statement.getText());
        }
        buffer.append(" }");
        return buffer.toString();
    }

    @Override
    public boolean isEmpty() {
        return this.statements.isEmpty();
    }

    public void setVariableScope(VariableScope scope) {
        this.scope = scope;
    }

    public VariableScope getVariableScope() {
        return this.scope;
    }
}

