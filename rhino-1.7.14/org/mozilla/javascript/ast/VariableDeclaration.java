/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableInitializer;

public class VariableDeclaration
extends AstNode {
    private List<VariableInitializer> variables = new ArrayList<VariableInitializer>();
    private boolean isStatement;

    public VariableDeclaration() {
        this.type = 126;
    }

    public VariableDeclaration(int pos) {
        super(pos);
        this.type = 126;
    }

    public VariableDeclaration(int pos, int len) {
        super(pos, len);
        this.type = 126;
    }

    public List<VariableInitializer> getVariables() {
        return this.variables;
    }

    public void setVariables(List<VariableInitializer> variables) {
        this.assertNotNull(variables);
        this.variables.clear();
        for (VariableInitializer vi : variables) {
            this.addVariable(vi);
        }
    }

    public void addVariable(VariableInitializer v) {
        this.assertNotNull(v);
        this.variables.add(v);
        v.setParent(this);
    }

    @Override
    public Node setType(int type) {
        if (type != 126 && type != 158 && type != 157) {
            throw new IllegalArgumentException("invalid decl type: " + type);
        }
        return super.setType(type);
    }

    public boolean isVar() {
        return this.type == 126;
    }

    public boolean isConst() {
        return this.type == 158;
    }

    public boolean isLet() {
        return this.type == 157;
    }

    public boolean isStatement() {
        return this.isStatement;
    }

    public void setIsStatement(boolean isStatement) {
        this.isStatement = isStatement;
    }

    private String declTypeName() {
        return Token.typeToName(this.type).toLowerCase();
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append(this.declTypeName());
        sb.append(" ");
        this.printList(this.variables, sb);
        if (this.isStatement()) {
            sb.append(";");
        }
        if (this.getInlineComment() != null) {
            sb.append(this.getInlineComment().toSource(depth)).append("\n");
        } else if (this.isStatement()) {
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (AstNode astNode : this.variables) {
                astNode.visit(v);
            }
        }
    }
}

