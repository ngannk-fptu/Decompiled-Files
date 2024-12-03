/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class NotationDeclarationImpl
extends XMLEventImpl
implements NotationDeclaration {
    private final String fSystemId;
    private final String fPublicId;
    private final String fName;

    public NotationDeclarationImpl(String string, String string2, String string3, Location location) {
        super(14, location);
        this.fName = string;
        this.fPublicId = string2;
        this.fSystemId = string3;
    }

    @Override
    public String getName() {
        return this.fName;
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
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!NOTATION ");
            if (this.fPublicId != null) {
                writer.write("PUBLIC \"");
                writer.write(this.fPublicId);
                writer.write(34);
                if (this.fSystemId != null) {
                    writer.write(" \"");
                    writer.write(this.fSystemId);
                    writer.write(34);
                }
            } else {
                writer.write("SYSTEM \"");
                writer.write(this.fSystemId);
                writer.write(34);
            }
            writer.write(this.fName);
            writer.write(62);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

