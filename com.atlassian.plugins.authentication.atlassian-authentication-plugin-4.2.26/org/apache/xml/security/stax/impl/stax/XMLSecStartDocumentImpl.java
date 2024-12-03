/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecStartDocument;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;

public class XMLSecStartDocumentImpl
extends XMLSecEventBaseImpl
implements XMLSecStartDocument {
    private final String systemId;
    private final String characterEncodingScheme;
    private final Boolean isStandAlone;
    private final String version;

    public XMLSecStartDocumentImpl(String systemId, String characterEncodingScheme, Boolean standAlone, String version) {
        this.systemId = systemId;
        this.characterEncodingScheme = characterEncodingScheme;
        this.isStandAlone = standAlone;
        this.version = version != null ? version : "1.0";
    }

    @Override
    public int getEventType() {
        return 7;
    }

    @Override
    public String getSystemId() {
        return this.systemId != null ? this.systemId : "";
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this.characterEncodingScheme != null ? this.characterEncodingScheme : StandardCharsets.UTF_8.name();
    }

    @Override
    public boolean encodingSet() {
        return this.characterEncodingScheme != null;
    }

    @Override
    public boolean isStandalone() {
        return this.isStandAlone != null && this.isStandAlone != false;
    }

    @Override
    public boolean standaloneSet() {
        return this.isStandAlone != null;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean isStartDocument() {
        return true;
    }

    @Override
    public XMLSecStartDocument asStartDocument() {
        return this;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?xml version=\"");
            if (this.getVersion() == null || this.getVersion().isEmpty()) {
                writer.write("1.0");
            } else {
                writer.write(this.getVersion());
            }
            writer.write(34);
            if (this.encodingSet()) {
                writer.write(" encoding=\"");
                writer.write(this.getCharacterEncodingScheme());
                writer.write(34);
            }
            if (this.standaloneSet()) {
                if (this.isStandalone()) {
                    writer.write(" standalone=\"yes\"");
                } else {
                    writer.write(" standalone=\"no\"");
                }
            }
            writer.write(" ?>");
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

