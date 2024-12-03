/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.stmt.Statement;

public class BreakStatement
extends Statement {
    private String label;

    public BreakStatement() {
        this(null);
    }

    public BreakStatement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitBreakStatement(this);
    }
}

