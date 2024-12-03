/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.InfixExpression;

public class ObjectProperty
extends InfixExpression {
    public void setNodeType(int nodeType) {
        if (nodeType != 107 && nodeType != 155 && nodeType != 156 && nodeType != 167) {
            throw new IllegalArgumentException("invalid node type: " + nodeType);
        }
        this.setType(nodeType);
    }

    public ObjectProperty() {
        this.type = 107;
    }

    public ObjectProperty(int pos) {
        super(pos);
        this.type = 107;
    }

    public ObjectProperty(int pos, int len) {
        super(pos, len);
        this.type = 107;
    }

    public void setIsGetterMethod() {
        this.type = 155;
    }

    public boolean isGetterMethod() {
        return this.type == 155;
    }

    public void setIsSetterMethod() {
        this.type = 156;
    }

    public boolean isSetterMethod() {
        return this.type == 156;
    }

    public void setIsNormalMethod() {
        this.type = 167;
    }

    public boolean isNormalMethod() {
        return this.type == 167;
    }

    public boolean isMethod() {
        return this.isGetterMethod() || this.isSetterMethod() || this.isNormalMethod();
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(this.makeIndent(depth + 1));
        if (this.isGetterMethod()) {
            sb.append("get ");
        } else if (this.isSetterMethod()) {
            sb.append("set ");
        }
        sb.append(this.left.toSource(this.getType() == 107 ? 0 : depth));
        if (this.type == 107) {
            sb.append(": ");
        }
        sb.append(this.right.toSource(this.getType() == 107 ? 0 : depth + 1));
        return sb.toString();
    }
}

