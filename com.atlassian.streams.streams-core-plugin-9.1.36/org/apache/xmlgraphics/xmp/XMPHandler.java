/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPArray;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPComplexValue;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.XMPStructure;
import org.apache.xmlgraphics.xmp.XMPThinStructure;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class XMPHandler
extends DefaultHandler {
    private Metadata meta;
    private StringBuffer content = new StringBuffer();
    private Stack attributesStack = new Stack();
    private Stack nestingInfoStack = new Stack();
    private Stack contextStack = new Stack();

    public Metadata getMetadata() {
        return this.meta;
    }

    private boolean hasComplexContent() {
        Object obj = this.contextStack.peek();
        return !(obj instanceof QName);
    }

    private PropertyAccess getCurrentProperties() {
        Object obj = this.contextStack.peek();
        if (obj instanceof PropertyAccess) {
            return (PropertyAccess)obj;
        }
        return null;
    }

    private QName getCurrentPropName() {
        Object obj = this.contextStack.peek();
        if (obj instanceof QName) {
            return (QName)obj;
        }
        return null;
    }

    private QName popCurrentPropName() throws SAXException {
        Object obj = this.contextStack.pop();
        this.nestingInfoStack.pop();
        if (obj instanceof QName) {
            return (QName)obj;
        }
        throw new SAXException("Invalid XMP structure. Property name expected");
    }

    private XMPStructure getCurrentStructure() {
        Object obj = this.contextStack.peek();
        if (obj instanceof XMPStructure) {
            return (XMPStructure)obj;
        }
        return null;
    }

    private XMPArray getCurrentArray(boolean required) throws SAXException {
        Object obj = this.contextStack.peek();
        if (obj instanceof XMPArray) {
            return (XMPArray)obj;
        }
        if (required) {
            throw new SAXException("Invalid XMP structure. Not in array");
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        this.content.setLength(0);
        this.attributesStack.push(new AttributesImpl(attributes));
        if ("adobe:ns:meta/".equals(uri)) {
            if (!"xmpmeta".equals(localName)) {
                throw new SAXException("Expected x:xmpmeta element, not " + qName);
            }
            if (this.meta != null) {
                throw new SAXException("Invalid XMP document. Root already received earlier.");
            }
            this.meta = new Metadata();
            this.contextStack.push(this.meta);
            this.nestingInfoStack.push("metadata");
            return;
        } else if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(uri)) {
            if ("RDF".equals(localName)) {
                if (this.meta != null) return;
                this.meta = new Metadata();
                this.contextStack.push(this.meta);
                this.nestingInfoStack.push("metadata");
                return;
            } else if ("Description".equals(localName)) {
                String about = attributes.getValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about");
                int c = attributes.getLength();
                for (int i = 0; i < c; ++i) {
                    String ns = attributes.getURI(i);
                    if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(ns) || "http://www.w3.org/2000/xmlns/".equals(ns) || "".equals(ns)) continue;
                    String qn = attributes.getQName(i);
                    String v = attributes.getValue(i);
                    XMPProperty prop = new XMPProperty(new QName(ns, qn), v);
                    prop.attribute = true;
                    this.getCurrentProperties().setProperty(prop);
                }
                if (this.contextStack.peek().equals(this.meta)) return;
                if (about != null) {
                    throw new SAXException("Nested rdf:Description elements may not have an about property");
                }
                this.startStructure();
                return;
            } else if ("Seq".equals(localName)) {
                XMPArray array = new XMPArray(XMPArrayType.SEQ);
                this.contextStack.push(array);
                this.nestingInfoStack.push("Seq");
                return;
            } else if ("Bag".equals(localName)) {
                XMPArray array = new XMPArray(XMPArrayType.BAG);
                this.contextStack.push(array);
                this.nestingInfoStack.push("Bag");
                return;
            } else if ("Alt".equals(localName)) {
                XMPArray array = new XMPArray(XMPArrayType.ALT);
                this.contextStack.push(array);
                this.nestingInfoStack.push("Alt");
                return;
            } else {
                if ("li".equals(localName)) return;
                if (!"value".equals(localName)) throw new SAXException("Unexpected element in the RDF namespace: " + localName);
                QName name = new QName(uri, qName);
                this.contextStack.push(name);
                this.nestingInfoStack.push("prop:" + name);
            }
            return;
        } else {
            if (this.getCurrentPropName() != null) {
                this.startStructure();
            }
            QName name = new QName(uri, qName);
            this.contextStack.push(name);
            this.nestingInfoStack.push("prop:" + name);
        }
    }

    private void startStructure() {
        XMPStructure struct = new XMPStructure();
        this.contextStack.push(struct);
        this.nestingInfoStack.push("struct");
    }

    private void startThinStructure() {
        XMPThinStructure struct = new XMPThinStructure();
        this.contextStack.push(struct);
        this.nestingInfoStack.push("struct");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Attributes atts = (Attributes)this.attributesStack.pop();
        if (!"adobe:ns:meta/".equals(uri)) {
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(uri) && !"value".equals(localName)) {
                if ("li".equals(localName)) {
                    XMPStructure struct = this.getCurrentStructure();
                    String parseType = atts.getValue("rdf:parseType");
                    if (struct != null) {
                        this.contextStack.pop();
                        this.nestingInfoStack.pop();
                        this.getCurrentArray(true).add(struct, null, parseType);
                    } else {
                        String s = this.content.toString().trim();
                        if (s.length() > 0) {
                            String lang = atts.getValue("http://www.w3.org/XML/1998/namespace", "lang");
                            this.getCurrentArray(true).add(s, lang, parseType);
                        } else {
                            String res = atts.getValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");
                            if (res != null) {
                                try {
                                    URI resource = new URI(res);
                                    this.getCurrentArray(true).add(resource, null, parseType);
                                }
                                catch (URISyntaxException e) {
                                    throw new SAXException("rdf:resource value is not a well-formed URI", e);
                                }
                            }
                        }
                    }
                } else if ("Description".equals(localName)) {
                    // empty if block
                }
            } else {
                XMPProperty prop;
                if (this.hasComplexContent()) {
                    Object obj = this.contextStack.pop();
                    this.nestingInfoStack.pop();
                    QName name = this.popCurrentPropName();
                    if (!(obj instanceof XMPComplexValue)) throw new UnsupportedOperationException("NYI");
                    XMPComplexValue complexValue = (XMPComplexValue)obj;
                    prop = new XMPProperty(name, complexValue);
                } else {
                    QName name = this.popCurrentPropName();
                    String s = this.content.toString().trim();
                    prop = new XMPProperty(name, s);
                    String lang = atts.getValue("http://www.w3.org/XML/1998/namespace", "lang");
                    String res = atts.getValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");
                    if (lang != null) {
                        prop.setXMLLang(lang);
                    }
                    if (res != null) {
                        try {
                            URI resource = new URI(res);
                            prop.setValue(resource);
                        }
                        catch (URISyntaxException e) {
                            throw new SAXException("rdf:resource value is not a well-formed URI", e);
                        }
                    }
                }
                if (prop.getName() == null) {
                    throw new IllegalStateException("No content in XMP property");
                }
                if (this.getCurrentProperties() == null) {
                    this.startThinStructure();
                }
                this.getCurrentProperties().setProperty(prop);
            }
        }
        this.content.setLength(0);
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.content.append(ch, start, length);
    }
}

