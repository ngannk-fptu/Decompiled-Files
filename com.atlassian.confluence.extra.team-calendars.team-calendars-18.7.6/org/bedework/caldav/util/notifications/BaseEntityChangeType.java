/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import org.bedework.caldav.util.notifications.ChangedByType;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.WebdavTags;
import org.bedework.webdav.servlet.shared.UrlPrefixer;
import org.bedework.webdav.servlet.shared.UrlUnprefixer;

public class BaseEntityChangeType {
    private String href;
    private ChangedByType changedBy;

    public void setHref(String val) {
        this.href = val;
    }

    public String getHref() {
        return this.href;
    }

    public void setChangedBy(ChangedByType val) {
        this.changedBy = val;
    }

    public ChangedByType getChangedBy() {
        return this.changedBy;
    }

    public void copyForAlias(BaseEntityChangeType copy, String collectionHref) {
        String[] split = Util.splitName(this.href);
        copy.href = Util.buildPath(this.href.endsWith("/"), collectionHref, "/", split[1]);
        copy.changedBy = this.changedBy;
    }

    public void prefixHrefs(UrlPrefixer prefixer) throws Throwable {
        this.setHref(prefixer.prefix(this.getHref()));
    }

    public void unprefixHrefs(UrlUnprefixer unprefixer) throws Throwable {
        this.setHref(unprefixer.unprefix(this.getHref()));
    }

    public void toXmlSegment(XmlEmit xml) throws Throwable {
        xml.property(WebdavTags.href, this.getHref());
        if (this.getChangedBy() != null) {
            this.getChangedBy().toXml(xml);
        }
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
        if (this.getChangedBy() != null) {
            this.getChangedBy().toStringSegment(ts);
        }
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

