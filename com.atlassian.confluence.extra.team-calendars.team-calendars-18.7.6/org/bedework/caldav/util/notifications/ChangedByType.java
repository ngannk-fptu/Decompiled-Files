/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class ChangedByType {
    private String commonName;
    private String firstName;
    private String lastName;
    private String dtstamp;
    private String href;

    public void setFirstName(String val) {
        this.firstName = val;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setLastName(String val) {
        this.lastName = val;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setCommonName(String val) {
        this.commonName = val;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public void setDtstamp(String val) {
        this.dtstamp = val;
    }

    public String getDtstamp() {
        return this.dtstamp;
    }

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.changedBy);
        if (this.getCommonName() != null) {
            xml.property(AppleServerTags.commonName, this.getCommonName());
        } else {
            xml.property(AppleServerTags.firstName, this.getFirstName());
            xml.property(AppleServerTags.lastName, this.getLastName());
        }
        if (this.getDtstamp() != null) {
            xml.property(AppleServerTags.dtstamp, this.getDtstamp());
        }
        xml.property(WebdavTags.href, this.getHref());
        xml.closeTag(AppleServerTags.changedBy);
    }

    protected void toStringSegment(ToString ts) {
        if (this.getCommonName() != null) {
            ts.append("commonName", this.getCommonName());
        } else {
            ts.append("firstName", this.getFirstName());
            ts.append("lastName", this.getLastName());
        }
        ts.append("dtstamp", this.getDtstamp());
        ts.append("href", this.getHref());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

