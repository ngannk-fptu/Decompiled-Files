/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.content.page.PageListViewEvent;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractPaginatedListAction;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebSudoRequired
public class ListPermissionPagesAction
extends AbstractPaginatedListAction
implements SpaceAdministrative,
Evented<PageListViewEvent> {
    private PageManagerInternal pageManagerInternal;
    private PaginationService paginationService;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return super.execute();
    }

    public void setApiPaginationService(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    public void setPageManager(PageManagerInternal pageManager) {
        this.pageManagerInternal = pageManager;
    }

    @Override
    public PageListViewEvent getEventToPublish(String result) {
        return new PageListViewEvent(this, this.getSpace(), "permissions");
    }

    public List getPermissionPages() {
        return this.paginationSupport.getItems();
    }

    @Override
    public List getItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean isSupportPaginationService() {
        return true;
    }

    public PageResponse<Page> getPageResponse() {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((int)this.paginationSupport.getStartIndex(), (int)ITEMS_PER_PAGE, (int)ITEMS_PER_PAGE, (boolean)true);
        PageResponse pagingResult = this.paginationService.performPaginationRequest(limitedRequest, nextRequest -> {
            Collection<Page> pageResult = this.pageManagerInternal.getPermissionPages(this.space, limitedRequest);
            PageResponseImpl response = PageResponseImpl.builder().addAll(pageResult).pageRequest(limitedRequest).hasMore(pageResult.size() >= nextRequest.getLimit()).build();
            return response;
        }, page -> page);
        return pagingResult;
    }

    @Override
    public long getItemsCount() {
        return this.pageManagerInternal.getPermissionPagesCount(this.space);
    }

    public List<ContentPermission> getPermissions(Page page) {
        return page.getPermissions();
    }

    protected List getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        if (this.getSpace() != null) {
            this.addPermissionTypeTo("VIEWSPACE", permissionTypes);
            this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissionTypes);
        }
        return permissionTypes;
    }
}

