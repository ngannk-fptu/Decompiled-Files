/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Scope;

public class Symbol {
    private int declType;
    private int index = -1;
    private String name;
    private Node node;
    private Scope containingTable;

    public Symbol() {
    }

    public Symbol(int declType, String name) {
        this.setName(name);
        this.setDeclType(declType);
    }

    public int getDeclType() {
        return this.declType;
    }

    public void setDeclType(int declType) {
        if (declType != 113 && declType != 90 && declType != 126 && declType != 157 && declType != 158) {
            throw new IllegalArgumentException("Invalid declType: " + declType);
        }
        this.declType = declType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getNode() {
        return this.node;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Scope getContainingTable() {
        return this.containingTable;
    }

    public void setContainingTable(Scope containingTable) {
        this.containingTable = containingTable;
    }

    public String getDeclTypeName() {
        return Token.typeToName(this.declType);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Symbol (");
        result.append(this.getDeclTypeName());
        result.append(") name=");
        result.append(this.name);
        if (this.node != null) {
            result.append(" line=");
            result.append(this.node.getLineno());
        }
        return result.toString();
    }
}

