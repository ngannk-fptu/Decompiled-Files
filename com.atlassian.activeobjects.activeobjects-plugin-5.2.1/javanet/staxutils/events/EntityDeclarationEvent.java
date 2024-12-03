/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationEvent
extends AbstractXMLEvent
implements EntityDeclaration {
    protected String name;
    protected String replacementText;
    protected String baseURI;
    protected String publicId;
    protected String systemId;
    protected String notationName;

    public EntityDeclarationEvent(String name, String replacementText, Location location) {
        super(location);
        this.name = name;
        this.replacementText = replacementText;
    }

    public EntityDeclarationEvent(String name, String replacementText, String notationName, Location location) {
        super(location);
        this.name = name;
        this.replacementText = replacementText;
        this.notationName = notationName;
    }

    public EntityDeclarationEvent(String name, String publicId, String systemId, String baseURI, String notationName, Location location) {
        super(location);
        this.name = name;
        this.publicId = publicId;
        this.systemId = systemId;
        this.baseURI = baseURI;
        this.notationName = notationName;
    }

    public EntityDeclarationEvent(EntityDeclaration that) {
        super(that);
        this.name = that.getName();
        this.replacementText = that.getReplacementText();
        this.publicId = that.getPublicId();
        this.systemId = that.getSystemId();
        this.baseURI = that.getBaseURI();
        this.notationName = that.getNotationName();
    }

    public int getEventType() {
        return 15;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    public String getName() {
        return this.name;
    }

    public String getNotationName() {
        return this.notationName;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getReplacementText() {
        return this.replacementText;
    }

    public String getSystemId() {
        return this.systemId;
    }
}

