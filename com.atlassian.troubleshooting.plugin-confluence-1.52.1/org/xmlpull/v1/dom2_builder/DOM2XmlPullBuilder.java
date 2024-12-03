/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.dom2_builder;

import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DOM2XmlPullBuilder {
    protected Document newDoc() throws XmlPullParserException {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            return builder.newDocument();
        }
        catch (FactoryConfigurationError ex) {
            throw new XmlPullParserException("could not configure factory JAXP DocumentBuilderFactory: " + ex, null, ex);
        }
        catch (ParserConfigurationException ex) {
            throw new XmlPullParserException("could not configure parser JAXP DocumentBuilderFactory: " + ex, null, ex);
        }
    }

    protected XmlPullParser newParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        return factory.newPullParser();
    }

    public Element parse(Reader reader) throws XmlPullParserException, IOException {
        Document docFactory = this.newDoc();
        return this.parse(reader, docFactory);
    }

    public Element parse(Reader reader, Document docFactory) throws XmlPullParserException, IOException {
        XmlPullParser pp = this.newParser();
        pp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        pp.setInput(reader);
        pp.next();
        return this.parse(pp, docFactory);
    }

    public Element parse(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
        Element root = this.parseSubTree(pp, docFactory);
        return root;
    }

    public Element parseSubTree(XmlPullParser pp) throws XmlPullParserException, IOException {
        Document doc = this.newDoc();
        Element root = this.parseSubTree(pp, doc);
        return root;
    }

    public Element parseSubTree(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
        BuildProcess process = new BuildProcess();
        return process.parseSubTree(pp, docFactory);
    }

    private static void assertEquals(String expected, String s) {
        if (expected != null && !expected.equals(s) || expected == null && s == null) {
            throw new RuntimeException("expected '" + expected + "' but got '" + s + "'");
        }
    }

    private static void assertNotNull(Object o) {
        if (o == null) {
            throw new RuntimeException("expected no null value");
        }
    }

    public static void main(String[] args) throws Exception {
    }

    static class BuildProcess {
        private XmlPullParser pp;
        private Document docFactory;
        private boolean scanNamespaces = true;

        private BuildProcess() {
        }

        public Element parseSubTree(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
            this.pp = pp;
            this.docFactory = docFactory;
            return this.parseSubTree();
        }

        private Element parseSubTree() throws XmlPullParserException, IOException {
            this.pp.require(2, null, null);
            String name = this.pp.getName();
            String ns = this.pp.getNamespace();
            String prefix = this.pp.getPrefix();
            String qname = prefix != null ? prefix + ":" + name : name;
            Element parent = this.docFactory.createElementNS(ns, qname);
            this.declareNamespaces(this.pp, parent);
            for (int i = 0; i < this.pp.getAttributeCount(); ++i) {
                String attrNs = this.pp.getAttributeNamespace(i);
                String attrName = this.pp.getAttributeName(i);
                String attrValue = this.pp.getAttributeValue(i);
                if (attrNs == null || attrNs.length() == 0) {
                    parent.setAttribute(attrName, attrValue);
                    continue;
                }
                String attrPrefix = this.pp.getAttributePrefix(i);
                String attrQname = attrPrefix != null ? attrPrefix + ":" + attrName : attrName;
                parent.setAttributeNS(attrNs, attrQname, attrValue);
            }
            while (this.pp.next() != 3) {
                if (this.pp.getEventType() == 2) {
                    Element el = this.parseSubTree(this.pp, this.docFactory);
                    parent.appendChild(el);
                    continue;
                }
                if (this.pp.getEventType() == 4) {
                    String text = this.pp.getText();
                    Text textEl = this.docFactory.createTextNode(text);
                    parent.appendChild(textEl);
                    continue;
                }
                throw new XmlPullParserException("unexpected event " + XmlPullParser.TYPES[this.pp.getEventType()], this.pp, null);
            }
            this.pp.require(3, ns, name);
            return parent;
        }

        private void declareNamespaces(XmlPullParser pp, Element parent) throws DOMException, XmlPullParserException {
            if (this.scanNamespaces) {
                int top;
                this.scanNamespaces = false;
                block0: for (int i = top = pp.getNamespaceCount(pp.getDepth()) - 1; i >= pp.getNamespaceCount(0); --i) {
                    String prefix = pp.getNamespacePrefix(i);
                    for (int j = top; j > i; --j) {
                        String prefixJ = pp.getNamespacePrefix(j);
                        if (prefix != null && prefix.equals(prefixJ) || prefix != null && prefix == prefixJ) continue block0;
                    }
                    this.declareOneNamespace(pp, i, parent);
                }
            } else {
                for (int i = pp.getNamespaceCount(pp.getDepth() - 1); i < pp.getNamespaceCount(pp.getDepth()); ++i) {
                    this.declareOneNamespace(pp, i, parent);
                }
            }
        }

        private void declareOneNamespace(XmlPullParser pp, int i, Element parent) throws DOMException, XmlPullParserException {
            String xmlnsPrefix = pp.getNamespacePrefix(i);
            String xmlnsUri = pp.getNamespaceUri(i);
            String xmlnsDecl = xmlnsPrefix != null ? "xmlns:" + xmlnsPrefix : "xmlns";
            parent.setAttributeNS("http://www.w3.org/2000/xmlns/", xmlnsDecl, xmlnsUri);
        }
    }
}

