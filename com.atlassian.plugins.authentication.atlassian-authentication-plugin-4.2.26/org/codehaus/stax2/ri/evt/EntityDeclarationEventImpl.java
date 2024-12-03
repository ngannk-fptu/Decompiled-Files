/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class EntityDeclarationEventImpl
extends BaseEventImpl
implements EntityDeclaration {
    protected final String mName;

    public EntityDeclarationEventImpl(Location location, String string) {
        super(location);
        this.mName = string;
    }

    public String getBaseURI() {
        return "";
    }

    public String getName() {
        return this.mName;
    }

    public String getNotationName() {
        return null;
    }

    public String getPublicId() {
        return null;
    }

    public String getReplacementText() {
        return null;
    }

    public String getSystemId() {
        return null;
    }

    public int getEventType() {
        return 15;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(this.getName());
            writer.write(" \"");
            String string = this.getReplacementText();
            if (string != null) {
                writer.write(string);
            }
            writer.write("\">");
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        StringWriter stringWriter = new StringWriter();
        this.writeAsEncodedUnicode(stringWriter);
        xMLStreamWriter2.writeRaw(stringWriter.toString());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof EntityDeclaration)) {
            return false;
        }
        EntityDeclaration entityDeclaration = (EntityDeclaration)object;
        return EntityDeclarationEventImpl.stringsWithNullsEqual(this.getName(), entityDeclaration.getName()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getBaseURI(), entityDeclaration.getBaseURI()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getNotationName(), entityDeclaration.getNotationName()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getPublicId(), entityDeclaration.getPublicId()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getReplacementText(), entityDeclaration.getReplacementText()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getSystemId(), entityDeclaration.getSystemId());
    }

    public int hashCode() {
        return this.mName.hashCode();
    }
}

