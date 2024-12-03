/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

final class XMLErrorCode {
    private String fDomain_;
    private String fKey_;

    XMLErrorCode(String domain, String key) {
        this.fDomain_ = domain;
        this.fKey_ = key;
    }

    public void setValues(String domain, String key) {
        this.fDomain_ = domain;
        this.fKey_ = key;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLErrorCode)) {
            return false;
        }
        XMLErrorCode err = (XMLErrorCode)obj;
        return this.fDomain_.equals(err.fDomain_) && this.fKey_.equals(err.fKey_);
    }

    public int hashCode() {
        return this.fDomain_.hashCode() + this.fKey_.hashCode();
    }
}

