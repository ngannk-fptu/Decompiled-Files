/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.domapi;

import javax.xml.transform.TransformerException;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.domapi.XPathResultImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;

class XPathExpressionImpl
implements XPathExpression {
    private final XPath m_xpath;
    private final Document m_doc;

    XPathExpressionImpl(XPath xpath, Document doc) {
        this.m_xpath = xpath;
        this.m_doc = doc;
    }

    @Override
    public Object evaluate(Node contextNode, short type, Object result) throws XPathException, DOMException {
        if (this.m_doc != null) {
            if (contextNode != this.m_doc && !contextNode.getOwnerDocument().equals(this.m_doc)) {
                String fmsg = XPATHMessages.createXPATHMessage("ER_WRONG_DOCUMENT", null);
                throw new DOMException(4, fmsg);
            }
            short nodeType = contextNode.getNodeType();
            if (nodeType != 9 && nodeType != 1 && nodeType != 2 && nodeType != 3 && nodeType != 4 && nodeType != 8 && nodeType != 7 && nodeType != 13) {
                String fmsg = XPATHMessages.createXPATHMessage("ER_WRONG_NODETYPE", null);
                throw new DOMException(9, fmsg);
            }
        }
        if (!XPathResultImpl.isValidType(type)) {
            String fmsg = XPATHMessages.createXPATHMessage("ER_INVALID_XPATH_TYPE", new Object[]{new Integer(type)});
            throw new XPathException(52, fmsg);
        }
        XPathContext xpathSupport = new XPathContext(false);
        if (null != this.m_doc) {
            xpathSupport.getDTMHandleFromNode(this.m_doc);
        }
        XObject xobj = null;
        try {
            xobj = this.m_xpath.execute(xpathSupport, contextNode, null);
        }
        catch (TransformerException te) {
            throw new XPathException(51, te.getMessageAndLocation());
        }
        return new XPathResultImpl(type, xobj, contextNode, this.m_xpath);
    }
}

