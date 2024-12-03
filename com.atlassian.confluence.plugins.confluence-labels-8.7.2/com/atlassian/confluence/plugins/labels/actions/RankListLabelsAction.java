/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.Evented
 *  com.atlassian.confluence.event.events.label.LabelListViewEvent
 *  com.atlassian.confluence.labels.actions.RankedNameComparator
 *  com.atlassian.confluence.labels.actions.RankedRankComparator
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.opensymphony.xwork2.ActionContext
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.label.LabelListViewEvent;
import com.atlassian.confluence.labels.actions.RankedNameComparator;
import com.atlassian.confluence.labels.actions.RankedRankComparator;
import com.atlassian.confluence.plugins.labels.actions.AbstractLabelDisplayingAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.xwork2.ActionContext;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class RankListLabelsAction
extends AbstractLabelDisplayingAction
implements Evented<LabelListViewEvent> {
    private List mostPopularLabels;
    private Set results;

    public void setGroupRanks(boolean b) {
        ActionContext.getContext().getSession().put("confluence.labels.heatmap.group.ranks", b);
    }

    public boolean isGroupRanks() {
        Boolean groupRanks = (Boolean)ActionContext.getContext().getSession().get("confluence.labels.heatmap.group.ranks");
        return groupRanks != null && groupRanks != false;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Object comp = this.isGroupRanks() ? new RankedRankComparator() : new RankedNameComparator();
        this.results = this.space != null ? this.labelManager.getMostPopularLabelsWithRanksInSpace(this.space.getKey(), 200, (Comparator)comp) : this.labelManager.getMostPopularLabelsWithRanks(200, (Comparator)comp);
        return "success";
    }

    public LabelListViewEvent getEventToPublish(String result) {
        return new LabelListViewEvent((Object)this, this.getSpace(), "heatmap");
    }

    public Set getResults() {
        return this.results;
    }

    public List getMostPopularLabels() {
        if (this.mostPopularLabels == null) {
            this.mostPopularLabels = this.space != null ? this.labelManager.getMostPopularLabelsInSpace(this.space.getKey(), 200) : this.labelManager.getMostPopularLabels(200);
        }
        return this.mostPopularLabels;
    }
}

