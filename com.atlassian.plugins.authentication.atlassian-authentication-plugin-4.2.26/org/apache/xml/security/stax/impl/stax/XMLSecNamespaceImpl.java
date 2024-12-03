/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public final class XMLSecNamespaceImpl
extends XMLSecEventBaseImpl
implements XMLSecNamespace {
    private static final Map<String, Map<String, XMLSecNamespace>> XMLSEC_NS_MAP = Collections.synchronizedMap(new WeakHashMap());
    private String prefix;
    private final String uri;
    private QName qName;

    private XMLSecNamespaceImpl(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public static XMLSecNamespace getInstance(String prefix, String uri) {
        Map<String, XMLSecNamespace> nsMap;
        String uriToUse;
        String prefixToUse = prefix;
        if (prefixToUse == null) {
            prefixToUse = "";
        }
        if ((uriToUse = uri) == null) {
            uriToUse = "";
        }
        if ((nsMap = XMLSEC_NS_MAP.get(prefixToUse)) != null) {
            XMLSecNamespace xmlSecNamespace = nsMap.get(uriToUse);
            if (xmlSecNamespace != null) {
                return xmlSecNamespace;
            }
            xmlSecNamespace = new XMLSecNamespaceImpl(prefixToUse, uriToUse);
            nsMap.put(uriToUse, xmlSecNamespace);
            return xmlSecNamespace;
        }
        nsMap = new WeakHashMap<String, XMLSecNamespace>();
        XMLSecNamespaceImpl xmlSecNamespace = new XMLSecNamespaceImpl(prefixToUse, uriToUse);
        nsMap.put(uriToUse, xmlSecNamespace);
        XMLSEC_NS_MAP.put(prefixToUse, nsMap);
        return xmlSecNamespace;
    }

    @Override
    public int compareTo(XMLSecNamespace o) {
        return this.prefix.compareTo(o.getPrefix());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecNamespace)) {
            return false;
        }
        XMLSecNamespace comparableNamespace = (XMLSecNamespace)obj;
        if (comparableNamespace.hashCode() != this.hashCode()) {
            return false;
        }
        return comparableNamespace.getPrefix().equals(this.prefix);
    }

    public int hashCode() {
        return this.prefix.hashCode();
    }

    @Override
    public QName getName() {
        if (this.qName == null) {
            this.qName = new QName("http://www.w3.org/2000/xmlns/", this.prefix);
        }
        return this.qName;
    }

    @Override
    public String getValue() {
        return this.uri;
    }

    @Override
    public String getDTDType() {
        return "CDATA";
    }

    @Override
    public boolean isSpecified() {
        return true;
    }

    @Override
    public String getNamespaceURI() {
        return this.uri;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return this.prefix.length() == 0;
    }

    @Override
    public int getEventType() {
        return 13;
    }

    @Override
    public boolean isNamespace() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("xmlns");
            if (this.getPrefix() != null && !this.getPrefix().isEmpty()) {
                writer.write(58);
                writer.write(this.getPrefix());
            }
            writer.write("=\"");
            this.writeEncoded(writer, this.getValue());
            writer.write("\"");
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private void writeEncoded(Writer writer, String text) throws IOException {
        int length = text.length();
        int idx = 0;
        for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            if (c == '&') {
                writer.write(text, idx, i - idx);
                writer.write("&amp;");
                idx = i + 1;
                continue;
            }
            if (c != '\"') continue;
            writer.write(text, idx, i - idx);
            writer.write("&quot;");
            idx = i + 1;
        }
        writer.write(text, idx, length - idx);
    }

    public String toString() {
        if (this.prefix == null || this.prefix.isEmpty()) {
            return "xmlns=\"" + this.uri + "\"";
        }
        return "xmlns:" + this.prefix + "=\"" + this.uri + "\"";
    }
}

