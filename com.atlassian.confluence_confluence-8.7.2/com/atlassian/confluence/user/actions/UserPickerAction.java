/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.fugue.Pair
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.internal.user.GroupSearchRequest;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedConfluenceAccess;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractSearchCrowdUsersAction;
import com.atlassian.confluence.validation.XWorkValidationResultSupport;
import com.atlassian.fugue.Pair;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@RequiresLicensedConfluenceAccess
public class UserPickerAction
extends AbstractSearchCrowdUsersAction {
    private String onPopupSubmit;
    private boolean userSearch = true;
    private String groupTerm;
    private String memberGroups;

    @Override
    public void validate() {
        if (this.userSearch) {
            super.validate();
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        if (this.userSearch) {
            return this.doUserSearch();
        }
        return this.doMemberOfGroupsSearch();
    }

    private String doMemberOfGroupsSearch() {
        GroupSearchRequest searchRequest = (GroupSearchRequest)GroupSearchRequest.builder().groupTerm(this.groupTerm).showUnlicensedUsers(this.isShowUnlicensedUsers()).build();
        try {
            Pair<List<String>, PageResponse<ConfluenceUser>> result = this.getUserSearchService().doMemberOfGroupsSearch(this.getPageRequest(), searchRequest);
            List groups = (List)result.left();
            this.setMemberGroups(StringUtils.join((Iterable)groups, (String)", "));
            this.setPageResponse((PageResponse<ConfluenceUser>)((PageResponse)result.right()));
            return "success";
        }
        catch (ServiceException e) {
            XWorkValidationResultSupport.addAnyMessages(this.getMessageHolder(), e);
            return "error";
        }
    }

    public String getGroupTerm() {
        return this.groupTerm;
    }

    public void setGroupTerm(String groupTerm) {
        this.groupTerm = groupTerm;
    }

    public String getOnPopupSubmit() {
        return this.onPopupSubmit;
    }

    public void setOnPopupSubmit(String onPopupSubmit) {
        this.onPopupSubmit = onPopupSubmit;
    }

    public boolean isUserSearch() {
        return this.userSearch;
    }

    public void setUserSearch(boolean userSearch) {
        this.userSearch = userSearch;
    }

    public String getMemberGroups() {
        return this.memberGroups;
    }

    public void setMemberGroups(String memberGroups) {
        this.memberGroups = memberGroups;
    }
}

