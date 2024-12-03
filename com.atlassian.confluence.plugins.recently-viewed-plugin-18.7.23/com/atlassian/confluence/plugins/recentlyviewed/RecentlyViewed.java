/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.pages.AbstractPage
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.plugins.recentlyviewed;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.pages.AbstractPage;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class RecentlyViewed {
    private long id;
    private long lastSeen;
    private String title;
    private String space;
    private String type;
    private String url;

    public RecentlyViewed() {
    }

    public RecentlyViewed(long id, long lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }

    @Deprecated
    public void setPageData(AbstractPage abstractPage) {
        this.title = abstractPage.getTitle();
        this.space = abstractPage.isLatestVersion() ? abstractPage.getSpace().getName() : abstractPage.getOriginalVersionPage().getSpace().getName();
        this.type = abstractPage.getType();
        this.url = abstractPage.getUrlPath();
    }

    public void setContent(Content content) {
        this.title = content.getTitle();
        this.space = content.getSpace().getName();
        this.type = content.getType().serialise();
        this.url = ((Link)content.getLinks().get(LinkType.WEB_UI)).getPath();
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSpace() {
        return this.space;
    }

    public String getType() {
        return this.type;
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.lastSeen, this.title, this.space, this.type, this.url);
    }

    public boolean equals(Object object) {
        if (!(object instanceof RecentlyViewed)) {
            return false;
        }
        RecentlyViewed other = (RecentlyViewed)object;
        return Objects.equals(this.id, other.id) && Objects.equals(this.lastSeen, other.lastSeen) && Objects.equals(this.title, other.title) && Objects.equals(this.space, other.space) && Objects.equals(this.type, other.type) && Objects.equals(this.url, other.url);
    }
}

