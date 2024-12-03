/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecEntityDeclaration;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecEntityDeclarationImpl
extends XMLSecEventBaseImpl
implements XMLSecEntityDeclaration {
    private String name;

    public XMLSecEntityDeclarationImpl(String name) {
        this.name = name;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getNotationName() {
        return null;
    }

    @Override
    public String getReplacementText() {
        return null;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public int getEventType() {
        return 15;
    }

    @Override
    public boolean isEntityReference() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(this.getName());
            writer.write(" \"");
            String replacementText = this.getReplacementText();
            if (replacementText != null) {
                writer.write(replacementText);
            }
            writer.write("\">");
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

