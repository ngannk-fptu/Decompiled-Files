/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class TemplateCharacters
extends AstNode {
    private String value;
    private String rawValue;

    public TemplateCharacters() {
        this.type = 171;
    }

    public TemplateCharacters(int pos) {
        super(pos);
        this.type = 171;
    }

    public TemplateCharacters(int pos, int len) {
        super(pos, len);
        this.type = 171;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRawValue() {
        return this.rawValue;
    }

    public void setRawValue(String rawValue) {
        this.assertNotNull(rawValue);
        this.rawValue = rawValue;
    }

    @Override
    public String toSource(int depth) {
        return this.makeIndent(depth) + this.rawValue;
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}

