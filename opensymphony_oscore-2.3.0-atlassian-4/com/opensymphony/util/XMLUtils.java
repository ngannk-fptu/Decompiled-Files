/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import com.opensymphony.provider.ProviderFactory;
import com.opensymphony.provider.ProviderInvocationException;
import com.opensymphony.provider.XMLPrinterProvider;
import com.opensymphony.provider.XPathProvider;
import com.opensymphony.provider.xmlprinter.DefaultXMLPrinterProvider;
import com.opensymphony.provider.xpath.XalanXPathProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {
    private static final XPathProvider xPathProvider;
    private static final XMLPrinterProvider xmlPrinterProvider;
    private static int cacheSize;
    private static HashMap xslCache;
    private static LinkedList xslKeyList;

    public static final String getElementText(Element element) {
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node c = nl.item(i);
            if (!(c instanceof Text)) continue;
            return ((Text)c).getData();
        }
        return null;
    }

    public static final Node cloneNode(Node node, Document target, boolean deep) throws DOMException {
        Node newNode;
        if (target == null || node.getOwnerDocument() == target) {
            return node.cloneNode(deep);
        }
        short nodeType = node.getNodeType();
        switch (nodeType) {
            case 2: {
                newNode = target.createAttribute(node.getNodeName());
                break;
            }
            case 11: {
                newNode = target.createDocumentFragment();
                break;
            }
            case 1: {
                Element newElement = target.createElement(node.getNodeName());
                NamedNodeMap nodeAttr = node.getAttributes();
                if (nodeAttr != null) {
                    for (int i = 0; i < nodeAttr.getLength(); ++i) {
                        Attr attr = (Attr)nodeAttr.item(i);
                        if (!attr.getSpecified()) continue;
                        Attr newAttr = (Attr)XMLUtils.cloneNode(attr, target, true);
                        newElement.setAttributeNode(newAttr);
                    }
                }
                newNode = newElement;
                break;
            }
            case 5: {
                newNode = target.createEntityReference(node.getNodeName());
                break;
            }
            case 7: {
                newNode = target.createProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 3: {
                newNode = target.createTextNode(node.getNodeValue());
                break;
            }
            case 4: {
                newNode = target.createCDATASection(node.getNodeValue());
                break;
            }
            case 8: {
                newNode = target.createComment(node.getNodeValue());
                break;
            }
            default: {
                throw new IllegalArgumentException("Importing of " + node + " not supported yet");
            }
        }
        if (deep) {
            for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                newNode.appendChild(XMLUtils.cloneNode(child, target, true));
            }
        }
        return newNode;
    }

    public static final Document newDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    public static final Document newDocument(String rootElementName) throws ParserConfigurationException {
        Document doc = XMLUtils.newDocument();
        doc.appendChild(doc.createElement(rootElementName));
        return doc;
    }

    public static final Document parse(InputSource in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(in);
    }

    public static final Document parse(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(new InputSource(in));
    }

    public static final Document parse(Reader in) throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(new InputSource(in));
    }

    public static final Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(new InputSource(new FileInputStream(file)));
    }

    public static final Document parse(URL url) throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(new InputSource(url.toString()));
    }

    public static final Document parse(String xml) throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(new InputSource(new StringReader(xml)));
    }

    public static final void print(Document document, Writer out) throws IOException {
        xmlPrinterProvider.print(document, out);
    }

    public static final void print(Document document, OutputStream out) throws IOException {
        XMLUtils.print(document, new OutputStreamWriter(out));
    }

    public static final void print(Document document, File file) throws IOException {
        XMLUtils.print(document, new FileWriter(file));
    }

    public static final String print(Document document) throws IOException {
        StringWriter result = new StringWriter();
        XMLUtils.print(document, result);
        return result.toString();
    }

    public static final void transform(Reader xml, Reader xsl, Writer result) throws TransformerException {
        XMLUtils.transform(xml, xsl, result, null);
    }

    public static final Node xpath(Node base, String xpath) throws TransformerException {
        try {
            return xPathProvider.getNode(base, xpath);
        }
        catch (ProviderInvocationException e) {
            try {
                throw e.getCause();
            }
            catch (TransformerException te) {
                throw te;
            }
            catch (Throwable tw) {
                tw.printStackTrace();
                return null;
            }
        }
    }

    public static final NodeList xpathList(Node base, String xpath) throws TransformerException {
        try {
            return xPathProvider.getNodes(base, xpath);
        }
        catch (ProviderInvocationException e) {
            try {
                throw e.getCause();
            }
            catch (TransformerException te) {
                throw te;
            }
            catch (Throwable tw) {
                tw.printStackTrace();
                return null;
            }
        }
    }

    public static void setCacheSize(int newCacheSize) {
        cacheSize = newCacheSize;
    }

    public static int getCacheSize() {
        return cacheSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void transform(Reader xml, Reader xsl, Writer result, Map parameters, String xslkey) throws TransformerException {
        try {
            Transformer t;
            if (null != xslkey && xslCache.containsKey(xslkey)) {
                t = (Transformer)xslCache.get(xslkey);
                LinkedList linkedList = xslKeyList;
                synchronized (linkedList) {
                    xslKeyList.remove(xslkey);
                    xslKeyList.add(xslkey);
                }
            }
            TransformerFactory factory = TransformerFactory.newInstance();
            t = factory.newTransformer(new StreamSource(xsl));
            if (null != xslkey) {
                xslCache.put(xslkey, t);
                xslKeyList.add(xslkey);
                LinkedList linkedList = xslKeyList;
                synchronized (linkedList) {
                    int s = xslKeyList.size();
                    int cacheSize = XMLUtils.getCacheSize();
                    int iterations = 1;
                    if (s > cacheSize + 1) {
                        iterations = 2;
                    }
                    while (iterations-- != 0) {
                        Object removalKey = xslKeyList.get(0);
                        xslKeyList.remove(0);
                        xslCache.remove(removalKey);
                    }
                }
            }
            if (parameters != null) {
                for (Object key : parameters.keySet()) {
                    Object value = parameters.get(key);
                    t.setParameter(key.toString(), value.toString());
                }
            }
            t.transform(new StreamSource(xml), new StreamResult(result));
        }
        catch (TransformerConfigurationException tce) {
            throw new TransformerException(tce);
        }
    }

    public static final void transform(Reader xml, Reader xsl, Writer result, Map parameters) throws TransformerException {
        XMLUtils.transform(xml, xsl, result, parameters, xsl.toString());
    }

    public static final void transform(InputStream xml, InputStream xsl, OutputStream result) throws TransformerException {
        XMLUtils.transform(new InputStreamReader(xml), new InputStreamReader(xsl), new OutputStreamWriter(result));
    }

    public static final String transform(String xml, String xsl) throws TransformerException {
        StringWriter result = new StringWriter();
        XMLUtils.transform(new StringReader(xml), new StringReader(xsl), result);
        return result.toString();
    }

    public static final Document transform(Document xml, Document xsl) throws ParserConfigurationException, TransformerException {
        try {
            Document result = XMLUtils.newDocument();
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer(new DOMSource(xsl));
            t.transform(new DOMSource(xml), new DOMResult(result));
            return result;
        }
        catch (TransformerConfigurationException tce) {
            throw new TransformerException(tce);
        }
    }

    static {
        ProviderFactory factory = ProviderFactory.getInstance();
        xPathProvider = (XPathProvider)factory.getProvider("xpath.provider", XalanXPathProvider.class.getName());
        xmlPrinterProvider = (XMLPrinterProvider)factory.getProvider("xmlprinter.provider", DefaultXMLPrinterProvider.class.getName());
        cacheSize = 10;
        xslCache = new HashMap();
        xslKeyList = new LinkedList();
    }
}

