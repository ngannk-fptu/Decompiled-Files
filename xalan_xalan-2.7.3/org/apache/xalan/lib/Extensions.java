/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.lib.ExsltDynamic;
import org.apache.xalan.lib.ExsltSets;
import org.apache.xalan.lib.ObjectFactory;
import org.apache.xalan.xslt.EnvironmentCheck;
import org.apache.xml.utils.Hashtree2Node;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXNotSupportedException;

public class Extensions {
    private Extensions() {
    }

    public static NodeSet nodeset(ExpressionContext myProcessor, Object rtf) {
        if (rtf instanceof NodeIterator) {
            return new NodeSet((NodeIterator)rtf);
        }
        String textNodeValue = rtf instanceof String ? (String)rtf : (rtf instanceof Boolean ? new XBoolean((boolean)((Boolean)rtf)).str() : (rtf instanceof Double ? new XNumber((Double)rtf).str() : rtf.toString()));
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document myDoc = db.newDocument();
            Text textNode = myDoc.createTextNode(textNodeValue);
            DocumentFragment docFrag = myDoc.createDocumentFragment();
            docFrag.appendChild(textNode);
            return new NodeSet(docFrag);
        }
        catch (ParserConfigurationException pce) {
            throw new WrappedRuntimeException(pce);
        }
    }

    public static NodeList intersection(NodeList nl1, NodeList nl2) {
        return ExsltSets.intersection(nl1, nl2);
    }

    public static NodeList difference(NodeList nl1, NodeList nl2) {
        return ExsltSets.difference(nl1, nl2);
    }

    public static NodeList distinct(NodeList nl) {
        return ExsltSets.distinct(nl);
    }

    public static boolean hasSameNodes(NodeList nl1, NodeList nl2) {
        NodeSet ns1 = new NodeSet(nl1);
        NodeSet ns2 = new NodeSet(nl2);
        if (ns1.getLength() != ns2.getLength()) {
            return false;
        }
        for (int i = 0; i < ns1.getLength(); ++i) {
            Node n = ns1.elementAt(i);
            if (ns2.contains(n)) continue;
            return false;
        }
        return true;
    }

    public static XObject evaluate(ExpressionContext myContext, String xpathExpr) throws SAXNotSupportedException {
        return ExsltDynamic.evaluate(myContext, xpathExpr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NodeList tokenize(String toTokenize, String delims) {
        Document doc = DocumentHolder.m_doc;
        StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
        NodeSet resultSet = new NodeSet();
        Document document = doc;
        synchronized (document) {
            while (lTokenizer.hasMoreTokens()) {
                resultSet.addNode(doc.createTextNode(lTokenizer.nextToken()));
            }
        }
        return resultSet;
    }

    public static NodeList tokenize(String toTokenize) {
        return Extensions.tokenize(toTokenize, " \t\n\r");
    }

    public static Node checkEnvironment(ExpressionContext myContext) {
        Document factoryDocument;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            factoryDocument = db.newDocument();
        }
        catch (ParserConfigurationException pce) {
            throw new WrappedRuntimeException(pce);
        }
        Node resultNode = null;
        try {
            resultNode = Extensions.checkEnvironmentUsingWhich(myContext, factoryDocument);
            if (null != resultNode) {
                return resultNode;
            }
            EnvironmentCheck envChecker = new EnvironmentCheck();
            Hashtable h = envChecker.getEnvironmentHash();
            resultNode = factoryDocument.createElement("checkEnvironmentExtension");
            envChecker.appendEnvironmentReport(resultNode, factoryDocument, h);
            envChecker = null;
        }
        catch (Exception e) {
            throw new WrappedRuntimeException(e);
        }
        return resultNode;
    }

    private static Node checkEnvironmentUsingWhich(ExpressionContext myContext, Document factoryDocument) {
        String WHICH_CLASSNAME = "org.apache.env.Which";
        String WHICH_METHODNAME = "which";
        Class[] WHICH_METHOD_ARGS = new Class[]{Hashtable.class, String.class, String.class};
        try {
            Class clazz = ObjectFactory.findProviderClass("org.apache.env.Which", ObjectFactory.findClassLoader(), true);
            if (null == clazz) {
                return null;
            }
            Method method = clazz.getMethod("which", WHICH_METHOD_ARGS);
            Hashtable report = new Hashtable();
            Object[] methodArgs = new Object[]{report, "XmlCommons;Xalan;Xerces;Crimson;Ant", ""};
            Object returnValue = method.invoke(null, methodArgs);
            Element resultNode = factoryDocument.createElement("checkEnvironmentExtension");
            Hashtree2Node.appendHashToNode(report, "whichReport", resultNode, factoryDocument);
            return resultNode;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static class DocumentHolder {
        private static final Document m_doc;

        private DocumentHolder() {
        }

        static {
            try {
                m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            catch (ParserConfigurationException pce) {
                throw new WrappedRuntimeException(pce);
            }
        }
    }
}

