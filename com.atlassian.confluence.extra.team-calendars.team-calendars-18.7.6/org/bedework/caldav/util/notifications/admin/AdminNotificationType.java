/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.admin;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.BaseNotificationType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public abstract class AdminNotificationType
extends BaseNotificationType {
    private String uid;
    private String href;
    private String principalHref;
    private String comment;
    private String calsuiteHref;
    private List<BaseNotificationType.AttributeType> attrs;

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

    public void setPrincipalHref(String val) {
        this.principalHref = val;
    }

    public String getPrincipalHref() {
        return this.principalHref;
    }

    public void setComment(String val) {
        this.comment = val;
    }

    public String getComment() {
        return this.comment;
    }

    public String getCalsuiteHref() {
        return this.calsuiteHref;
    }

    public void setCalsuiteHref(String calsuiteHref) {
        this.calsuiteHref = calsuiteHref;
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
        if (this.attrs != null) {
            return this.attrs;
        }
        this.attrs = new ArrayList<BaseNotificationType.AttributeType>();
        return this.attrs;
    }

    @Override
    public void prefixHrefs(UrlPrefixer prefixer) throws Throwable {
        this.setHref(prefixer.prefix(this.getHref()));
        this.setPrincipalHref(prefixer.prefix(this.getPrincipalHref()));
        this.setCalsuiteHref(prefixer.prefix(this.getCalsuiteHref()));
    }

    @Override
    public void unprefixHrefs(UrlUnprefixer unprefixer) throws Throwable {
        this.setHref(unprefixer.unprefix(this.getHref()));
        this.setPrincipalHref(unprefixer.unprefix(this.getPrincipalHref()));
        this.setCalsuiteHref(unprefixer.unprefix(this.getCalsuiteHref()));
    }

    protected void bodyToXml(XmlEmit xml) throws Throwable {
        super.toXml(xml);
        xml.property(AppleServerTags.uid, this.getUid());
        xml.property(WebdavTags.href, this.getHref());
        xml.openTag(WebdavTags.principalURL);
        xml.property(WebdavTags.href, this.getPrincipalHref());
        xml.closeTag(WebdavTags.principalURL);
        xml.property(BedeworkServerTags.comment, this.getComment());
        xml.openTag(BedeworkServerTags.calsuiteURL);
        xml.property(WebdavTags.href, this.getCalsuiteHref());
        xml.closeTag(BedeworkServerTags.calsuiteURL);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("uid", this.getUid());
        ts.append("href", this.getHref());
        ts.append("principalHref", this.getPrincipalHref());
        ts.append("comment", this.getComment());
        ts.append("calsuiteHref", this.getCalsuiteHref());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

