/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.sharing.parse.Parser;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public class InviteReplyType
extends BaseNotificationType {
    public static final String sharedTypeCalendar = "calendar";
    private String sharedType;
    private String href;
    private Boolean accepted;
    private String hostUrl;
    private String inReplyTo;
    private String summary;
    private String commonName;

    public void setSharedType(String val) {
        this.sharedType = val;
    }

    public String getSharedType() {
        return this.sharedType;
    }

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    public void setAccepted(Boolean val) {
        this.accepted = val;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }

    public void setHostUrl(String val) {
        this.hostUrl = val;
    }

    public String getHostUrl() {
        return this.hostUrl;
    }

    public void setInReplyTo(String val) {
        this.inReplyTo = val;
    }

    public String getInReplyTo() {
        return this.inReplyTo;
    }

    public void setSummary(String val) {
        this.summary = val;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setCommonName(String val) {
        this.commonName = val;
    }

    public String getCommonName() {
        return this.commonName;
    }

    @Override
    public QName getElementName() {
        return AppleServerTags.inviteReply;
    }

    @Override
    public String getName() {
        if (super.getName() == null) {
            this.setName(this.getInReplyTo() + "-reply");
        }
        return super.getName();
    }

    @Override
    public void setEncoding(String val) {
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public List<BaseNotificationType.AttributeType> getElementAttributes() {
        return null;
    }

    @Override
    public void prefixHrefs(UrlPrefixer prefixer) throws Throwable {
        this.setHostUrl(prefixer.prefix(this.getHostUrl()));
    }

    @Override
    public void unprefixHrefs(UrlUnprefixer unprefixer) throws Throwable {
        this.setHostUrl(unprefixer.unprefix(this.getHostUrl()));
    }

    @Override
    public void toXml(XmlEmit xml) throws Throwable {
        if (this.getSharedType() != null) {
            xml.openTag(AppleServerTags.inviteReply, "shared-type", this.getSharedType());
        } else {
            xml.openTag(AppleServerTags.inviteReply, "shared-type", sharedTypeCalendar);
        }
        super.toXml(xml);
        xml.property(WebdavTags.href, this.getHref());
        if (this.testAccepted()) {
            xml.emptyTag(AppleServerTags.inviteAccepted);
        } else {
            xml.emptyTag(AppleServerTags.inviteDeclined);
        }
        if (this.getCommonName() == null) {
            xml.property(AppleServerTags.commonName, this.getHref());
        } else {
            xml.property(AppleServerTags.commonName, this.getCommonName());
        }
        xml.openTag(AppleServerTags.hosturl);
        xml.property(WebdavTags.href, this.getHostUrl());
        xml.closeTag(AppleServerTags.hosturl);
        xml.property(AppleServerTags.inReplyTo, this.getInReplyTo());
        xml.property(AppleServerTags.summary, this.getSummary());
        xml.closeTag(AppleServerTags.inviteReply);
    }

    public boolean testAccepted() {
        Boolean f = this.getAccepted();
        if (f == null) {
            return false;
        }
        return f;
    }

    protected void toStringSegment(ToString ts) {
        ts.append("sharedType", this.getSharedType());
        ts.append("href", this.getHref());
        ts.append("accepted", this.getAccepted());
        ts.append("hostUrl", this.getHostUrl());
        ts.append("inReplyTo", this.getInReplyTo());
        ts.append("summary", this.getSummary());
        ts.append("commonName", this.getCommonName());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }

    @Override
    public Object clone() {
        try {
            String xml = this.toXml();
            return new Parser().parseInviteReply(xml);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}

