/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelPermissionSupport;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.PermittedLabelView;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLabelableEntityObject
extends AbstractVersionedEntityObject
implements EditableLabelable {
    private List<Labelling> labellings;

    @Override
    public List<Label> getLabels() {
        return LabelUtil.extractLabelsFromLabellings(this.getLabellings());
    }

    @Override
    public int getLabelCount() {
        return this.getLabellings().size();
    }

    @Override
    public boolean isFavourite(ConfluenceUser user) {
        return user != null && (this.hasPersonalLabel("favourite", user) || this.hasPersonalLabel("favorite", user));
    }

    private boolean hasPersonalLabel(String labelName, ConfluenceUser user) {
        return this.getLabels().contains(new Label(labelName, Namespace.PERSONAL, user));
    }

    public List<Label> getVisibleLabels(User user) {
        return new PermittedLabelView(this, user, false).getLabels();
    }

    public List<Label> getPersonalLabels(User user) {
        return LabelPermissionSupport.filterLabelsByNamespace(this.getLabelsForDisplay(user), user, Namespace.PERSONAL);
    }

    public List<Label> getGlobalLabels(User user) {
        return LabelPermissionSupport.filterLabelsByNamespace(this.getLabelsForDisplay(user), user, Namespace.GLOBAL);
    }

    public List<Label> getTeamLabels(User user) {
        return LabelPermissionSupport.filterLabelsByNamespace(this.getLabelsForDisplay(user), user, Namespace.TEAM);
    }

    public List<Label> getLabelsForDisplay(User user) {
        return new PermittedLabelView(this, user, true).getLabels();
    }

    protected void setLabellings(List<Labelling> labellings) {
        this.labellings = labellings;
    }

    @Override
    public List<Labelling> getLabellings() {
        if (this.labellings == null) {
            this.labellings = new ArrayList<Labelling>();
        }
        return this.labellings;
    }

    @Override
    public void addLabelling(Labelling content) {
        if (this.getLabellings().contains(content)) {
            return;
        }
        this.labellings.add(content);
    }

    @Override
    public void removeLabelling(Labelling labelling) {
        if (this.labellings != null) {
            this.labellings.remove(labelling);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractLabelableEntityObject obj = (AbstractLabelableEntityObject)super.clone();
        ArrayList<Labelling> labels = new ArrayList<Labelling>();
        for (Labelling labelling : this.getLabellings()) {
            labels.add(labelling.copy(obj));
        }
        obj.setLabellings(labels);
        return obj;
    }
}

