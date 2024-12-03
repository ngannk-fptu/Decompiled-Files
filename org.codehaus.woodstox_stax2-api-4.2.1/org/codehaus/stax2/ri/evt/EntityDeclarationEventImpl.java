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

    public EntityDeclarationEventImpl(Location loc, String name) {
        super(loc);
        this.mName = name;
    }

    @Override
    public String getBaseURI() {
        return "";
    }

    @Override
    public String getName() {
        return this.mName;
    }

    @Override
    public String getNotationName() {
        return null;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getReplacementText() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }

    @Override
    public int getEventType() {
        return 15;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<!ENTITY ");
            w.write(this.getName());
            w.write(" \"");
            String content = this.getReplacementText();
            if (content != null) {
                w.write(content);
            }
            w.write("\">");
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        StringWriter strw = new StringWriter();
        this.writeAsEncodedUnicode(strw);
        w.writeRaw(strw.toString());
    }

    @Override
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
        return EntityDeclarationEventImpl.stringsWithNullsEqual(this.getName(), other.getName()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getBaseURI(), other.getBaseURI()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getNotationName(), other.getNotationName()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getPublicId(), other.getPublicId()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getReplacementText(), other.getReplacementText()) && EntityDeclarationEventImpl.stringsWithNullsEqual(this.getSystemId(), other.getSystemId());
    }

    @Override
    public int hashCode() {
        return this.mName.hashCode();
    }
}

