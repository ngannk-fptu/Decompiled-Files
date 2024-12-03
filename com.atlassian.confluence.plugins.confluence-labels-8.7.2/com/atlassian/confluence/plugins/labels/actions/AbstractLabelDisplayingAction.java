/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.persistence.dao.LabelSearchResult
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.google.common.collect.Lists;
import java.util.List;

public abstract class AbstractLabelDisplayingAction
extends AbstractSpaceAction
implements SpaceAware {
    public static final int MAX_LABELS = 15;

    public boolean isSpaceRequired() {
        return false;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    public List<Label> getRecentLabels() {
        if (this.getSpace() == null) {
            return this.labelManager.getRecentlyUsedLabels(15);
        }
        return this.labelManager.getRecentlyUsedLabelsInSpace(this.space.getKey(), 15);
    }

    public List<Label> getPopularLabels() {
        List labelSearchResults = null;
        labelSearchResults = this.getSpace() == null ? this.labelManager.getMostPopularLabels(15) : this.labelManager.getMostPopularLabelsInSpace(this.getSpace().getKey(), 15);
        return Lists.transform((List)labelSearchResults, LabelSearchResult::getLabel);
    }
}

