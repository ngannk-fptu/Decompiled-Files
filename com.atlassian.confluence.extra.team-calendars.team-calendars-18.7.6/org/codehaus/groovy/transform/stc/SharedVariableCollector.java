/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;

public class SharedVariableCollector
extends ClassCodeVisitorSupport {
    private final SourceUnit unit;
    private final Set<VariableExpression> closureSharedExpressions = new LinkedHashSet<VariableExpression>();
    private boolean visited = false;

    public SharedVariableCollector(SourceUnit unit) {
        this.unit = unit;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.unit;
    }

    public Set<VariableExpression> getClosureSharedExpressions() {
        return Collections.unmodifiableSet(this.closureSharedExpressions);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        if (this.visited) {
            return;
        }
        this.visited = true;
        if (expression.isClosureSharedVariable()) {
            this.closureSharedExpressions.add(expression);
        }
        super.visitVariableExpression(expression);
    }
}

