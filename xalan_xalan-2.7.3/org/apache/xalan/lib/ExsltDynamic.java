/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.lib.ExsltBase;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.NodeSet;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXNotSupportedException;

public class ExsltDynamic
extends ExsltBase {
    public static final String EXSL_URI = "http://exslt.org/common";

    public static double max(ExpressionContext myContext, NodeList nl, String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double maxValue = -1.7976931348623157E308;
        for (int i = 0; i < contextNodes.getLength(); ++i) {
            int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            if (!(result > maxValue)) continue;
            maxValue = result;
        }
        xctxt.popContextNodeList();
        return maxValue;
    }

    public static double min(ExpressionContext myContext, NodeList nl, String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double minValue = Double.MAX_VALUE;
        for (int i = 0; i < nl.getLength(); ++i) {
            int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            if (!(result < minValue)) continue;
            minValue = result;
        }
        xctxt.popContextNodeList();
        return minValue;
    }

    public static double sum(ExpressionContext myContext, NodeList nl, String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return Double.NaN;
        }
        NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        double sum = 0.0;
        for (int i = 0; i < nl.getLength(); ++i) {
            int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            double result = 0.0;
            try {
                XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                result = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext()).num();
            }
            catch (TransformerException e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return Double.NaN;
            }
            xctxt.popCurrentNode();
            sum += result;
        }
        xctxt.popContextNodeList();
        return sum;
    }

    public static NodeList map(ExpressionContext myContext, NodeList nl, String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        Document lDoc = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return new NodeSet();
        }
        NodeSetDTM contextNodes = new NodeSetDTM(nl, xctxt);
        xctxt.pushContextNodeList(contextNodes);
        NodeSet resultSet = new NodeSet();
        resultSet.setShouldCacheNodes(true);
        for (int i = 0; i < nl.getLength(); ++i) {
            int contextNode = contextNodes.item(i);
            xctxt.pushCurrentNode(contextNode);
            XObject object = null;
            try {
                XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                object = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                if (object instanceof XNodeSet) {
                    NodeList nodelist = null;
                    nodelist = ((XNodeSet)object).nodelist();
                    for (int k = 0; k < nodelist.getLength(); ++k) {
                        Node n = nodelist.item(k);
                        if (resultSet.contains(n)) continue;
                        resultSet.addNode(n);
                    }
                } else {
                    if (lDoc == null) {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        lDoc = db.newDocument();
                    }
                    Element element = null;
                    element = object instanceof XNumber ? lDoc.createElementNS(EXSL_URI, "exsl:number") : (object instanceof XBoolean ? lDoc.createElementNS(EXSL_URI, "exsl:boolean") : lDoc.createElementNS(EXSL_URI, "exsl:string"));
                    Text textNode = lDoc.createTextNode(object.str());
                    element.appendChild(textNode);
                    resultSet.addNode(element);
                }
            }
            catch (Exception e) {
                xctxt.popCurrentNode();
                xctxt.popContextNodeList();
                return new NodeSet();
            }
            xctxt.popCurrentNode();
        }
        xctxt.popContextNodeList();
        return resultSet;
    }

    public static XObject evaluate(ExpressionContext myContext, String xpathExpr) throws SAXNotSupportedException {
        if (myContext instanceof XPathContext.XPathExpressionContext) {
            XPathContext xctxt = null;
            try {
                xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
                XPath dynamicXPath = new XPath(xpathExpr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                return dynamicXPath.execute(xctxt, myContext.getContextNode(), xctxt.getNamespaceContext());
            }
            catch (TransformerException e) {
                return new XNodeSet(xctxt.getDTMManager());
            }
        }
        throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
    }

    public static NodeList closure(ExpressionContext myContext, NodeList nl, String expr) throws SAXNotSupportedException {
        XPathContext xctxt = null;
        if (!(myContext instanceof XPathContext.XPathExpressionContext)) {
            throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[]{myContext}));
        }
        xctxt = ((XPathContext.XPathExpressionContext)myContext).getXPathContext();
        if (expr == null || expr.length() == 0) {
            return new NodeSet();
        }
        NodeSet closureSet = new NodeSet();
        closureSet.setShouldCacheNodes(true);
        NodeList iterationList = nl;
        do {
            int i;
            NodeSet iterationSet = new NodeSet();
            NodeSetDTM contextNodes = new NodeSetDTM(iterationList, xctxt);
            xctxt.pushContextNodeList(contextNodes);
            for (i = 0; i < iterationList.getLength(); ++i) {
                block9: {
                    int contextNode = contextNodes.item(i);
                    xctxt.pushCurrentNode(contextNode);
                    XObject object = null;
                    try {
                        XPath dynamicXPath = new XPath(expr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 0);
                        object = dynamicXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                        if (object instanceof XNodeSet) {
                            NodeList nodelist = null;
                            nodelist = ((XNodeSet)object).nodelist();
                            for (int k = 0; k < nodelist.getLength(); ++k) {
                                Node n = nodelist.item(k);
                                if (iterationSet.contains(n)) continue;
                                iterationSet.addNode(n);
                            }
                            break block9;
                        }
                        xctxt.popCurrentNode();
                        xctxt.popContextNodeList();
                        return new NodeSet();
                    }
                    catch (TransformerException e) {
                        xctxt.popCurrentNode();
                        xctxt.popContextNodeList();
                        return new NodeSet();
                    }
                }
                xctxt.popCurrentNode();
            }
            xctxt.popContextNodeList();
            iterationList = iterationSet;
            for (i = 0; i < iterationList.getLength(); ++i) {
                Node n = iterationList.item(i);
                if (closureSet.contains(n)) continue;
                closureSet.addNode(n);
            }
        } while (iterationList.getLength() > 0);
        return closureSet;
    }
}

