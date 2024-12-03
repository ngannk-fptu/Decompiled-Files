/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.apache.jackrabbit.commons.xml.DefaultContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class SerializingContentHandler
extends DefaultContentHandler {
    public static final String ENCODING = "UTF-8";
    private static final String XML = "http://www.w3.org/XML/1998/namespace";
    private static final SAXTransformerFactory FACTORY = (SAXTransformerFactory)TransformerFactory.newInstance();
    private static final boolean NEEDS_XMLNS_ATTRIBUTES = SerializingContentHandler.needsXmlnsAttributes();
    private List prefixList = new ArrayList();
    private List uriList = new ArrayList();
    private Map uriToPrefixMap = new HashMap();
    private Map prefixToUriMap = new HashMap();
    private boolean hasMappings = false;
    private final List addedPrefixMappings = new ArrayList();

    private static boolean needsXmlnsAttributes() {
        try {
            StringWriter writer = new StringWriter();
            TransformerHandler probe = FACTORY.newTransformerHandler();
            probe.setResult(new StreamResult(writer));
            probe.startDocument();
            probe.startPrefixMapping("p", "uri");
            probe.startElement("uri", "e", "p:e", new AttributesImpl());
            probe.endElement("uri", "e", "p:e");
            probe.endPrefixMapping("p");
            probe.endDocument();
            return writer.toString().indexOf("xmlns") == -1;
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("XML serialization fails");
        }
    }

    public static DefaultHandler getSerializer(OutputStream output) throws SAXException {
        return SerializingContentHandler.getSerializer(new StreamResult(output));
    }

    public static DefaultHandler getSerializer(Writer writer) throws SAXException {
        return SerializingContentHandler.getSerializer(new StreamResult(writer));
    }

    public static DefaultHandler getSerializer(Result result) throws SAXException {
        try {
            TransformerHandler handler = FACTORY.newTransformerHandler();
            handler.setResult(result);
            Transformer transformer = handler.getTransformer();
            transformer.setOutputProperty("method", "xml");
            transformer.setOutputProperty("encoding", ENCODING);
            transformer.setOutputProperty("indent", "no");
            if (NEEDS_XMLNS_ATTRIBUTES) {
                return new SerializingContentHandler(handler);
            }
            return new DefaultContentHandler(handler);
        }
        catch (TransformerConfigurationException e) {
            throw new SAXException("Failed to initialize XML serializer", e);
        }
    }

    private SerializingContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void startDocument() throws SAXException {
        this.uriToPrefixMap.clear();
        this.prefixToUriMap.clear();
        this.clearMappings();
        super.startDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (uri != null && !prefix.startsWith("xml")) {
            this.hasMappings = true;
            this.prefixList.add(prefix);
            this.uriList.add(uri);
            if (prefix.length() > 0) {
                this.uriToPrefixMap.put(uri, prefix + ":");
            } else {
                this.uriToPrefixMap.put(uri, prefix);
            }
            this.prefixToUriMap.put(prefix, uri);
        }
        super.startPrefixMapping(prefix, uri);
    }

    private void checkPrefixMapping(String uri, String qname) throws SAXException {
        if (uri != null && uri.length() > 0 && !uri.startsWith("xml") && !this.uriToPrefixMap.containsKey(uri)) {
            int colon;
            String prefix = "ns";
            if (qname != null && qname.length() > 0 && (colon = qname.indexOf(58)) != -1) {
                prefix = qname.substring(0, colon);
            }
            String base = prefix;
            int i = 2;
            while (this.prefixToUriMap.containsKey(prefix)) {
                prefix = base + i;
                ++i;
            }
            int last = this.addedPrefixMappings.size() - 1;
            ArrayList<String> prefixes = (ArrayList<String>)this.addedPrefixMappings.get(last);
            if (prefixes == null) {
                prefixes = new ArrayList<String>();
                this.addedPrefixMappings.set(last, prefixes);
            }
            prefixes.add(prefix);
            this.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void startElement(String eltUri, String eltLocalName, String eltQName, Attributes attrs) throws SAXException {
        this.addedPrefixMappings.add(null);
        this.checkPrefixMapping(eltUri, eltQName);
        for (int i = 0; i < attrs.getLength(); ++i) {
            this.checkPrefixMapping(attrs.getURI(i), attrs.getQName(i));
        }
        if (null != eltUri && eltUri.length() != 0 && this.uriToPrefixMap.containsKey(eltUri)) {
            eltQName = this.uriToPrefixMap.get(eltUri) + eltLocalName;
        }
        if (this.hasMappings) {
            AttributesImpl newAttrs = null;
            int mappingCount = this.prefixList.size();
            int attrCount = attrs.getLength();
            for (int mapping = 0; mapping < mappingCount; ++mapping) {
                String uri = (String)this.uriList.get(mapping);
                String prefix = (String)this.prefixList.get(mapping);
                String qName = prefix.equals("") ? "xmlns" : "xmlns:" + prefix;
                boolean found = false;
                for (int attr = 0; attr < attrCount; ++attr) {
                    if (!qName.equals(attrs.getQName(attr))) continue;
                    if (!uri.equals(attrs.getValue(attr))) {
                        throw new SAXException("URI in prefix mapping and attribute do not match");
                    }
                    found = true;
                    break;
                }
                if (found) continue;
                if (newAttrs == null) {
                    newAttrs = attrCount == 0 ? new AttributesImpl() : new AttributesImpl(attrs);
                }
                if (prefix.equals("")) {
                    newAttrs.addAttribute(XML, qName, qName, "CDATA", uri);
                    continue;
                }
                newAttrs.addAttribute(XML, prefix, qName, "CDATA", uri);
            }
            this.clearMappings();
            super.startElement(eltUri, eltLocalName, eltQName, newAttrs == null ? attrs : newAttrs);
        } else {
            super.startElement(eltUri, eltLocalName, eltQName, attrs);
        }
    }

    @Override
    public void endElement(String eltUri, String eltLocalName, String eltQName) throws SAXException {
        if (null != eltUri && eltUri.length() != 0 && this.uriToPrefixMap.containsKey(eltUri)) {
            eltQName = this.uriToPrefixMap.get(eltUri) + eltLocalName;
        }
        super.endElement(eltUri, eltLocalName, eltQName);
        int last = this.addedPrefixMappings.size() - 1;
        List prefixes = (List)this.addedPrefixMappings.remove(last);
        if (prefixes != null) {
            Iterator iterator = prefixes.iterator();
            while (iterator.hasNext()) {
                this.endPrefixMapping((String)iterator.next());
            }
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        int pos;
        if (this.prefixToUriMap.containsKey(prefix)) {
            this.uriToPrefixMap.remove(this.prefixToUriMap.get(prefix));
            this.prefixToUriMap.remove(prefix);
        }
        if (this.hasMappings && (pos = this.prefixList.lastIndexOf(prefix)) != -1) {
            this.prefixList.remove(pos);
            this.uriList.remove(pos);
        }
        super.endPrefixMapping(prefix);
    }

    @Override
    public void endDocument() throws SAXException {
        this.uriToPrefixMap.clear();
        this.prefixToUriMap.clear();
        this.clearMappings();
        super.endDocument();
    }

    private void clearMappings() {
        this.hasMappings = false;
        this.prefixList.clear();
        this.uriList.clear();
    }
}

