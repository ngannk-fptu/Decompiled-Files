/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.IllegalNameException;
import org.jdom2.Parent;
import org.jdom2.Verifier;

public class EntityRef
extends Content {
    private static final long serialVersionUID = 200L;
    protected String name;
    protected String publicID;
    protected String systemID;

    protected EntityRef() {
        super(Content.CType.EntityRef);
    }

    public EntityRef(String name) {
        this(name, null, null);
    }

    public EntityRef(String name, String systemID) {
        this(name, null, systemID);
    }

    public EntityRef(String name, String publicID, String systemID) {
        super(Content.CType.EntityRef);
        this.setName(name);
        this.setPublicID(publicID);
        this.setSystemID(systemID);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return "";
    }

    public String getPublicID() {
        return this.publicID;
    }

    public String getSystemID() {
        return this.systemID;
    }

    public EntityRef setName(String name) {
        String reason = Verifier.checkXMLName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "EntityRef", reason);
        }
        this.name = name;
        return this;
    }

    public EntityRef setPublicID(String publicID) {
        String reason = Verifier.checkPublicID(publicID);
        if (reason != null) {
            throw new IllegalDataException(publicID, "EntityRef", reason);
        }
        this.publicID = publicID;
        return this;
    }

    public EntityRef setSystemID(String systemID) {
        String reason = Verifier.checkSystemLiteral(systemID);
        if (reason != null) {
            throw new IllegalDataException(systemID, "EntityRef", reason);
        }
        this.systemID = systemID;
        return this;
    }

    public String toString() {
        return "[EntityRef: " + "&" + this.name + ";" + "]";
    }

    public EntityRef detach() {
        return (EntityRef)super.detach();
    }

    protected EntityRef setParent(Parent parent) {
        return (EntityRef)super.setParent(parent);
    }

    public Element getParent() {
        return (Element)super.getParent();
    }

    public EntityRef clone() {
        return (EntityRef)super.clone();
    }
}

