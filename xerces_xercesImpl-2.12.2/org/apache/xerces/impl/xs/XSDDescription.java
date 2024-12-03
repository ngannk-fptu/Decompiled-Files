/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.grammars.XMLSchemaDescription;

public class XSDDescription
extends XMLResourceIdentifierImpl
implements XMLSchemaDescription {
    public static final short CONTEXT_INITIALIZE = -1;
    public static final short CONTEXT_INCLUDE = 0;
    public static final short CONTEXT_REDEFINE = 1;
    public static final short CONTEXT_IMPORT = 2;
    public static final short CONTEXT_PREPARSE = 3;
    public static final short CONTEXT_INSTANCE = 4;
    public static final short CONTEXT_ELEMENT = 5;
    public static final short CONTEXT_ATTRIBUTE = 6;
    public static final short CONTEXT_XSITYPE = 7;
    protected short fContextType;
    protected String[] fLocationHints;
    protected QName fTriggeringComponent;
    protected QName fEnclosedElementName;
    protected XMLAttributes fAttributes;

    @Override
    public String getGrammarType() {
        return "http://www.w3.org/2001/XMLSchema";
    }

    @Override
    public short getContextType() {
        return this.fContextType;
    }

    @Override
    public String getTargetNamespace() {
        return this.fNamespace;
    }

    @Override
    public String[] getLocationHints() {
        return this.fLocationHints;
    }

    @Override
    public QName getTriggeringComponent() {
        return this.fTriggeringComponent;
    }

    @Override
    public QName getEnclosingElementName() {
        return this.fEnclosedElementName;
    }

    @Override
    public XMLAttributes getAttributes() {
        return this.fAttributes;
    }

    public boolean fromInstance() {
        return this.fContextType == 6 || this.fContextType == 5 || this.fContextType == 4 || this.fContextType == 7;
    }

    public boolean equals(Object object) {
        if (!(object instanceof XMLSchemaDescription)) {
            return false;
        }
        XMLSchemaDescription xMLSchemaDescription = (XMLSchemaDescription)object;
        if (this.fNamespace != null) {
            return this.fNamespace.equals(xMLSchemaDescription.getTargetNamespace());
        }
        return xMLSchemaDescription.getTargetNamespace() == null;
    }

    @Override
    public int hashCode() {
        return this.fNamespace == null ? 0 : this.fNamespace.hashCode();
    }

    public void setContextType(short s) {
        this.fContextType = s;
    }

    public void setTargetNamespace(String string) {
        this.fNamespace = string;
    }

    public void setLocationHints(String[] stringArray) {
        int n = stringArray.length;
        this.fLocationHints = new String[n];
        System.arraycopy(stringArray, 0, this.fLocationHints, 0, n);
    }

    public void setTriggeringComponent(QName qName) {
        this.fTriggeringComponent = qName;
    }

    public void setEnclosingElementName(QName qName) {
        this.fEnclosedElementName = qName;
    }

    public void setAttributes(XMLAttributes xMLAttributes) {
        this.fAttributes = xMLAttributes;
    }

    public void reset() {
        super.clear();
        this.fContextType = (short)-1;
        this.fLocationHints = null;
        this.fTriggeringComponent = null;
        this.fEnclosedElementName = null;
        this.fAttributes = null;
    }

    public XSDDescription makeClone() {
        XSDDescription xSDDescription = new XSDDescription();
        xSDDescription.fAttributes = this.fAttributes;
        xSDDescription.fBaseSystemId = this.fBaseSystemId;
        xSDDescription.fContextType = this.fContextType;
        xSDDescription.fEnclosedElementName = this.fEnclosedElementName;
        xSDDescription.fExpandedSystemId = this.fExpandedSystemId;
        xSDDescription.fLiteralSystemId = this.fLiteralSystemId;
        xSDDescription.fLocationHints = this.fLocationHints;
        xSDDescription.fPublicId = this.fPublicId;
        xSDDescription.fNamespace = this.fNamespace;
        xSDDescription.fTriggeringComponent = this.fTriggeringComponent;
        return xSDDescription;
    }
}

