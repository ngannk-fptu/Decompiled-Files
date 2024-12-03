/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.stmt.Statement;

public class ContinueStatement
extends Statement {
    private String label;

    public ContinueStatement() {
        this(null);
    }

    public ContinueStatement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitContinueStatement(this);
    }
}

