/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public abstract class WEntityDeclaration
extends BaseEventImpl
implements EntityDeclaration {
    public WEntityDeclaration(Location loc) {
        super(loc);
    }

    public abstract String getBaseURI();

    public abstract String getName();

    public abstract String getNotationName();

    public abstract String getPublicId();

    public abstract String getReplacementText();

    public abstract String getSystemId();

    public int getEventType() {
        return 15;
    }

    public abstract void writeEnc(Writer var1) throws IOException;

    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            this.writeEnc(w);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        throw new XMLStreamException("Can not write entity declarations using an XMLStreamWriter");
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof EntityDeclaration)) {
            return false;
        }
        EntityDeclaration other = (EntityDeclaration)o;
        return WEntityDeclaration.stringsWithNullsEqual(this.getName(), other.getName()) && WEntityDeclaration.stringsWithNullsEqual(this.getBaseURI(), other.getBaseURI()) && WEntityDeclaration.stringsWithNullsEqual(this.getNotationName(), other.getNotationName()) && WEntityDeclaration.stringsWithNullsEqual(this.getPublicId(), other.getPublicId()) && WEntityDeclaration.stringsWithNullsEqual(this.getReplacementText(), other.getReplacementText()) && WEntityDeclaration.stringsWithNullsEqual(this.getSystemId(), other.getSystemId());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

