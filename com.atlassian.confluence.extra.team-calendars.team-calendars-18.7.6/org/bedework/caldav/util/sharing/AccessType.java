/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;

public class AccessType {
    private Boolean read;
    private Boolean readWrite;

    public void setRead(Boolean val) {
        this.read = val;
    }

    public Boolean getRead() {
        return this.read;
    }

    public void setReadWrite(Boolean val) {
        this.readWrite = val;
    }

    public Boolean getReadWrite() {
        return this.readWrite;
    }

    public boolean testRead() {
        Boolean f = this.getRead();
        if (f == null) {
            return false;
        }
        return f;
    }

    public boolean testReadWrite() {
        Boolean f = this.getReadWrite();
        if (f == null) {
            return false;
        }
        return f;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.access);
        if (this.testRead()) {
            xml.emptyTag(AppleServerTags.read);
        } else if (this.testReadWrite()) {
            xml.emptyTag(AppleServerTags.readWrite);
        }
        xml.closeTag(AppleServerTags.access);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("read", this.getRead());
        ts.append("readWrite", this.getReadWrite());
    }

    public boolean equals(Object o) {
        AccessType that = (AccessType)o;
        return this.testRead() == that.testRead() || this.testReadWrite() == that.testReadWrite();
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

