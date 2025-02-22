/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class CachedXPathAPI {
    protected XPathContext xpathSupport;

    public CachedXPathAPI() {
        this.xpathSupport = new XPathContext(false);
    }

    public CachedXPathAPI(CachedXPathAPI priorXPathAPI) {
        this.xpathSupport = priorXPathAPI.xpathSupport;
    }

    public XPathContext getXPathContext() {
        return this.xpathSupport;
    }

    public Node selectSingleNode(Node contextNode, String str) throws TransformerException {
        return this.selectSingleNode(contextNode, str, contextNode);
    }

    public Node selectSingleNode(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        NodeIterator nl = this.selectNodeIterator(contextNode, str, namespaceNode);
        return nl.nextNode();
    }

    public NodeIterator selectNodeIterator(Node contextNode, String str) throws TransformerException {
        return this.selectNodeIterator(contextNode, str, contextNode);
    }

    public NodeIterator selectNodeIterator(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        XObject list = this.eval(contextNode, str, namespaceNode);
        return list.nodeset();
    }

    public NodeList selectNodeList(Node contextNode, String str) throws TransformerException {
        return this.selectNodeList(contextNode, str, contextNode);
    }

    public NodeList selectNodeList(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        XObject list = this.eval(contextNode, str, namespaceNode);
        return list.nodelist();
    }

    public XObject eval(Node contextNode, String str) throws TransformerException {
        return this.eval(contextNode, str, contextNode);
    }

    public XObject eval(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        PrefixResolverDefault prefixResolver = new PrefixResolverDefault(namespaceNode.getNodeType() == 9 ? ((Document)namespaceNode).getDocumentElement() : namespaceNode);
        XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        int ctxtNode = this.xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(this.xpathSupport, ctxtNode, (PrefixResolver)prefixResolver);
    }

    public XObject eval(Node contextNode, String str, PrefixResolver prefixResolver) throws TransformerException {
        XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        XPathContext xpathSupport = new XPathContext(false);
        int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
    }
}

