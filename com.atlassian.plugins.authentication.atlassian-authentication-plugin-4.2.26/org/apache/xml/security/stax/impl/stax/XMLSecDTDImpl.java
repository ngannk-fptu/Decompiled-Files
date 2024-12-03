/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecDTD;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecDTDImpl
extends XMLSecEventBaseImpl
implements XMLSecDTD {
    private final String dtd;

    public XMLSecDTDImpl(String dtd, XMLSecStartElement parentXmlSecStartElement) {
        this.dtd = dtd;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public String getDocumentTypeDeclaration() {
        return this.dtd;
    }

    @Override
    public Object getProcessedDTD() {
        return null;
    }

    public List getNotations() {
        return Collections.emptyList();
    }

    public List getEntities() {
        return Collections.emptyList();
    }

    @Override
    public int getEventType() {
        return 11;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(this.getDocumentTypeDeclaration());
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

