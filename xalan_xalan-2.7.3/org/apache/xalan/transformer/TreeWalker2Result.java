/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.transformer;

import javax.xml.transform.TransformerException;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.XPathContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TreeWalker2Result
extends DTMTreeWalker {
    TransformerImpl m_transformer;
    SerializationHandler m_handler;
    int m_startNode;

    public TreeWalker2Result(TransformerImpl transformer, SerializationHandler handler) {
        super((ContentHandler)handler, null);
        this.m_transformer = transformer;
        this.m_handler = handler;
    }

    @Override
    public void traverse(int pos) throws SAXException {
        this.m_dtm = this.m_transformer.getXPathContext().getDTM(pos);
        this.m_startNode = pos;
        super.traverse(pos);
    }

    @Override
    protected void endNode(int node) throws SAXException {
        super.endNode(node);
        if (1 == this.m_dtm.getNodeType(node)) {
            this.m_transformer.getXPathContext().popCurrentNode();
        }
    }

    @Override
    protected void startNode(int node) throws SAXException {
        XPathContext xcntxt = this.m_transformer.getXPathContext();
        try {
            if (1 == this.m_dtm.getNodeType(node)) {
                xcntxt.pushCurrentNode(node);
                if (this.m_startNode != node) {
                    super.startNode(node);
                } else {
                    String elemName = this.m_dtm.getNodeName(node);
                    String localName = this.m_dtm.getLocalName(node);
                    String namespace = this.m_dtm.getNamespaceURI(node);
                    this.m_handler.startElement(namespace, localName, elemName);
                    boolean hasNSDecls = false;
                    DTM dtm = this.m_dtm;
                    int ns = dtm.getFirstNamespaceNode(node, true);
                    while (-1 != ns) {
                        SerializerUtils.ensureNamespaceDeclDeclared(this.m_handler, dtm, ns);
                        ns = dtm.getNextNamespaceNode(node, ns, true);
                    }
                    int attr = dtm.getFirstAttribute(node);
                    while (-1 != attr) {
                        SerializerUtils.addAttribute(this.m_handler, attr);
                        attr = dtm.getNextAttribute(attr);
                    }
                }
            } else {
                xcntxt.pushCurrentNode(node);
                super.startNode(node);
                xcntxt.popCurrentNode();
            }
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }
}

