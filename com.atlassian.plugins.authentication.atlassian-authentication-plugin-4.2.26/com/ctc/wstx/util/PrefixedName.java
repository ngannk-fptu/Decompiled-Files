/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import javax.xml.namespace.QName;

public final class PrefixedName
implements Comparable<PrefixedName> {
    private String mPrefix;
    private String mLocalName;
    volatile int mHash = 0;

    public PrefixedName(String prefix, String localName) {
        this.mLocalName = localName;
        this.mPrefix = prefix != null && prefix.length() == 0 ? null : prefix;
    }

    public PrefixedName reset(String prefix, String localName) {
        this.mLocalName = localName;
        this.mPrefix = prefix != null && prefix.length() == 0 ? null : prefix;
        this.mHash = 0;
        return this;
    }

    public static PrefixedName valueOf(QName n) {
        return new PrefixedName(n.getPrefix(), n.getLocalPart());
    }

    public String getPrefix() {
        return this.mPrefix;
    }

    public String getLocalName() {
        return this.mLocalName;
    }

    public boolean isaNsDeclaration() {
        if (this.mPrefix == null) {
            return this.mLocalName == "xmlns";
        }
        return this.mPrefix == "xmlns";
    }

    public boolean isXmlReservedAttr(boolean nsAware, String localName) {
        if (nsAware) {
            if ("xml" == this.mPrefix) {
                return this.mLocalName == localName;
            }
        } else if (this.mLocalName.length() == 4 + localName.length()) {
            return this.mLocalName.startsWith("xml:") && this.mLocalName.endsWith(localName);
        }
        return false;
    }

    public String toString() {
        if (this.mPrefix == null || this.mPrefix.length() == 0) {
            return this.mLocalName;
        }
        StringBuilder sb = new StringBuilder(this.mPrefix.length() + 1 + this.mLocalName.length());
        sb.append(this.mPrefix);
        sb.append(':');
        sb.append(this.mLocalName);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrefixedName)) {
            return false;
        }
        PrefixedName other = (PrefixedName)o;
        if (this.mLocalName != other.mLocalName) {
            return false;
        }
        return this.mPrefix == other.mPrefix;
    }

    public int hashCode() {
        int hash = this.mHash;
        if (hash == 0) {
            hash = this.mLocalName.hashCode();
            if (this.mPrefix != null) {
                hash ^= this.mPrefix.hashCode();
            }
            this.mHash = hash;
        }
        return hash;
    }

    @Override
    public int compareTo(PrefixedName other) {
        String op = other.mPrefix;
        if (op == null || op.length() == 0) {
            if (this.mPrefix != null && this.mPrefix.length() > 0) {
                return 1;
            }
        } else {
            if (this.mPrefix == null || this.mPrefix.length() == 0) {
                return -1;
            }
            int result = this.mPrefix.compareTo(op);
            if (result != 0) {
                return result;
            }
        }
        return this.mLocalName.compareTo(other.mLocalName);
    }
}

