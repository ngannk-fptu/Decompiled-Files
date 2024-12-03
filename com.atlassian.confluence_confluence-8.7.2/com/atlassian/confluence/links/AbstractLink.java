/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.Hibernate
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.hibernate.Hibernate;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractLink
extends ConfluenceEntityObject {
    private ContentEntityObject sourceContent;
    private String linkTitle;

    public ContentEntityObject getSourceContent() {
        return this.sourceContent;
    }

    public void setSourceContent(ContentEntityObject sourceContent) {
        this.sourceContent = sourceContent;
    }

    public String getLinkTitle() {
        return this.linkTitle;
    }

    public void setLinkTitle(String title) {
        if (title == null || !StringUtils.isNotEmpty((CharSequence)title.trim())) {
            this.linkTitle = null;
        }
        this.linkTitle = title;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass((Object)this) != Hibernate.getClass((Object)o)) {
            return false;
        }
        AbstractLink that = (AbstractLink)o;
        return !(this.getSourceContent() != null ? !this.getSourceContent().equals(that.getSourceContent()) : that.getSourceContent() != null);
    }

    public int hashCode() {
        int result = 17;
        result = 29 * result + (this.getSourceContent() != null ? this.getSourceContent().hashCode() : 0);
        return result;
    }
}

