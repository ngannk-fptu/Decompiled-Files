/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;

public class Statement
extends ASTNode {
    private List<String> statementLabels = null;

    public List<String> getStatementLabels() {
        return this.statementLabels;
    }

    public String getStatementLabel() {
        return this.statementLabels == null ? null : this.statementLabels.get(0);
    }

    public void setStatementLabel(String label) {
        if (this.statementLabels == null) {
            this.statementLabels = new LinkedList<String>();
        }
        this.statementLabels.add(label);
    }

    public void addStatementLabel(String label) {
        if (this.statementLabels == null) {
            this.statementLabels = new LinkedList<String>();
        }
        this.statementLabels.add(label);
    }

    public boolean isEmpty() {
        return false;
    }
}

