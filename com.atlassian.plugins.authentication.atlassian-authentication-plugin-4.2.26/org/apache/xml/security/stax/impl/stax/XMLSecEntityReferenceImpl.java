/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityReference;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecEntityReferenceImpl
extends XMLSecEventBaseImpl
implements XMLSecEntityReference {
    private final String name;
    private final EntityDeclaration entityDeclaration;

    public XMLSecEntityReferenceImpl(String name, EntityDeclaration entityDeclaration, XMLSecStartElement parentXmlSecStartElement) {
        this.name = name;
        this.entityDeclaration = entityDeclaration;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public EntityDeclaration getDeclaration() {
        return this.entityDeclaration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getEventType() {
        return 9;
    }

    @Override
    public boolean isEntityReference() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(this.getName());
            writer.write(59);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

