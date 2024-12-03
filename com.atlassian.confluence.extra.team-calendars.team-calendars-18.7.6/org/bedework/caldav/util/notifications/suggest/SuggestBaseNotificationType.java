/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications.suggest;

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

public abstract class SuggestBaseNotificationType
extends BaseNotificationType {
    private String uid;
    private String href;
    private String suggesterHref;
    private String suggesteeHref;
    private String comment;
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

    public void setSuggesterHref(String val) {
        this.suggesterHref = val;
    }

    public String getSuggesterHref() {
        return this.suggesterHref;
    }

    public void setSuggesteeHref(String val) {
        this.suggesteeHref = val;
    }

    public String getSuggesteeHref() {
        return this.suggesteeHref;
    }

    public void setComment(String val) {
        this.comment = val;
    }

    public String getComment() {
        return this.comment;
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
        this.setSuggesterHref(prefixer.prefix(this.getSuggesterHref()));
        this.setSuggesteeHref(prefixer.prefix(this.getSuggesteeHref()));
    }

    @Override
    public void unprefixHrefs(UrlUnprefixer unprefixer) throws Throwable {
        this.setHref(unprefixer.unprefix(this.getHref()));
        this.setSuggesterHref(unprefixer.unprefix(this.getSuggesterHref()));
        this.setSuggesteeHref(unprefixer.unprefix(this.getSuggesteeHref()));
    }

    protected void bodyToXml(XmlEmit xml) throws Throwable {
        super.toXml(xml);
        xml.property(AppleServerTags.uid, this.getUid());
        xml.property(WebdavTags.href, this.getHref());
        xml.property(BedeworkServerTags.suggesterHref, this.getSuggesterHref());
        xml.property(BedeworkServerTags.suggesteeHref, this.getSuggesteeHref());
        xml.property(BedeworkServerTags.comment, this.getComment());
    }

    protected void toStringSegment(ToString ts) {
        ts.append("uid", this.getUid());
        ts.append("href", this.getHref());
        ts.append("suggesterHref", this.getSuggesterHref());
        ts.append("suggesteeHref", this.getSuggesteeHref());
        ts.append("comment", this.getComment());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

