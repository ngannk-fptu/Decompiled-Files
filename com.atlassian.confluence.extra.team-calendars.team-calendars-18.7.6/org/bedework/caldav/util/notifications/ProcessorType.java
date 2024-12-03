/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class ProcessorType {
    private String type;
    private String dtstamp;
    private String status;

    public void setType(String val) {
        this.type = val;
    }

    public String getType() {
        return this.type;
    }

    public void setDtstamp(String val) {
        this.dtstamp = val;
    }

    public String getDtstamp() {
        return this.dtstamp;
    }

    public void setStatus(String val) {
        this.status = val;
    }

    public String getStatus() {
        return this.status;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(BedeworkServerTags.processor);
        xml.property(BedeworkServerTags.type, this.getType());
        if (this.dtstamp != null) {
            xml.property(AppleServerTags.dtstamp, this.getDtstamp());
        }
        if (this.status != null) {
            xml.property(WebdavTags.status, this.getStatus());
        }
        xml.closeTag(BedeworkServerTags.processor);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("ChangedParameter:type", this.getType());
        if (this.dtstamp != null) {
            ts.append("dtstamp", this.getDtstamp());
        }
        if (this.status != null) {
            ts.append("status", this.getStatus());
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

