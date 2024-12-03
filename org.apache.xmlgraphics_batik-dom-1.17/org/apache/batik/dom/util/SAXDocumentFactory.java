/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.dom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.batik.dom.util.DocumentDescriptor;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.dom.util.HashTableStack;
import org.apache.batik.dom.util.SAXIOException;
import org.apache.batik.util.HaltingThread;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXDocumentFactory
extends DefaultHandler
implements LexicalHandler,
DocumentFactory {
    protected DOMImplementation implementation;
    protected String parserClassName;
    protected XMLReader parser;
    protected Document document;
    protected DocumentDescriptor documentDescriptor;
    protected boolean createDocumentDescriptor;
    protected Node currentNode;
    protected Locator locator;
    protected StringBuffer stringBuffer = new StringBuffer();
    protected DocumentType doctype;
    protected boolean stringContent;
    protected boolean inDTD;
    protected boolean inCDATA;
    protected boolean inProlog;
    protected boolean isValidating;
    protected boolean isStandalone;
    protected String xmlVersion;
    protected HashTableStack namespaces;
    protected ErrorHandler errorHandler;
    protected List preInfo;
    static SAXParserFactory saxFactory = SAXParserFactory.newInstance();

    public SAXDocumentFactory(DOMImplementation impl, String parser) {
        this.implementation = impl;
        this.parserClassName = parser;
    }

    public SAXDocumentFactory(DOMImplementation impl, String parser, boolean dd) {
        this.implementation = impl;
        this.parserClassName = parser;
        this.createDocumentDescriptor = dd;
    }

    @Override
    public Document createDocument(String ns, String root, String uri) throws IOException {
        return this.createDocument(ns, root, uri, new InputSource(uri));
    }

    public Document createDocument(String uri) throws IOException {
        return this.createDocument(new InputSource(uri));
    }

    @Override
    public Document createDocument(String ns, String root, String uri, InputStream is) throws IOException {
        InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return this.createDocument(ns, root, uri, inp);
    }

    public Document createDocument(String uri, InputStream is) throws IOException {
        InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return this.createDocument(inp);
    }

    @Override
    public Document createDocument(String ns, String root, String uri, Reader r) throws IOException {
        InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return this.createDocument(ns, root, uri, inp);
    }

    @Override
    public Document createDocument(String ns, String root, String uri, XMLReader r) throws IOException {
        r.setContentHandler(this);
        r.setDTDHandler(this);
        r.setEntityResolver(this);
        try {
            r.parse(uri);
        }
        catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException)ex;
            }
            throw new SAXIOException(e);
        }
        this.currentNode = null;
        Document ret = this.document;
        this.document = null;
        this.doctype = null;
        return ret;
    }

    public Document createDocument(String uri, Reader r) throws IOException {
        InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return this.createDocument(inp);
    }

    protected Document createDocument(String ns, String root, String uri, InputSource is) throws IOException {
        String docElemNS;
        Document ret = this.createDocument(is);
        Element docElem = ret.getDocumentElement();
        String lname = root;
        String nsURI = ns;
        if (ns == null) {
            int idx = lname.indexOf(58);
            String nsp = idx == -1 || idx == lname.length() - 1 ? "" : lname.substring(0, idx);
            nsURI = this.namespaces.get(nsp);
            if (idx != -1 && idx != lname.length() - 1) {
                lname = lname.substring(idx + 1);
            }
        }
        if (!((docElemNS = docElem.getNamespaceURI()) == nsURI || docElemNS != null && docElemNS.equals(nsURI))) {
            throw new IOException("Root element namespace does not match that requested:\nRequested: " + nsURI + "\nFound: " + docElemNS);
        }
        if (docElemNS != null) {
            if (!docElem.getLocalName().equals(lname)) {
                throw new IOException("Root element does not match that requested:\nRequested: " + lname + "\nFound: " + docElem.getLocalName());
            }
        } else if (!docElem.getNodeName().equals(lname)) {
            throw new IOException("Root element does not match that requested:\nRequested: " + lname + "\nFound: " + docElem.getNodeName());
        }
        return ret;
    }

    protected Document createDocument(InputSource is) throws IOException {
        try {
            if (this.parserClassName != null) {
                this.parser = XMLReaderFactory.createXMLReader(this.parserClassName);
            } else {
                SAXParser saxParser;
                try {
                    saxParser = saxFactory.newSAXParser();
                }
                catch (ParserConfigurationException pce) {
                    throw new IOException("Could not create SAXParser: " + pce.getMessage());
                }
                this.parser = saxParser.getXMLReader();
            }
            this.parser.setContentHandler(this);
            this.parser.setDTDHandler(this);
            this.parser.setEntityResolver(this);
            this.parser.setErrorHandler(this.errorHandler == null ? this : this.errorHandler);
            this.parser.setFeature("http://xml.org/sax/features/namespaces", true);
            this.parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            this.parser.setFeature("http://xml.org/sax/features/validation", this.isValidating);
            this.parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            this.parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            this.parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            this.parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            this.parser.parse(is);
        }
        catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException)ex;
            }
            throw new SAXIOException(e);
        }
        this.currentNode = null;
        Document ret = this.document;
        this.document = null;
        this.doctype = null;
        this.locator = null;
        this.parser = null;
        return ret;
    }

    @Override
    public DocumentDescriptor getDocumentDescriptor() {
        return this.documentDescriptor;
    }

    @Override
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    @Override
    public void setValidating(boolean isValidating) {
        this.isValidating = isValidating;
    }

    @Override
    public boolean isValidating() {
        return this.isValidating;
    }

    public void setErrorHandler(ErrorHandler eh) {
        this.errorHandler = eh;
    }

    public DOMImplementation getDOMImplementation(String ver) {
        return this.implementation;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
    }

    @Override
    public void startDocument() throws SAXException {
        this.preInfo = new LinkedList();
        this.namespaces = new HashTableStack();
        this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        this.namespaces.put("xmlns", "http://www.w3.org/2000/xmlns/");
        this.namespaces.put("", null);
        this.inDTD = false;
        this.inCDATA = false;
        this.inProlog = true;
        this.currentNode = null;
        this.document = null;
        this.doctype = null;
        this.isStandalone = false;
        this.xmlVersion = "1.0";
        this.stringBuffer.setLength(0);
        this.stringContent = false;
        this.documentDescriptor = this.createDocumentDescriptor ? new DocumentDescriptor() : null;
    }

    @Override
    public void startElement(String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        Element e;
        if (HaltingThread.hasBeenHalted()) {
            throw new SAXException(new InterruptedIOException());
        }
        if (this.inProlog) {
            this.inProlog = false;
            if (this.parser != null) {
                try {
                    this.isStandalone = this.parser.getFeature("http://xml.org/sax/features/is-standalone");
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                    // empty catch block
                }
                try {
                    this.xmlVersion = (String)this.parser.getProperty("http://xml.org/sax/properties/document-xml-version");
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                    // empty catch block
                }
            }
        }
        int len = attributes.getLength();
        this.namespaces.push();
        String version = null;
        for (int i = 0; i < len; ++i) {
            String ns;
            String aname = attributes.getQName(i);
            int slen = aname.length();
            if (slen < 5) continue;
            if (aname.equals("version")) {
                version = attributes.getValue(i);
                continue;
            }
            if (!aname.startsWith("xmlns")) continue;
            if (slen == 5) {
                ns = attributes.getValue(i);
                if (ns.length() == 0) {
                    ns = null;
                }
                this.namespaces.put("", ns);
                continue;
            }
            if (aname.charAt(5) != ':') continue;
            ns = attributes.getValue(i);
            if (ns.length() == 0) {
                ns = null;
            }
            this.namespaces.put(aname.substring(6), ns);
        }
        this.appendStringData();
        int idx = rawName.indexOf(58);
        String nsp = idx == -1 || idx == rawName.length() - 1 ? "" : rawName.substring(0, idx);
        String nsURI = this.namespaces.get(nsp);
        if (this.currentNode == null) {
            this.implementation = this.getDOMImplementation(version);
            this.document = this.implementation.createDocument(nsURI, rawName, this.doctype);
            Iterator i = this.preInfo.iterator();
            e = this.document.getDocumentElement();
            this.currentNode = e;
            while (i.hasNext()) {
                PreInfo pi = (PreInfo)i.next();
                Node n = pi.createNode(this.document);
                this.document.insertBefore(n, e);
            }
            this.preInfo = null;
        } else {
            e = this.document.createElementNS(nsURI, rawName);
            this.currentNode.appendChild(e);
            this.currentNode = e;
        }
        if (this.createDocumentDescriptor && this.locator != null) {
            this.documentDescriptor.setLocation(e, this.locator.getLineNumber(), this.locator.getColumnNumber());
        }
        for (int i = 0; i < len; ++i) {
            String aname = attributes.getQName(i);
            if (aname.equals("xmlns")) {
                e.setAttributeNS("http://www.w3.org/2000/xmlns/", aname, attributes.getValue(i));
                continue;
            }
            idx = aname.indexOf(58);
            nsURI = idx == -1 ? null : this.namespaces.get(aname.substring(0, idx));
            e.setAttributeNS(nsURI, aname, attributes.getValue(i));
        }
    }

    @Override
    public void endElement(String uri, String localName, String rawName) throws SAXException {
        this.appendStringData();
        if (this.currentNode != null) {
            this.currentNode = this.currentNode.getParentNode();
        }
        this.namespaces.pop();
    }

    public void appendStringData() {
        if (!this.stringContent) {
            return;
        }
        String str = this.stringBuffer.toString();
        this.stringBuffer.setLength(0);
        this.stringContent = false;
        if (this.currentNode == null) {
            if (this.inCDATA) {
                this.preInfo.add(new CDataInfo(str));
            } else {
                this.preInfo.add(new TextInfo(str));
            }
        } else {
            Text n = this.inCDATA ? this.document.createCDATASection(str) : this.document.createTextNode(str);
            this.currentNode.appendChild(n);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.stringBuffer.append(ch, start, length);
        this.stringContent = true;
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.stringBuffer.append(ch, start, length);
        this.stringContent = true;
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.inDTD) {
            return;
        }
        this.appendStringData();
        if (this.currentNode == null) {
            this.preInfo.add(new ProcessingInstructionInfo(target, data));
        } else {
            this.currentNode.appendChild(this.document.createProcessingInstruction(target, data));
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.appendStringData();
        this.doctype = this.implementation.createDocumentType(name, publicId, systemId);
        this.inDTD = true;
    }

    @Override
    public void endDTD() throws SAXException {
        this.inDTD = false;
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        this.appendStringData();
        this.inCDATA = true;
        this.stringContent = true;
    }

    @Override
    public void endCDATA() throws SAXException {
        this.appendStringData();
        this.inCDATA = false;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.inDTD) {
            return;
        }
        this.appendStringData();
        String str = new String(ch, start, length);
        if (this.currentNode == null) {
            this.preInfo.add(new CommentInfo(str));
        } else {
            this.currentNode.appendChild(this.document.createComment(str));
        }
    }

    static {
        try {
            saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
        catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        }
        catch (SAXNotSupportedException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    static class TextInfo
    implements PreInfo {
        public String text;

        public TextInfo(String text) {
            this.text = text;
        }

        @Override
        public Node createNode(Document doc) {
            return doc.createTextNode(this.text);
        }
    }

    static class CDataInfo
    implements PreInfo {
        public String cdata;

        public CDataInfo(String cdata) {
            this.cdata = cdata;
        }

        @Override
        public Node createNode(Document doc) {
            return doc.createCDATASection(this.cdata);
        }
    }

    static class CommentInfo
    implements PreInfo {
        public String comment;

        public CommentInfo(String comment) {
            this.comment = comment;
        }

        @Override
        public Node createNode(Document doc) {
            return doc.createComment(this.comment);
        }
    }

    static class ProcessingInstructionInfo
    implements PreInfo {
        public String target;
        public String data;

        public ProcessingInstructionInfo(String target, String data) {
            this.target = target;
            this.data = data;
        }

        @Override
        public Node createNode(Document doc) {
            return doc.createProcessingInstruction(this.target, this.data);
        }
    }

    protected static interface PreInfo {
        public Node createNode(Document var1);
    }
}

