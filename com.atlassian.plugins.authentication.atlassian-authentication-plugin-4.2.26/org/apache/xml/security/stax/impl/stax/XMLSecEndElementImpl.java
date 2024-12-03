/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecEndElementImpl
extends XMLSecEventBaseImpl
implements XMLSecEndElement {
    private final QName elementName;

    public XMLSecEndElementImpl(QName elementName, XMLSecStartElement parentXmlSecStartElement) {
        this.elementName = elementName;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public QName getName() {
        return this.elementName;
    }

    public Iterator getNamespaces() {
        return XMLSecEndElementImpl.getEmptyIterator();
    }

    @Override
    public int getEventType() {
        return 2;
    }

    @Override
    public boolean isEndElement() {
        return true;
    }

    @Override
    public XMLSecEndElement asEndElement() {
        return this;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            String prefix = this.getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(this.getName().getPrefix());
                writer.write(58);
            }
            writer.write(this.getName().getLocalPart());
            writer.write(62);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

