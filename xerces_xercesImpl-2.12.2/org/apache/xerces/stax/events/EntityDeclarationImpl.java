/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class EntityDeclarationImpl
extends XMLEventImpl
implements EntityDeclaration {
    private final String fPublicId;
    private final String fSystemId;
    private final String fName;
    private final String fNotationName;

    public EntityDeclarationImpl(String string, String string2, String string3, String string4, Location location) {
        super(15, location);
        this.fPublicId = string;
        this.fSystemId = string2;
        this.fName = string3;
        this.fNotationName = string4;
    }

    @Override
    public String getPublicId() {
        return this.fPublicId;
    }

    @Override
    public String getSystemId() {
        return this.fSystemId;
    }

    @Override
    public String getName() {
        return this.fName;
    }

    @Override
    public String getNotationName() {
        return this.fNotationName;
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
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(this.fName);
            if (this.fPublicId != null) {
                writer.write(" PUBLIC \"");
                writer.write(this.fPublicId);
                writer.write("\" \"");
                writer.write(this.fSystemId);
                writer.write(34);
            } else {
                writer.write(" SYSTEM \"");
                writer.write(this.fSystemId);
                writer.write(34);
            }
            if (this.fNotationName != null) {
                writer.write(" NDATA ");
                writer.write(this.fNotationName);
            }
            writer.write(62);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

