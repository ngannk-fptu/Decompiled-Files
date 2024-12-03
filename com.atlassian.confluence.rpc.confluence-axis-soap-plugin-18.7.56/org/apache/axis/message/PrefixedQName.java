/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;

public class PrefixedQName
implements Name {
    private static final String emptyString = "".intern();
    private String prefix;
    private QName qName;

    public PrefixedQName(String uri, String localName, String pre) {
        this.qName = new QName(uri, localName);
        this.prefix = pre == null ? emptyString : pre.intern();
    }

    public PrefixedQName(QName qname) {
        this.qName = qname;
        this.prefix = emptyString;
    }

    public String getLocalName() {
        return this.qName.getLocalPart();
    }

    public String getQualifiedName() {
        StringBuffer buf = new StringBuffer(this.prefix);
        if (this.prefix != emptyString) {
            buf.append(':');
        }
        buf.append(this.qName.getLocalPart());
        return buf.toString();
    }

    public String getURI() {
        return this.qName.getNamespaceURI();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrefixedQName)) {
            return false;
        }
        if (!this.qName.equals(((PrefixedQName)obj).qName)) {
            return false;
        }
        return this.prefix == ((PrefixedQName)obj).prefix;
    }

    public int hashCode() {
        return this.prefix.hashCode() + this.qName.hashCode();
    }

    public String toString() {
        return this.qName.toString();
    }
}

