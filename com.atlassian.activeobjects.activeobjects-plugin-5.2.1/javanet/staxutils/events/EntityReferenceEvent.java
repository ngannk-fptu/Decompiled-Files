/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent
extends AbstractXMLEvent
implements EntityReference {
    protected String name;
    protected EntityDeclaration declaration;

    public EntityReferenceEvent(String name, EntityDeclaration declaration) {
        this.name = name;
        this.declaration = declaration;
    }

    public EntityReferenceEvent(String name, EntityDeclaration declaration, Location location) {
        super(location);
        this.name = name;
        this.declaration = declaration;
    }

    public EntityReferenceEvent(EntityReference that) {
        super(that);
        this.name = that.getName();
        this.declaration = that.getDeclaration();
    }

    public EntityDeclaration getDeclaration() {
        return this.declaration;
    }

    public String getName() {
        return this.name;
    }

    public int getEventType() {
        return 9;
    }
}

