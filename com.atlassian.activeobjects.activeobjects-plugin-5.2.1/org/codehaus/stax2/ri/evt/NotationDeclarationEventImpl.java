/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.NotationDeclaration2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class NotationDeclarationEventImpl
extends BaseEventImpl
implements NotationDeclaration2 {
    final String mName;
    final String mPublicId;
    final String mSystemId;

    public NotationDeclarationEventImpl(Location location, String string, String string2, String string3) {
        super(location);
        this.mName = string;
        this.mPublicId = string2;
        this.mSystemId = string3;
    }

    public String getName() {
        return this.mName;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public String getBaseURI() {
        return "";
    }

    public int getEventType() {
        return 14;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!NOTATION ");
            writer.write(this.mName);
            if (this.mPublicId != null) {
                writer.write("PUBLIC \"");
                writer.write(this.mPublicId);
                writer.write(34);
            } else {
                writer.write("SYSTEM");
            }
            if (this.mSystemId != null) {
                writer.write(" \"");
                writer.write(this.mSystemId);
                writer.write(34);
            }
            writer.write(62);
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        throw new XMLStreamException("Can not write notation declarations using an XMLStreamWriter");
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof NotationDeclaration2)) {
            return false;
        }
        NotationDeclaration2 notationDeclaration2 = (NotationDeclaration2)object;
        return NotationDeclarationEventImpl.stringsWithNullsEqual(this.getName(), notationDeclaration2.getName()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getPublicId(), notationDeclaration2.getPublicId()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getSystemId(), notationDeclaration2.getSystemId()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getBaseURI(), notationDeclaration2.getBaseURI());
    }

    public int hashCode() {
        int n = 0;
        if (this.mName != null) {
            n ^= this.mName.hashCode();
        }
        if (this.mPublicId != null) {
            n ^= this.mPublicId.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        return n;
    }
}

