/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.space.SpaceTrashViewEvent;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;

public class ViewTrashAction
extends AbstractSpaceAdminAction
implements Evented<SpaceTrashViewEvent> {
    private static final String TRASH_CONTENT_PAGE_SIZE_KEY = "space.trash.content.pagination.size.max";
    private static final Integer TRASH_CONTENT_PAGE_SIZE_MAX = Integer.getInteger("space.trash.content.pagination.size.max", 500);
    private TrashManager trashManager;
    private boolean isReverse;
    private long contentId;
    private int limit;

    public PageResponse<Content> getTrash() {
        LimitedRequest request = this.contentId == 0L ? LimitedRequestImpl.create((Cursor)ContentCursor.EMPTY_CURSOR, (int)(this.limit > 0 ? this.limit : TRASH_CONTENT_PAGE_SIZE_MAX), (int)TRASH_CONTENT_PAGE_SIZE_MAX) : LimitedRequestImpl.create((Cursor)ContentCursor.createCursor((boolean)this.isReverse, (long)this.contentId), (int)(this.limit > 0 ? this.limit : TRASH_CONTENT_PAGE_SIZE_MAX), (int)TRASH_CONTENT_PAGE_SIZE_MAX);
        Expansion[] expansions = ExpansionsParser.parse((String)"ancestors,container,space,metadata");
        return this.trashManager.getTrashContents(this.getSpace(), request, expansions);
    }

    public void setTrashManager(TrashManager trashManager) {
        this.trashManager = trashManager;
    }

    public TrashManager getTrashManager() {
        return this.trashManager;
    }

    public boolean isReverse() {
        return this.isReverse;
    }

    public void setReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public SpaceTrashViewEvent getEventToPublish(String result) {
        return new SpaceTrashViewEvent(this, this.getSpace());
    }
}

