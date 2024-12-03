/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.domapi;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.domapi.XPathExpressionImpl;
import org.apache.xpath.domapi.XPathNSResolverImpl;
import org.apache.xpath.domapi.XPathStylesheetDOM3Exception;
import org.apache.xpath.res.XPATHMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;

public final class XPathEvaluatorImpl
implements XPathEvaluator {
    private final Document m_doc;

    public XPathEvaluatorImpl(Document doc) {
        this.m_doc = doc;
    }

    public XPathEvaluatorImpl() {
        this.m_doc = null;
    }

    @Override
    public XPathExpression createExpression(String expression, XPathNSResolver resolver) throws XPathException, DOMException {
        try {
            XPath xpath = new XPath(expression, null, null == resolver ? new DummyPrefixResolver() : (PrefixResolver)((Object)resolver), 0);
            return new XPathExpressionImpl(xpath, this.m_doc);
        }
        catch (TransformerException e) {
            if (e instanceof XPathStylesheetDOM3Exception) {
                throw new DOMException(14, e.getMessageAndLocation());
            }
            throw new XPathException(51, e.getMessageAndLocation());
        }
    }

    @Override
    public XPathNSResolver createNSResolver(Node nodeResolver) {
        return new XPathNSResolverImpl(nodeResolver.getNodeType() == 9 ? ((Document)nodeResolver).getDocumentElement() : nodeResolver);
    }

    @Override
    public Object evaluate(String expression, Node contextNode, XPathNSResolver resolver, short type, Object result) throws XPathException, DOMException {
        XPathExpression xpathExpression = this.createExpression(expression, resolver);
        return xpathExpression.evaluate(contextNode, type, result);
    }

    private static class DummyPrefixResolver
    implements PrefixResolver {
        DummyPrefixResolver() {
        }

        @Override
        public String getNamespaceForPrefix(String prefix, Node context) {
            String fmsg = XPATHMessages.createXPATHMessage("ER_NULL_RESOLVER", null);
            throw new DOMException(14, fmsg);
        }

        @Override
        public String getNamespaceForPrefix(String prefix) {
            return this.getNamespaceForPrefix(prefix, null);
        }

        @Override
        public boolean handlesNullPrefixes() {
            return false;
        }

        @Override
        public String getBaseIdentifier() {
            return null;
        }
    }
}

