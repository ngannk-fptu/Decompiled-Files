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

    public NotationDeclarationEventImpl(Location loc, String name, String pubId, String sysId) {
        super(loc);
        this.mName = name;
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    @Override
    public String getName() {
        return this.mName;
    }

    @Override
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override
    public String getBaseURI() {
        return "";
    }

    @Override
    public int getEventType() {
        return 14;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!NOTATION ");
            w.write(this.mName);
            if (this.mPublicId != null) {
                w.write("PUBLIC \"");
                w.write(this.mPublicId);
                w.write(34);
            } else {
                w.write("SYSTEM");
            }
            if (this.mSystemId != null) {
                w.write(" \"");
                w.write(this.mSystemId);
                w.write(34);
            }
            w.write(62);
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        throw new XMLStreamException("Can not write notation declarations using an XMLStreamWriter");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof NotationDeclaration2)) {
            return false;
        }
        NotationDeclaration2 other = (NotationDeclaration2)o;
        return NotationDeclarationEventImpl.stringsWithNullsEqual(this.getName(), other.getName()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getPublicId(), other.getPublicId()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getSystemId(), other.getSystemId()) && NotationDeclarationEventImpl.stringsWithNullsEqual(this.getBaseURI(), other.getBaseURI());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.mName != null) {
            hash ^= this.mName.hashCode();
        }
        if (this.mPublicId != null) {
            hash ^= this.mPublicId.hashCode();
        }
        if (this.mSystemId != null) {
            hash ^= this.mSystemId.hashCode();
        }
        return hash;
    }
}

