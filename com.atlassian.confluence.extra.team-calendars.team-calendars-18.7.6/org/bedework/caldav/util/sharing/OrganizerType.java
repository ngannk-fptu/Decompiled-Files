/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class OrganizerType {
    private String href;
    private String commonName;

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    public void setCommonName(String val) {
        this.commonName = val;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.organizer);
        xml.property(WebdavTags.href, this.getHref());
        if (this.getCommonName() == null) {
            xml.property(AppleServerTags.commonName, this.getHref());
        } else {
            xml.property(AppleServerTags.commonName, this.getCommonName());
        }
        xml.closeTag(AppleServerTags.organizer);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
        ts.append("commonName", this.getCommonName());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

