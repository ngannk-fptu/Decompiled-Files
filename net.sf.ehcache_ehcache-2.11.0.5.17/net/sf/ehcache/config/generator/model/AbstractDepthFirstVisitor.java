/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import java.util.List;
import net.sf.ehcache.config.generator.model.NodeAttribute;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.NodeElementVisitor;

public abstract class AbstractDepthFirstVisitor
implements NodeElementVisitor {
    @Override
    public void visit(NodeElement element) {
        if (element == null) {
            throw new NullPointerException("element cannot be null");
        }
        this.doDfs(element);
    }

    private void doDfs(NodeElement element) {
        this.startElement(element);
        this.startAttributes(element);
        this.visitAttributes(element, element.getAttributes());
        this.endAttributes(element);
        this.visitElement(element);
        this.startChildren(element);
        for (NodeElement child : element.getChildElements()) {
            this.doDfs(child);
        }
        this.endChildren(element);
        this.endElement(element);
    }

    protected void startElement(NodeElement element) {
    }

    protected void startAttributes(NodeElement element) {
    }

    protected void visitAttributes(NodeElement element, List<NodeAttribute> attributes) {
    }

    protected void endAttributes(NodeElement element) {
    }

    protected void visitElement(NodeElement element) {
    }

    protected void startChildren(NodeElement element) {
    }

    protected void endChildren(NodeElement element) {
    }

    protected void endElement(NodeElement element) {
    }
}

