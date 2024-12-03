/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.filter.Filter
 *  com.atlassian.core.util.filter.FilterChain
 *  com.atlassian.core.util.filter.ListFilter
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.labels.SpecialLabelFilter;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.history.UserHistory;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.core.util.filter.Filter;
import com.atlassian.core.util.filter.FilterChain;
import com.atlassian.core.util.filter.ListFilter;
import com.atlassian.user.User;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class SpaceLabelManagerImpl
implements SpaceLabelManager {
    private LabelManager labelManager;
    private SpaceManager spaceManager;

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public LabelManager getLabelManager() {
        return this.labelManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    @Override
    public Label addLabel(Space space, String labelName) {
        if (space == null) {
            throw new IllegalArgumentException("space must not be null");
        }
        if (StringUtils.isBlank((CharSequence)labelName)) {
            throw new IllegalArgumentException("labelName must not be null or empty, [labelName=" + labelName + "]");
        }
        if (!LabelUtil.isValidLabelName(labelName) || !LabelUtil.isValidLabelLength(labelName)) {
            throw new IllegalArgumentException("labelName is not valid, [labelName=" + labelName + "]");
        }
        Label label = this.buildTeamLabel(labelName);
        this.spaceManager.ensureSpaceDescriptionExists(space);
        int result = this.labelManager.addLabel(space.getDescription(), label);
        this.recordLabelInteractionInHistory(label);
        if (result == 0) {
            return null;
        }
        if (result == 1) {
            return this.labelManager.getLabel(label);
        }
        return label;
    }

    @Override
    public List getTeamLabelsOnSpace(String spaceKey) {
        return this.getLabelManager().getTeamLabelsForSpace(spaceKey);
    }

    @Override
    public List getAvailableTeamLabels(String spaceKey) {
        List<Label> result = this.getLabelManager().getTeamLabels();
        result.removeAll(this.getTeamLabelsOnSpace(spaceKey));
        return result;
    }

    @Override
    public List getLabelsOnSpace(Space space) {
        ArrayList<Label> result = new ArrayList<Label>();
        for (Label o : space.getDescription().getLabels()) {
            Label label = o;
            if (Namespace.isTeam(label)) continue;
            result.add(label);
        }
        FilterChain filterChain = new FilterChain();
        filterChain.addFilter((Filter)new SpecialLabelFilter());
        return new ListFilter((Filter)filterChain).filterList(result);
    }

    @Override
    public List getSuggestedLabelsForSpace(Space space, User remoteUser) {
        return LabelUtil.getRecentAndPopularLabelsForEntity(space.getDescription(), this.getLabelManager(), 20, remoteUser.getName());
    }

    private Label buildTeamLabel(String labelName) {
        ParsedLabelName parsedLabelName = LabelParser.parse(labelName);
        Label label = parsedLabelName.toLabel();
        if (label.isTeamLabel()) {
            return label;
        }
        return new Label(labelName, Namespace.TEAM);
    }

    private void recordLabelInteractionInHistory(Label label) {
        Map session = ActionContext.getContext().getSession();
        if (session == null) {
            return;
        }
        UserHistory history = (UserHistory)session.get("confluence.user.history");
        if (history == null) {
            history = new UserHistory(20);
            session.put("confluence.user.history", history);
        }
        history.addLabel(label);
    }
}

