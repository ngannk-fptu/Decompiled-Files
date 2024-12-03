/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.AbstractPageLinkRenamingBean;
import org.apache.commons.lang3.StringUtils;

public class PageLinkRenamingBean
extends AbstractPageLinkRenamingBean {
    public PageLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        super(referringContent, pageBeingChanged, newSpaceKey, newTitle);
    }

    @Override
    protected String getCurrentLinkPart() {
        String pageTitle = this.getPageBeingChanged().getTitle();
        if (StringUtils.isNotEmpty((CharSequence)this.getReferringContentSpaceKey()) && this.getReferringContentSpaceKey().equalsIgnoreCase(this.getPageBeingChanged().getSpaceKey())) {
            return "(?:" + this.getPageBeingChanged().getSpaceKey() + ":)?(?i)\\Q" + pageTitle + "\\E";
        }
        return "(?i)\\Q" + this.getPageBeingChanged().getSpaceKey() + ":" + pageTitle + "\\E";
    }

    @Override
    protected String getNewLinkPart() {
        if (StringUtils.isNotEmpty((CharSequence)this.getReferringContentSpaceKey()) && this.getReferringContentSpaceKey().equalsIgnoreCase(this.newSpaceKey)) {
            return this.newTitle;
        }
        return this.newSpaceKey + ":" + this.newTitle;
    }
}

