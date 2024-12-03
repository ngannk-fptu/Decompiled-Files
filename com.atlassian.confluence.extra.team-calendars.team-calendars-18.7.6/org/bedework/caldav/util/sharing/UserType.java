/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import javax.xml.namespace.QName;
import org.bedework.caldav.util.sharing.AccessType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class UserType {
    private String href;
    private String commonName;
    private QName inviteStatus;
    private AccessType access;
    private String summary;
    private boolean externalUser;

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

    public void setInviteStatus(QName val) {
        this.inviteStatus = val;
    }

    public QName getInviteStatus() {
        return this.inviteStatus;
    }

    public void setAccess(AccessType val) {
        this.access = val;
    }

    public AccessType getAccess() {
        return this.access;
    }

    public void setSummary(String val) {
        this.summary = val;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setExternalUser(boolean val) {
        this.externalUser = val;
    }

    public boolean getExternalUser() {
        return this.externalUser;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.user);
        xml.property(WebdavTags.href, this.getHref());
        if (this.getCommonName() == null) {
            xml.property(AppleServerTags.commonName, this.getHref());
        } else {
            xml.property(AppleServerTags.commonName, this.getCommonName());
        }
        xml.emptyTag(this.getInviteStatus());
        this.getAccess().toXml(xml);
        xml.property(AppleServerTags.summary, this.getSummary());
        xml.property(BedeworkServerTags.externalUser, String.valueOf(this.getExternalUser()));
        xml.closeTag(AppleServerTags.user);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
        ts.append("commonName", this.getCommonName());
        ts.append("status", this.getInviteStatus().toString());
        ts.append(this.getAccess().toString());
        ts.append("summary", this.getSummary());
        ts.append("externalUser", this.getExternalUser());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }

    public int hashCode() {
        return this.getHref().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof UserType)) {
            return false;
        }
        UserType that = (UserType)o;
        return this.getHref().equals(that.getHref());
    }
}

