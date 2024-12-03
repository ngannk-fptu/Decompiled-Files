/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.List;

public class PermittedLabelView
implements Labelable {
    private Labelable original;
    private User actingUser;
    private boolean hideSpecialLabels;
    private List<Label> visibleLabels = null;

    public PermittedLabelView(Labelable original, User actingUser, boolean hideSpecialLabels) {
        this.original = original;
        this.actingUser = actingUser;
        this.hideSpecialLabels = hideSpecialLabels;
    }

    @Override
    public List<Label> getLabels() {
        return this.getVisibleLabels();
    }

    @Override
    public int getLabelCount() {
        return this.getVisibleLabels().size();
    }

    @Override
    public boolean isFavourite(ConfluenceUser user) {
        return user != null && (this.hasPersonalLabel("favourite", user) || this.hasPersonalLabel("favorite", user));
    }

    private boolean hasPersonalLabel(String labelName, ConfluenceUser user) {
        return this.getVisibleLabels().contains(new Label(labelName, Namespace.PERSONAL, user));
    }

    private List<Label> getVisibleLabels() {
        if (this.visibleLabels == null) {
            List<Label> originalLabels = this.original.getLabels();
            this.visibleLabels = LabelUtil.getLabelFilters(this.actingUser, this.hideSpecialLabels).filterList(originalLabels);
        }
        return this.visibleLabels;
    }

    public Labelable getDelegate() {
        return this.original;
    }
}

