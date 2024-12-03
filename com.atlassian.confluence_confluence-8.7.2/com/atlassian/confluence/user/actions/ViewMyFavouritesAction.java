/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.event.events.profile.ViewMyFavouritesEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collections;
import java.util.List;

public class ViewMyFavouritesAction
extends AbstractUserProfileAction {
    private static final int PAGE_SIZE = 20;
    private static final int MAX_RESULTS = 500;
    private EventPublisher eventPublisher;
    private List<ContentEntityObject> contentItems = Collections.emptyList();
    private long labelId;
    private PaginationSupport<ContentEntityObject> paginationSupport = new PaginationSupport(20);

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public PaginationSupport<ContentEntityObject> getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setStartIndex(int startIndex) {
        this.getPaginationSupport().setStartIndex(startIndex);
    }

    public int getPageSize() {
        return 20;
    }

    public List<ContentEntityObject> getPaginatedItems() {
        return this.paginationSupport.getPage();
    }

    public List<ContentEntityObject> getContent() {
        return this.contentItems;
    }

    private List<ContentEntityObject> getContentForLabel() {
        List<Object> result;
        Label label = this.getCurrentLabel();
        if (label != null) {
            PartialList<ContentEntityObject> items = this.labelManager.getContentForLabel(0, 500, label);
            result = items.getList();
        } else {
            result = Collections.emptyList();
        }
        return this.permissionManager.getPermittedEntities(this.getAuthenticatedUser(), Permission.VIEW, result);
    }

    public Label getCurrentLabel() {
        if (this.labelId > 0L) {
            return this.labelManager.getLabel(this.labelId);
        }
        return null;
    }

    @Override
    public void validate() {
        if (this.hasErrors()) {
            return;
        }
        if (!this.isMyProfile()) {
            this.addActionError(this.getText("cannot.view.another.users.favourites"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ViewMyFavouritesEvent event = new ViewMyFavouritesEvent(this);
        this.eventPublisher.publish((Object)event);
        ParsedLabelName labelName = LabelParser.parse("my:favourite", this.getAuthenticatedUser());
        Label lab = this.getLabelManager().getLabel(labelName);
        if (lab != null) {
            this.setLabelId(lab.getId());
        }
        this.contentItems = this.getContentForLabel();
        this.getPaginationSupport().setItems(this.contentItems);
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

