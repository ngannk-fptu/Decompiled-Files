/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;
import org.codehaus.stax2.ri.evt.EntityDeclarationEventImpl;

public class EntityReferenceEventImpl
extends BaseEventImpl
implements EntityReference {
    protected final EntityDeclaration mDecl;

    public EntityReferenceEventImpl(Location loc, EntityDeclaration decl) {
        super(loc);
        this.mDecl = decl;
    }

    public EntityReferenceEventImpl(Location loc, String name) {
        super(loc);
        this.mDecl = new EntityDeclarationEventImpl(loc, name);
    }

    @Override
    public EntityDeclaration getDeclaration() {
        return this.mDecl;
    }

    @Override
    public String getName() {
        return this.mDecl.getName();
    }

    @Override
    public int getEventType() {
        return 9;
    }

    @Override
    public boolean isEntityReference() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write(38);
            w.write(this.getName());
            w.write(59);
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeEntityRef(this.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof EntityReference)) {
            return false;
        }
        EntityReference other = (EntityReference)o;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}

