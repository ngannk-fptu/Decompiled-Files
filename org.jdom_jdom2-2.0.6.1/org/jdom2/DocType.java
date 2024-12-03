/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.IllegalDataException;
import org.jdom2.IllegalNameException;
import org.jdom2.Parent;
import org.jdom2.Verifier;
import org.jdom2.output.XMLOutputter;

public class DocType
extends Content {
    private static final long serialVersionUID = 200L;
    protected String elementName;
    protected String publicID;
    protected String systemID;
    protected String internalSubset;

    protected DocType() {
        super(Content.CType.DocType);
    }

    public DocType(String elementName, String publicID, String systemID) {
        super(Content.CType.DocType);
        this.setElementName(elementName);
        this.setPublicID(publicID);
        this.setSystemID(systemID);
    }

    public DocType(String elementName, String systemID) {
        this(elementName, null, systemID);
    }

    public DocType(String elementName) {
        this(elementName, null, null);
    }

    public String getElementName() {
        return this.elementName;
    }

    public DocType setElementName(String elementName) {
        String reason = Verifier.checkXMLName(elementName);
        if (reason != null) {
            throw new IllegalNameException(elementName, "DocType", reason);
        }
        this.elementName = elementName;
        return this;
    }

    public String getPublicID() {
        return this.publicID;
    }

    public DocType setPublicID(String publicID) {
        String reason = Verifier.checkPublicID(publicID);
        if (reason != null) {
            throw new IllegalDataException(publicID, "DocType", reason);
        }
        this.publicID = publicID;
        return this;
    }

    public String getSystemID() {
        return this.systemID;
    }

    public DocType setSystemID(String systemID) {
        String reason = Verifier.checkSystemLiteral(systemID);
        if (reason != null) {
            throw new IllegalDataException(systemID, "DocType", reason);
        }
        this.systemID = systemID;
        return this;
    }

    public String getValue() {
        return "";
    }

    public void setInternalSubset(String newData) {
        this.internalSubset = newData;
    }

    public String getInternalSubset() {
        return this.internalSubset;
    }

    public String toString() {
        return "[DocType: " + new XMLOutputter().outputString(this) + "]";
    }

    public DocType clone() {
        return (DocType)super.clone();
    }

    public DocType detach() {
        return (DocType)super.detach();
    }

    protected DocType setParent(Parent parent) {
        return (DocType)super.setParent(parent);
    }

    public Document getParent() {
        return (Document)super.getParent();
    }
}

