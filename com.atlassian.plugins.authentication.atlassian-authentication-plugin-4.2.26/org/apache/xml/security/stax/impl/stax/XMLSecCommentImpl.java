/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecComment;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecCommentImpl
extends XMLSecEventBaseImpl
implements XMLSecComment {
    private final String text;

    public XMLSecCommentImpl(String text, XMLSecStartElement parentXmlSecStartElement) {
        this.text = text;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public int getEventType() {
        return 5;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(this.getText());
            writer.write("-->");
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

