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

public class XPathAPI {
    public static Node selectSingleNode(Node contextNode, String str) throws TransformerException {
        return XPathAPI.selectSingleNode(contextNode, str, contextNode);
    }

    public static Node selectSingleNode(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        NodeIterator nl = XPathAPI.selectNodeIterator(contextNode, str, namespaceNode);
        return nl.nextNode();
    }

    public static NodeIterator selectNodeIterator(Node contextNode, String str) throws TransformerException {
        return XPathAPI.selectNodeIterator(contextNode, str, contextNode);
    }

    public static NodeIterator selectNodeIterator(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        XObject list = XPathAPI.eval(contextNode, str, namespaceNode);
        return list.nodeset();
    }

    public static NodeList selectNodeList(Node contextNode, String str) throws TransformerException {
        return XPathAPI.selectNodeList(contextNode, str, contextNode);
    }

    public static NodeList selectNodeList(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        XObject list = XPathAPI.eval(contextNode, str, namespaceNode);
        return list.nodelist();
    }

    public static XObject eval(Node contextNode, String str) throws TransformerException {
        return XPathAPI.eval(contextNode, str, contextNode);
    }

    public static XObject eval(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        XPathContext xpathSupport = new XPathContext(false);
        PrefixResolverDefault prefixResolver = new PrefixResolverDefault(namespaceNode.getNodeType() == 9 ? ((Document)namespaceNode).getDocumentElement() : namespaceNode);
        XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(xpathSupport, ctxtNode, (PrefixResolver)prefixResolver);
    }

    public static XObject eval(Node contextNode, String str, PrefixResolver prefixResolver) throws TransformerException {
        XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        XPathContext xpathSupport = new XPathContext(false);
        int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
    }
}

