/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.plugins.labels.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.plugins.labels.actions.AbstractUserProfileAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ViewMyLabelsAction
extends AbstractUserProfileAction {
    private static final int PAGE_SIZE = 10;
    private Collection<Label> myLabels = null;
    private List content = null;
    private long labelId;
    private PaginationSupport paginationSupport = new PaginationSupport(10);

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setStartIndex(int startIndex) {
        this.getPaginationSupport().setStartIndex(startIndex);
    }

    public int getPageSize() {
        return 10;
    }

    public List getPaginatedItems() {
        return this.paginationSupport.getPage();
    }

    public Collection<Label> getMyLabels() {
        if (this.myLabels == null) {
            this.myLabels = Collections.EMPTY_LIST;
        }
        return this.myLabels;
    }

    public List getContent() {
        if (this.content == null) {
            this.content = new LinkedList();
        }
        return this.content;
    }

    private List getContentForLabel() {
        Label label = this.getCurrentLabel();
        List result = label != null ? this.labelManager.getCurrentContentForLabel(label) : this.labelManager.getCurrentContentWithPersonalLabel(this.getAuthenticatedUser().getName());
        return this.permissionManager.getPermittedEntities((User)this.getAuthenticatedUser(), Permission.VIEW, result);
    }

    public Label getCurrentLabel() {
        if (this.labelId > 0L) {
            return this.labelManager.getLabel(this.labelId);
        }
        return null;
    }

    public void validate() {
        if (this.hasErrors()) {
            return;
        }
        if (!this.isMyProfile()) {
            this.addActionError(this.getText("cannot.view.another.users.labels"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.myLabels = this.getLabelManager().getUsersLabels(this.getAuthenticatedUser().getName());
        this.myLabels = Collections2.filter(this.myLabels, label -> !"favourite".equals(label.getName()) && !"favorite".equals(label.getName()));
        this.content = this.getContentForLabel();
        this.getPaginationSupport().setItems(this.content);
        return super.execute();
    }

    public long getLabelId() {
        return this.labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public int getContentCount(Label l) {
        return this.labelManager.getContentCount(l);
    }
}

