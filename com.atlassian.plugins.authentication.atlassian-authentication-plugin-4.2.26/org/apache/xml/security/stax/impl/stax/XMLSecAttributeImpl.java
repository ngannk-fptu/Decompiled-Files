/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecNamespaceImpl;

public class XMLSecAttributeImpl
extends XMLSecEventBaseImpl
implements XMLSecAttribute {
    private final QName name;
    private final String value;
    private XMLSecNamespace attributeNamespace;

    public XMLSecAttributeImpl(QName name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(XMLSecAttribute o) {
        int namespacePartCompare = this.name.getNamespaceURI().compareTo(o.getName().getNamespaceURI());
        if (namespacePartCompare != 0) {
            return namespacePartCompare;
        }
        return this.name.getLocalPart().compareTo(o.getName().getLocalPart());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLSecAttribute)) {
            return false;
        }
        XMLSecAttribute comparableAttribute = (XMLSecAttribute)obj;
        if (comparableAttribute.hashCode() != this.hashCode()) {
            return false;
        }
        return comparableAttribute.getName().getLocalPart().equals(this.name.getLocalPart());
    }

    public int hashCode() {
        return this.name.getLocalPart().hashCode();
    }

    @Override
    public XMLSecNamespace getAttributeNamespace() {
        if (this.attributeNamespace == null) {
            this.attributeNamespace = XMLSecNamespaceImpl.getInstance(this.name.getPrefix(), this.name.getNamespaceURI());
        }
        return this.attributeNamespace;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
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
    public int getEventType() {
        return 10;
    }

    @Override
    public boolean isAttribute() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            String prefix = this.getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.getName().getLocalPart());
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
}

