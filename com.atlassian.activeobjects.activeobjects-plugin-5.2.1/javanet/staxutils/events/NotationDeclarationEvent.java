/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.NotationDeclaration;

public class NotationDeclarationEvent
extends AbstractXMLEvent
implements NotationDeclaration {
    protected String name;
    protected String publicId;
    protected String systemId;

    public NotationDeclarationEvent(String name, String publicId, Location location) {
        super(location);
        this.name = name;
        this.publicId = publicId;
    }

    public NotationDeclarationEvent(String name, String publicId, String systemId, Location location) {
        super(location);
        this.name = name;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public NotationDeclarationEvent(NotationDeclaration that) {
        super(that);
        this.name = that.getName();
        this.publicId = that.getPublicId();
        this.systemId = that.getSystemId();
    }

    public int getEventType() {
        return 14;
    }

    public String getName() {
        return this.name;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }
}

