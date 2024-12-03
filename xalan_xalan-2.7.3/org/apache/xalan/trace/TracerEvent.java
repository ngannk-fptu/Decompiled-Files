/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.trace;

import java.util.EventListener;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TracerEvent
implements EventListener {
    public final ElemTemplateElement m_styleNode;
    public final TransformerImpl m_processor;
    public final Node m_sourceNode;
    public final QName m_mode;

    public TracerEvent(TransformerImpl processor, Node sourceNode, QName mode, ElemTemplateElement styleNode) {
        this.m_processor = processor;
        this.m_sourceNode = sourceNode;
        this.m_mode = mode;
        this.m_styleNode = styleNode;
    }

    public static String printNode(Node n) {
        String r = n.hashCode() + " ";
        if (n instanceof Element) {
            r = r + "<" + n.getNodeName();
            for (Node c = n.getFirstChild(); null != c; c = c.getNextSibling()) {
                if (!(c instanceof Attr)) continue;
                r = r + TracerEvent.printNode(c) + " ";
            }
            r = r + ">";
        } else {
            r = n instanceof Attr ? r + n.getNodeName() + "=" + n.getNodeValue() : r + n.getNodeName();
        }
        return r;
    }

    public static String printNodeList(NodeList l) {
        Node n;
        int i;
        String r = l.hashCode() + "[";
        int len = l.getLength() - 1;
        for (i = 0; i < len; ++i) {
            n = l.item(i);
            if (null == n) continue;
            r = r + TracerEvent.printNode(n) + ", ";
        }
        if (i == len && null != (n = l.item(len))) {
            r = r + TracerEvent.printNode(n);
        }
        return r + "]";
    }
}

