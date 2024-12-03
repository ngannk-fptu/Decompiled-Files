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

    public EntityReferenceEventImpl(Location location, EntityDeclaration entityDeclaration) {
        super(location);
        this.mDecl = entityDeclaration;
    }

    public EntityReferenceEventImpl(Location location, String string) {
        super(location);
        this.mDecl = new EntityDeclarationEventImpl(location, string);
    }

    public EntityDeclaration getDeclaration() {
        return this.mDecl;
    }

    public String getName() {
        return this.mDecl.getName();
    }

    public int getEventType() {
        return 9;
    }

    public boolean isEntityReference() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(this.getName());
            writer.write(59);
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        xMLStreamWriter2.writeEntityRef(this.getName());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof EntityReference)) {
            return false;
        }
        EntityReference entityReference = (EntityReference)object;
        return this.getName().equals(entityReference.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

