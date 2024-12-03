/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class DOMVisitor {
    public void visit(Document dom) {
        this.visit(dom.getDocumentElement());
    }

    public void visit(Element e) {
        NodeList lst = e.getChildNodes();
        int len = lst.getLength();
        for (int i = 0; i < len; ++i) {
            Node n = lst.item(i);
            if (n.getNodeType() == 1) {
                this.visit((Element)n);
                continue;
            }
            this.visitNode(n);
        }
    }

    public void visitNode(Node n) {
    }
}

