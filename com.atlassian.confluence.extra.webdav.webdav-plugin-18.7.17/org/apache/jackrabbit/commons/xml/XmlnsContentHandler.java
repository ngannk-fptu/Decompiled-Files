/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.jackrabbit.commons.xml.ProxyContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XmlnsContentHandler
extends ProxyContentHandler {
    private static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";
    private final LinkedHashMap namespaces = new LinkedHashMap();

    public XmlnsContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.namespaces.put(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (!this.namespaces.isEmpty()) {
            AttributesImpl attributes = new AttributesImpl(atts);
            for (Map.Entry entry : this.namespaces.entrySet()) {
                String prefix = (String)entry.getKey();
                String uri = (String)entry.getValue();
                if (prefix.length() == 0) {
                    attributes.addAttribute(XMLNS_NAMESPACE, "xmlns", "xmlns", "CDATA", uri);
                    continue;
                }
                attributes.addAttribute(XMLNS_NAMESPACE, prefix, "xmlns:" + prefix, "CDATA", uri);
            }
            atts = attributes;
            this.namespaces.clear();
        }
        super.startElement(namespaceURI, localName, qName, atts);
    }
}

