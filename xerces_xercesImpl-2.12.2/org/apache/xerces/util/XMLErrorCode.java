/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

final class XMLErrorCode {
    private String fDomain;
    private String fKey;

    public XMLErrorCode(String string, String string2) {
        this.fDomain = string;
        this.fKey = string2;
    }

    public void setValues(String string, String string2) {
        this.fDomain = string;
        this.fKey = string2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof XMLErrorCode)) {
            return false;
        }
        XMLErrorCode xMLErrorCode = (XMLErrorCode)object;
        return this.fDomain.equals(xMLErrorCode.fDomain) && this.fKey.equals(xMLErrorCode.fKey);
    }

    public int hashCode() {
        return this.fDomain.hashCode() + this.fKey.hashCode();
    }
}

