/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.label.LabelListViewEvent;
import com.atlassian.confluence.internal.labels.LabelManagerInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.actions.AbstractPaginatedListAction;
import java.util.Collections;
import java.util.List;

public class ListLabelsBySpaceAction
extends AbstractPaginatedListAction<Label>
implements Evented<LabelListViewEvent> {
    private List labels = null;
    private LabelManagerInternal labelManagerInternal;
    private PaginationService paginationService;

    public void setLabelManager(LabelManagerInternal labelManager) {
        this.labelManagerInternal = labelManager;
    }

    public void setApiPaginationService(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @Override
    public LabelListViewEvent getEventToPublish(String result) {
        return new LabelListViewEvent(this, this.getSpace(), "by-space");
    }

    @Override
    public List getItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean isSupportPaginationService() {
        return true;
    }

    public PageResponse<Label> getPageResponse() {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((int)this.paginationSupport.getStartIndex(), (int)ITEMS_PER_PAGE, (int)ITEMS_PER_PAGE, (boolean)true);
        PageResponse pagingResult = this.paginationService.performPaginationRequest(limitedRequest, nextRequest -> {
            List<Label> pageResult = this.labelManagerInternal.getLabelsInSpace(this.getSpaceKey(), limitedRequest);
            PageResponseImpl response = PageResponseImpl.builder().addAll(pageResult).pageRequest(limitedRequest).hasMore(pageResult.size() >= nextRequest.getLimit()).build();
            return response;
        }, label -> label);
        return pagingResult;
    }

    @Override
    public long getItemsCount() {
        return this.labelManagerInternal.getTotalLabelInSpace(this.getSpaceKey());
    }

    public int getStartIndex() {
        return this.paginationSupport.getStartIndex();
    }

    @Override
    public void validate() {
        super.validate();
        if (this.hasErrors()) {
            return;
        }
        if (this.getSpaceKey() == null || this.getSpaceKey().equals("")) {
            this.addFieldError("key", this.getText("missing.name"));
        }
    }
}

