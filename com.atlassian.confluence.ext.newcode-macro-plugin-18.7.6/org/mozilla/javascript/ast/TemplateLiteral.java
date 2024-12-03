/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.TemplateCharacters;

public class TemplateLiteral
extends AstNode {
    private List<AstNode> elements;

    public TemplateLiteral() {
        this.type = 170;
    }

    public TemplateLiteral(int pos) {
        super(pos);
        this.type = 170;
    }

    public TemplateLiteral(int pos, int len) {
        super(pos, len);
        this.type = 170;
    }

    public List<TemplateCharacters> getTemplateStrings() {
        if (this.elements == null) {
            return Collections.emptyList();
        }
        ArrayList<TemplateCharacters> strings = new ArrayList<TemplateCharacters>();
        for (AstNode e : this.elements) {
            if (e.getType() != 171) continue;
            strings.add((TemplateCharacters)e);
        }
        return Collections.unmodifiableList(strings);
    }

    public List<AstNode> getSubstitutions() {
        if (this.elements == null) {
            return Collections.emptyList();
        }
        ArrayList<AstNode> subs = new ArrayList<AstNode>();
        for (AstNode e : this.elements) {
            if (e.getType() == 171) continue;
            subs.add(e);
        }
        return Collections.unmodifiableList(subs);
    }

    public List<AstNode> getElements() {
        if (this.elements == null) {
            return Collections.emptyList();
        }
        return this.elements;
    }

    public void setElements(List<AstNode> elements) {
        if (elements == null) {
            this.elements = null;
        } else {
            if (this.elements != null) {
                this.elements.clear();
            }
            for (AstNode e : elements) {
                this.addElement(e);
            }
        }
    }

    public void addElement(AstNode element) {
        this.assertNotNull(element);
        if (this.elements == null) {
            this.elements = new ArrayList<AstNode>();
        }
        this.elements.add(element);
        element.setParent(this);
    }

    public int getSize() {
        return this.elements == null ? 0 : this.elements.size();
    }

    public AstNode getElement(int index) {
        if (this.elements == null) {
            throw new IndexOutOfBoundsException("no elements");
        }
        return this.elements.get(index);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append("`");
        for (AstNode e : this.getElements()) {
            if (e.getType() == 171) {
                sb.append(e.toSource(0));
                continue;
            }
            sb.append("${").append(e.toSource(0)).append("}");
        }
        sb.append("`");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (AstNode e : this.getElements()) {
                e.visit(v);
            }
        }
    }
}

