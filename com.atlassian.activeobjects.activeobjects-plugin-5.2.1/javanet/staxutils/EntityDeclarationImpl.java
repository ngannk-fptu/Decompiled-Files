/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.IOException;
import java.io.Writer;
import javanet.staxutils.EventHelper;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;

class EntityDeclarationImpl
extends EventHelper
implements EntityDeclaration {
    private final String entityName;
    private final String publicId;
    private final String systemId;
    private final String notationName;
    private final String replacementText;

    public EntityDeclarationImpl(Location location, String entityName, String publicId, String systemId, String notationName, String replacementText) {
        super(location);
        this.entityName = entityName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.notationName = notationName;
        this.replacementText = replacementText;
    }

    public String getBaseURI() {
        return null;
    }

    public String getName() {
        return this.entityName;
    }

    public String getNotationName() {
        return this.notationName;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getReplacementText() {
        return this.replacementText;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getEventType() {
        return 15;
    }

    public boolean isEntityReference() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write(38);
            w.write(this.entityName);
            w.write(59);
        }
        catch (IOException ie) {
            throw new XMLStreamException(ie);
        }
    }
}

