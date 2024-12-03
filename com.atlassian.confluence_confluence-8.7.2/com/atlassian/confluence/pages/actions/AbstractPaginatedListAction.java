/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 */
package com.atlassian.confluence.pages.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.pages.actions.PaginationServiceSupportActionAware;
import com.atlassian.confluence.pages.actions.PaginationSupportAdaptor;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaginatedListAction<T>
extends AbstractSpaceAction
implements SpaceAware,
PaginationServiceSupportActionAware {
    public static final int ITEMS_PER_PAGE = Integer.getInteger("NumberItemPerPageOfPaginatedListAction", 30);
    protected PaginationSupport paginationSupport;

    public AbstractPaginatedListAction() {
        this(ITEMS_PER_PAGE);
    }

    public String execute() throws Exception {
        if (this.isSupportPaginationService()) {
            PageResponse pageResponse = this.getPageResponse();
            if (pageResponse == null) {
                pageResponse = PageResponseImpl.empty((boolean)false);
            }
            long totalItems = this.getItemsCount();
            this.paginationSupport = new PaginationSupportAdaptor(totalItems, ITEMS_PER_PAGE, pageResponse);
            return "success";
        }
        List items = this.getItems();
        ArrayList itemsList = new ArrayList();
        if (items != null) {
            items.iterator().forEachRemaining(itemsList::add);
            this.getPaginationSupport().setItems(itemsList);
        }
        return "success";
    }

    public abstract List getItems();

    public AbstractPaginatedListAction(int itemsPerPage) {
        this.paginationSupport = new PaginationSupport(itemsPerPage);
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setStartIndex(int startIndex) {
        this.paginationSupport.setStartIndex(startIndex);
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
}

