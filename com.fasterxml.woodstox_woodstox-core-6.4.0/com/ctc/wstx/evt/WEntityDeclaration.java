/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.XMLStreamWriter2
 *  org.codehaus.stax2.ri.evt.BaseEventImpl
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

    @Override
    public abstract String getBaseURI();

    @Override
    public abstract String getName();

    @Override
    public abstract String getNotationName();

    @Override
    public abstract String getPublicId();

    @Override
    public abstract String getReplacementText();

    @Override
    public abstract String getSystemId();

    @Override
    public int getEventType() {
        return 15;
    }

    public abstract void writeEnc(Writer var1) throws IOException;

    @Override
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
        return WEntityDeclaration.stringsWithNullsEqual((String)this.getName(), (String)other.getName()) && WEntityDeclaration.stringsWithNullsEqual((String)this.getBaseURI(), (String)other.getBaseURI()) && WEntityDeclaration.stringsWithNullsEqual((String)this.getNotationName(), (String)other.getNotationName()) && WEntityDeclaration.stringsWithNullsEqual((String)this.getPublicId(), (String)other.getPublicId()) && WEntityDeclaration.stringsWithNullsEqual((String)this.getReplacementText(), (String)other.getReplacementText()) && WEntityDeclaration.stringsWithNullsEqual((String)this.getSystemId(), (String)other.getSystemId());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

