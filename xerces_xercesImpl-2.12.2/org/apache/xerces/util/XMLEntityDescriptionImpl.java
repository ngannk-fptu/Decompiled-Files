/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.util.XMLResourceIdentifierImpl;

public class XMLEntityDescriptionImpl
extends XMLResourceIdentifierImpl
implements XMLEntityDescription {
    protected String fEntityName;

    public XMLEntityDescriptionImpl() {
    }

    public XMLEntityDescriptionImpl(String string, String string2, String string3, String string4, String string5) {
        this.setDescription(string, string2, string3, string4, string5);
    }

    public XMLEntityDescriptionImpl(String string, String string2, String string3, String string4, String string5, String string6) {
        this.setDescription(string, string2, string3, string4, string5, string6);
    }

    @Override
    public void setEntityName(String string) {
        this.fEntityName = string;
    }

    @Override
    public String getEntityName() {
        return this.fEntityName;
    }

    public void setDescription(String string, String string2, String string3, String string4, String string5) {
        this.setDescription(string, string2, string3, string4, string5, null);
    }

    public void setDescription(String string, String string2, String string3, String string4, String string5, String string6) {
        this.fEntityName = string;
        this.setValues(string2, string3, string4, string5, string6);
    }

    @Override
    public void clear() {
        super.clear();
        this.fEntityName = null;
    }

    @Override
    public int hashCode() {
        int n = super.hashCode();
        if (this.fEntityName != null) {
            n += this.fEntityName.hashCode();
        }
        return n;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.fEntityName != null) {
            stringBuffer.append(this.fEntityName);
        }
        stringBuffer.append(':');
        if (this.fPublicId != null) {
            stringBuffer.append(this.fPublicId);
        }
        stringBuffer.append(':');
        if (this.fLiteralSystemId != null) {
            stringBuffer.append(this.fLiteralSystemId);
        }
        stringBuffer.append(':');
        if (this.fBaseSystemId != null) {
            stringBuffer.append(this.fBaseSystemId);
        }
        stringBuffer.append(':');
        if (this.fExpandedSystemId != null) {
            stringBuffer.append(this.fExpandedSystemId);
        }
        stringBuffer.append(':');
        if (this.fNamespace != null) {
            stringBuffer.append(this.fNamespace);
        }
        return stringBuffer.toString();
    }
}

