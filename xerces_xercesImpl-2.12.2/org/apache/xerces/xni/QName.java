/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni;

public class QName
implements Cloneable {
    public String prefix;
    public String localpart;
    public String rawname;
    public String uri;

    public QName() {
        this.clear();
    }

    public QName(String string, String string2, String string3, String string4) {
        this.setValues(string, string2, string3, string4);
    }

    public QName(QName qName) {
        this.setValues(qName);
    }

    public void setValues(QName qName) {
        this.prefix = qName.prefix;
        this.localpart = qName.localpart;
        this.rawname = qName.rawname;
        this.uri = qName.uri;
    }

    public void setValues(String string, String string2, String string3, String string4) {
        this.prefix = string;
        this.localpart = string2;
        this.rawname = string3;
        this.uri = string4;
    }

    public void clear() {
        this.prefix = null;
        this.localpart = null;
        this.rawname = null;
        this.uri = null;
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
            QName qName = (QName)object;
            if (qName.uri != null) {
                return this.uri == qName.uri && this.localpart == qName.localpart;
            }
            if (this.uri == null) {
                return this.rawname == qName.rawname;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        boolean bl = false;
        if (this.prefix != null) {
            stringBuffer.append("prefix=\"").append(this.prefix).append('\"');
            bl = true;
        }
        if (this.localpart != null) {
            if (bl) {
                stringBuffer.append(',');
            }
            stringBuffer.append("localpart=\"").append(this.localpart).append('\"');
            bl = true;
        }
        if (this.rawname != null) {
            if (bl) {
                stringBuffer.append(',');
            }
            stringBuffer.append("rawname=\"").append(this.rawname).append('\"');
            bl = true;
        }
        if (this.uri != null) {
            if (bl) {
                stringBuffer.append(',');
            }
            stringBuffer.append("uri=\"").append(this.uri).append('\"');
        }
        return stringBuffer.toString();
    }
}

