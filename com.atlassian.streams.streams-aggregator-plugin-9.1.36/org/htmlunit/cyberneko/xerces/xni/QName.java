/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni;

public class QName
implements Cloneable {
    public String prefix;
    public String localpart;
    public String rawname;
    public String uri;

    public QName() {
    }

    public QName(String prefix, String localpart, String rawname, String uri) {
        this.setValues(prefix, localpart, rawname, uri);
    }

    public QName(QName qname) {
        this.setValues(qname);
    }

    public void setValues(QName qname) {
        this.prefix = qname.prefix;
        this.localpart = qname.localpart;
        this.rawname = qname.rawname;
        this.uri = qname.uri;
    }

    public void setValues(String prefix, String localpart, String rawname, String uri) {
        this.prefix = prefix;
        this.localpart = localpart;
        this.rawname = rawname;
        this.uri = uri;
    }

    public Object clone() {
        return new QName(this);
    }

    public int hashCode() {
        if (this.uri != null) {
            return this.uri.hashCode() + (this.localpart != null ? this.localpart.hashCode() : 0);
        }
        return this.rawname != null ? this.rawname.hashCode() : 0;
    }

    public boolean equals(Object object) {
        if (object instanceof QName) {
            QName qname = (QName)object;
            if (qname.uri != null) {
                return qname.uri.equals(this.uri) && this.localpart == qname.localpart;
            }
            if (this.uri == null) {
                return this.rawname == qname.rawname;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        boolean comma = false;
        if (this.prefix != null) {
            str.append("prefix=\"").append(this.prefix).append('\"');
            comma = true;
        }
        if (this.localpart != null) {
            if (comma) {
                str.append(',');
            }
            str.append("localpart=\"").append(this.localpart).append('\"');
            comma = true;
        }
        if (this.rawname != null) {
            if (comma) {
                str.append(',');
            }
            str.append("rawname=\"").append(this.rawname).append('\"');
            comma = true;
        }
        if (this.uri != null) {
            if (comma) {
                str.append(',');
            }
            str.append("uri=\"").append(this.uri).append('\"');
        }
        return str.toString();
    }
}

