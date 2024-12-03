/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecProcessingInstruction;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecProcessingInstructionImpl
extends XMLSecEventBaseImpl
implements XMLSecProcessingInstruction {
    private final String data;
    private final String target;

    public XMLSecProcessingInstructionImpl(String target, String data, XMLSecStartElement parentXmlSecStartElement) {
        this.target = target;
        this.data = data;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public int getEventType() {
        return 3;
    }

    @Override
    public boolean isProcessingInstruction() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(this.getTarget());
            String data = this.getData();
            if (data != null && !data.isEmpty()) {
                writer.write(32);
                writer.write(data);
            }
            writer.write("?>");
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

