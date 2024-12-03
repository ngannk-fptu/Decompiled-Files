/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.caldav.util.sharing.AccessType;
import org.bedework.caldav.util.sharing.OrganizerType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public class InviteNotificationType
extends BaseNotificationType {
    public static final String sharedTypeCalendar = "calendar";
    private String sharedType;
    private String uid;
    private String href;
    private QName inviteStatus;
    private AccessType access;
    private String hostUrl;
    private OrganizerType organizer;
    private String summary;
    private List<String> supportedComponents;
    private QName previousStatus;
    private List<BaseNotificationType.AttributeType> attrs;

    public void setSharedType(String val) {
        this.sharedType = val;
    }

    public String getSharedType() {
        return this.sharedType;
    }

    public void setUid(String val) {
        this.uid = val;
    }

    public String getUid() {
        return this.uid;
    }

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
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

    public void setHostUrl(String val) {
        this.hostUrl = val;
    }

    public String getHostUrl() {
        return this.hostUrl;
    }

    public void setOrganizer(OrganizerType val) {
        this.organizer = val;
    }

    public OrganizerType getOrganizer() {
        return this.organizer;
    }

    public void setSummary(String val) {
        this.summary = val;
    }

    public String getSummary() {
        return this.summary;
    }

    public List<String> getSupportedComponents() {
        if (this.supportedComponents == null) {
            this.supportedComponents = new ArrayList<String>();
        }
        return this.supportedComponents;
    }

    public void setPreviousStatus(QName val) {
        this.previousStatus = val;
    }

    public QName getPreviousStatus() {
        return this.previousStatus;
    }

    @Override
    public QName getElementName() {
        return AppleServerTags.inviteNotification;
    }

    @Override
    public void setName(String val) {
    }

    @Override
    public String getName() {
        return this.getUid();
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
        if (this.attrs != null || this.getSharedType() == null) {
            return this.attrs;
        }
        this.attrs = new ArrayList<BaseNotificationType.AttributeType>();
        this.attrs.add(new BaseNotificationType.AttributeType("shared-type", this.getSharedType()));
        return this.attrs;
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
            xml.openTag(AppleServerTags.inviteNotification, "shared-type", this.getSharedType());
        } else {
            xml.openTag(AppleServerTags.inviteNotification);
        }
        super.toXml(xml);
        xml.property(AppleServerTags.uid, this.getUid());
        xml.property(WebdavTags.href, this.getHref());
        xml.emptyTag(this.getInviteStatus());
        if (this.getAccess() != null) {
            this.getAccess().toXml(xml);
        }
        xml.openTag(AppleServerTags.hosturl);
        xml.property(WebdavTags.href, this.getHostUrl());
        xml.closeTag(AppleServerTags.hosturl);
        if (this.getOrganizer() != null) {
            this.getOrganizer().toXml(xml);
        }
        xml.property(AppleServerTags.summary, this.getSummary());
        xml.openTag(CaldavTags.supportedCalendarComponentSet);
        for (String s : this.getSupportedComponents()) {
            xml.emptyTag(CaldavTags.comp, "name", s);
        }
        xml.closeTag(CaldavTags.supportedCalendarComponentSet);
        xml.closeTag(AppleServerTags.inviteNotification);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("uid", this.getUid());
        ts.append("href", this.getHref());
        ts.append("status", this.getInviteStatus());
        ts.append("access", this.getAccess());
        ts.append("hostUrl", this.getHostUrl());
        ts.append("organizer", this.getOrganizer());
        ts.append("summary", this.getSummary());
        ts.append("supportedComponents", this.getSupportedComponents());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

