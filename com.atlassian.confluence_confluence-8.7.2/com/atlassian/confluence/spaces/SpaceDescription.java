/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceDescription
extends SpaceContentEntityObject {
    private static final Logger log = LoggerFactory.getLogger(SpaceDescription.class);
    public static final String CONTENT_TYPE_SPACEDESC = "spacedesc";
    public static final String CONTENT_TYPE_PERSONAL_SPACEDESC = "personalspacedesc";

    public SpaceDescription() {
    }

    public SpaceDescription(Space space) {
        this.setSpace(space);
    }

    @Override
    public String getDisplayTitle() {
        if (!this.isLatestVersion()) {
            return this.getLatestVersion().getDisplayTitle();
        }
        if (this.getSpace() == null) {
            return "Orphaned Space Description";
        }
        return this.getSpace().getName();
    }

    @Override
    public ContentEntityObject getLatestVersion() {
        return (ContentEntityObject)super.getLatestVersion();
    }

    @Override
    public String getUrlPath() {
        if (this.getSpace() != null) {
            return this.getSpace().getUrlPath();
        }
        log.debug("Cannot determine URL path for this space description (id=" + this.getId() + ") since no space is associated with it.");
        return "/fourohfour.action";
    }

    @Override
    public String getType() {
        if (this.isPersonalSpace()) {
            return CONTENT_TYPE_PERSONAL_SPACEDESC;
        }
        return CONTENT_TYPE_SPACEDESC;
    }

    @Override
    public String getSpaceKey() {
        return this.getSpace().getKey();
    }

    public boolean isPersonalSpace() {
        Space space = ((SpaceDescription)this.getLatestVersion()).getSpace();
        return space != null && SpaceType.PERSONAL.equals(space.getSpaceType());
    }

    @Override
    public BodyType getDefaultBodyType() {
        return BodyType.WIKI;
    }

    @Override
    public String getAttachmentUrlPath(Attachment attachment) {
        this.ensureAttachmentBelongsToContent(attachment);
        return this.getUrlPath();
    }
}

