/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class EntityReferenceImpl
extends XMLEventImpl
implements EntityReference {
    private final String fName;
    private final EntityDeclaration fDecl;

    public EntityReferenceImpl(EntityDeclaration entityDeclaration, Location location) {
        this(entityDeclaration != null ? entityDeclaration.getName() : "", entityDeclaration, location);
    }

    public EntityReferenceImpl(String string, EntityDeclaration entityDeclaration, Location location) {
        super(9, location);
        this.fName = string != null ? string : "";
        this.fDecl = entityDeclaration;
    }

    @Override
    public EntityDeclaration getDeclaration() {
        return this.fDecl;
    }

    @Override
    public String getName() {
        return this.fName;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(this.fName);
            writer.write(59);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

