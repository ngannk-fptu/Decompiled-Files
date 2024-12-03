/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.util.AbstractPageLinkRenamingBean;
import org.apache.commons.lang3.StringUtils;

public class BlogPostLinkRenamingBean
extends AbstractPageLinkRenamingBean {
    public BlogPostLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        super(referringContent, pageBeingChanged, newSpaceKey, newTitle);
    }

    protected BlogPost getBlogPostBeingChanged() {
        return (BlogPost)super.getPageBeingChanged();
    }

    @Override
    protected String getCurrentLinkPart() {
        String linkPart = this.getBlogPostBeingChanged().getLinkPart();
        if (StringUtils.isNotEmpty((CharSequence)this.getReferringContentSpaceKey()) && this.getReferringContentSpaceKey().equalsIgnoreCase(this.getPageBeingChanged().getSpaceKey())) {
            return "(?:" + this.getPageBeingChanged().getSpaceKey() + ":)?(?i)\\Q" + linkPart + "\\E";
        }
        return "(?i)\\Q" + this.getPageBeingChanged().getSpaceKey() + ":" + linkPart + "\\E";
    }

    @Override
    protected String getNewLinkPart() {
        if (StringUtils.isNotEmpty((CharSequence)this.getReferringContentSpaceKey()) && this.getReferringContentSpaceKey().equalsIgnoreCase(this.newSpaceKey)) {
            return "/" + this.getBlogPostBeingChanged().getDatePath() + "/" + this.newTitle;
        }
        return this.newSpaceKey + ":/" + this.getBlogPostBeingChanged().getDatePath() + "/" + this.newTitle;
    }
}

