/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.encoding;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class TagInfoset {
    @NotNull
    public final String[] ns;
    @NotNull
    public final AttributesImpl atts;
    @Nullable
    public final String prefix;
    @Nullable
    public final String nsUri;
    @NotNull
    public final String localName;
    @Nullable
    private String qname;
    private static final String[] EMPTY_ARRAY = new String[0];
    private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();

    public TagInfoset(String nsUri, String localName, String prefix, AttributesImpl atts, String ... ns) {
        this.nsUri = nsUri;
        this.prefix = prefix;
        this.localName = localName;
        this.atts = atts;
        this.ns = ns;
    }

    public TagInfoset(XMLStreamReader reader) {
        int ac;
        this.prefix = reader.getPrefix();
        this.nsUri = reader.getNamespaceURI();
        this.localName = reader.getLocalName();
        int nsc = reader.getNamespaceCount();
        if (nsc > 0) {
            this.ns = new String[nsc * 2];
            for (int i = 0; i < nsc; ++i) {
                this.ns[i * 2] = TagInfoset.fixNull(reader.getNamespacePrefix(i));
                this.ns[i * 2 + 1] = TagInfoset.fixNull(reader.getNamespaceURI(i));
            }
        } else {
            this.ns = EMPTY_ARRAY;
        }
        if ((ac = reader.getAttributeCount()) > 0) {
            this.atts = new AttributesImpl();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ac; ++i) {
                String qname;
                sb.setLength(0);
                String prefix = reader.getAttributePrefix(i);
                String localName = reader.getAttributeLocalName(i);
                if (prefix != null && prefix.length() != 0) {
                    sb.append(prefix);
                    sb.append(":");
                    sb.append(localName);
                    qname = sb.toString();
                } else {
                    qname = localName;
                }
                this.atts.addAttribute(TagInfoset.fixNull(reader.getAttributeNamespace(i)), localName, qname, reader.getAttributeType(i), reader.getAttributeValue(i));
            }
        } else {
            this.atts = EMPTY_ATTRIBUTES;
        }
    }

    public void writeStart(ContentHandler contentHandler) throws SAXException {
        for (int i = 0; i < this.ns.length; i += 2) {
            contentHandler.startPrefixMapping(TagInfoset.fixNull(this.ns[i]), TagInfoset.fixNull(this.ns[i + 1]));
        }
        contentHandler.startElement(TagInfoset.fixNull(this.nsUri), this.localName, this.getQName(), this.atts);
    }

    public void writeEnd(ContentHandler contentHandler) throws SAXException {
        contentHandler.endElement(TagInfoset.fixNull(this.nsUri), this.localName, this.getQName());
        for (int i = this.ns.length - 2; i >= 0; i -= 2) {
            contentHandler.endPrefixMapping(TagInfoset.fixNull(this.ns[i]));
        }
    }

    public void writeStart(XMLStreamWriter w) throws XMLStreamException {
        int i;
        if (this.prefix == null) {
            if (this.nsUri == null) {
                w.writeStartElement(this.localName);
            } else {
                w.writeStartElement("", this.localName, this.nsUri);
            }
        } else {
            w.writeStartElement(this.prefix, this.localName, this.nsUri);
        }
        for (i = 0; i < this.ns.length; i += 2) {
            w.writeNamespace(this.ns[i], this.ns[i + 1]);
        }
        for (i = 0; i < this.atts.getLength(); ++i) {
            String nsUri = this.atts.getURI(i);
            if (nsUri == null || nsUri.length() == 0) {
                w.writeAttribute(this.atts.getLocalName(i), this.atts.getValue(i));
                continue;
            }
            String rawName = this.atts.getQName(i);
            String prefix = rawName.substring(0, rawName.indexOf(58));
            w.writeAttribute(prefix, nsUri, this.atts.getLocalName(i), this.atts.getValue(i));
        }
    }

    private String getQName() {
        if (this.qname != null) {
            return this.qname;
        }
        StringBuilder sb = new StringBuilder();
        if (this.prefix != null) {
            sb.append(this.prefix);
            sb.append(':');
            sb.append(this.localName);
            this.qname = sb.toString();
        } else {
            this.qname = this.localName;
        }
        return this.qname;
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public String getNamespaceURI(String prefix) {
        int size = this.ns.length / 2;
        for (int i = 0; i < size; ++i) {
            String p = this.ns[i * 2];
            String n = this.ns[i * 2 + 1];
            if (!prefix.equals(p)) continue;
            return n;
        }
        return null;
    }

    public String getPrefix(String namespaceURI) {
        int size = this.ns.length / 2;
        for (int i = 0; i < size; ++i) {
            String p = this.ns[i * 2];
            String n = this.ns[i * 2 + 1];
            if (!namespaceURI.equals(n)) continue;
            return p;
        }
        return null;
    }

    public List<String> allPrefixes(String namespaceURI) {
        int size = this.ns.length / 2;
        ArrayList<String> l = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            String p = this.ns[i * 2];
            String n = this.ns[i * 2 + 1];
            if (!namespaceURI.equals(n)) continue;
            l.add(p);
        }
        return l;
    }
}

