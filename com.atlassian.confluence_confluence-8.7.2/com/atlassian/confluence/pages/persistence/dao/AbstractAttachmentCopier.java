/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAttachmentCopier
implements AttachmentDao.AttachmentCopier {
    private static final Logger log = LoggerFactory.getLogger(AbstractAttachmentCopier.class);
    protected ProgressMeter progress;
    private List<ConfluenceEntityObject> parentContentToExclude = new ArrayList<ConfluenceEntityObject>();
    private List<Space> spacesToInclude = new ArrayList<Space>();

    @Override
    public void setParentContentToExclude(List<? extends ConfluenceEntityObject> contentList) {
        this.parentContentToExclude = ImmutableList.copyOf(contentList);
    }

    @Override
    public void setSpacesToInclude(List<? extends Space> spaceList) {
        this.spacesToInclude = new ArrayList<Space>(spaceList);
    }

    @Override
    public void setProgressMeter(ProgressMeter progress) {
        this.progress = progress;
    }

    protected boolean isSpaceIncluded(Space space) {
        return this.spacesToInclude.contains(space);
    }

    protected boolean isContentSpaceIncluded(ContentEntityObject content) {
        if (this.spacesToInclude.size() == 0) {
            return true;
        }
        if (!(content instanceof SpaceContentEntityObject)) {
            if (log.isDebugEnabled()) {
                log.debug("Content '" + content + "' is not a SpaceContentEntityObject and there are Space restrictions.");
            }
            return false;
        }
        SpaceContentEntityObject spaceContent = (SpaceContentEntityObject)content;
        Space space = spaceContent.getSpace();
        return this.isSpaceIncluded(space);
    }

    protected boolean isContentExcluded(ContentEntityObject content) {
        return this.parentContentToExclude.contains(content);
    }

    protected List<Space> getSpacesToInclude() {
        return this.spacesToInclude;
    }
}

