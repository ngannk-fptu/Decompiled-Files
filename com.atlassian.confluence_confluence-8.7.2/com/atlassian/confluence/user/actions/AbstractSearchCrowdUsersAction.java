/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.core.util.PairType
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.actions.Tabbed;
import com.atlassian.confluence.internal.user.UserSearchRequest;
import com.atlassian.confluence.internal.user.UserSearchServiceInternal;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.actions.SearchableUserAction;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.validation.XWorkValidationResultSupport;
import com.atlassian.core.util.PairType;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class AbstractSearchCrowdUsersAction
extends AbstractUsersAction
implements Tabbed,
SearchableUserAction<ConfluenceUser> {
    private static final String MATCH_ALL = "match all";
    private static final String MATCH_ANY = "match any";
    public static final String SHOW_ALL_SEARCH_TERM = "*";
    private UserChecker userChecker;
    private LicenseService licenseService;
    private UserSearchServiceInternal userSearchService;
    private String searchTerm;
    private String usernameTerm;
    private String fullnameTerm;
    private String emailTerm;
    private String operator = "match all";
    private int startIndex;
    private int resultsPerPage = 10;
    private List<PairType> resultsPerPageOptions;
    private String selectedTab = "search";
    private boolean showUnlicensedUsers;
    private PageResponse<ConfluenceUser> result;

    @Override
    public void validate() {
        if (StringUtils.isEmpty((CharSequence)this.usernameTerm) && StringUtils.isEmpty((CharSequence)this.fullnameTerm) && StringUtils.isEmpty((CharSequence)this.searchTerm) && (StringUtils.isEmpty((CharSequence)this.emailTerm) || !this.isEmailVisible())) {
            this.addFieldError("searchTerm", this.getText("must.specify.search.term"));
        }
        super.validate();
    }

    public boolean isSupportsSimpleSearch() {
        return this.userSearchService.isSupportsSimpleSearch();
    }

    public List<String> getOperators() {
        ArrayList<String> ops = new ArrayList<String>();
        ops.add(MATCH_ANY);
        ops.add(MATCH_ALL);
        return ops;
    }

    @Override
    public String getSelectedTab() {
        return this.selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doUserSearch() {
        UserSearchRequest searchRequest = (UserSearchRequest)UserSearchRequest.builder().searchTerm(this.searchTerm).emailTerm(this.emailTerm).fullnameTerm(this.fullnameTerm).usernameTerm(this.usernameTerm).showEmail(this.isEmailVisible()).showUnlicensedUsers(this.showUnlicensedUsers).build();
        try {
            this.result = this.userSearchService.doUserSearch(this.getPageRequest(), searchRequest);
            this.resultsPerPageOptions = this.buildResultsPerPageOptions();
            return "success";
        }
        catch (ServiceException e) {
            XWorkValidationResultSupport.addAnyMessages(this.getMessageHolder(), e);
            return "error";
        }
    }

    @Override
    public PageRequest getPageRequest() {
        return new SimplePageRequest(this.startIndex, this.resultsPerPage);
    }

    @Override
    public boolean isShowAll() {
        return SHOW_ALL_SEARCH_TERM.equals(this.getSearchTerm());
    }

    @Override
    public int getResultsPerPage() {
        return this.resultsPerPage;
    }

    @Override
    public String getSearchTerm() {
        return this.searchTerm;
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getUsernameTerm() {
        return this.usernameTerm;
    }

    public void setUsernameTerm(String usernameTerm) {
        this.usernameTerm = usernameTerm;
    }

    public String getFullnameTerm() {
        return this.fullnameTerm;
    }

    public void setFullnameTerm(String fullnameTerm) {
        this.fullnameTerm = fullnameTerm;
    }

    public String getEmailTerm() {
        return this.emailTerm;
    }

    public void setEmailTerm(String emailTerm) {
        this.emailTerm = emailTerm;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isShowUnlicensedUsers() {
        return this.showUnlicensedUsers;
    }

    public void setShowUnlicensedUsers(boolean showUnlicensedUsers) {
        this.showUnlicensedUsers = showUnlicensedUsers;
    }

    public String getLicenseErrorHtml() {
        ConfluenceLicense confluenceLicense = this.licenseService.retrieve();
        int currentUsers = this.userChecker.getNumberOfRegisteredUsers();
        if (currentUsers == -1) {
            return this.getText("license.unabletoretrieveusers");
        }
        if (!confluenceLicense.isUnlimitedNumberOfUsers() && confluenceLicense.getMaximumNumberOfUsers() <= currentUsers) {
            String msgKey = "too.many.users";
            return this.getText(msgKey, Arrays.asList(confluenceLicense.getMaximumNumberOfUsers(), currentUsers)) + "<p>" + this.getText("buy.upgrade", new String[]{this.getText("url.atlassian.license.upgrade")}) + " " + this.getText("license.upgrades", new String[]{this.getText("hitcounter.license.upgrades")}) + "</p>" + this.getText("contact.us", new String[]{this.getText("mailto.sales")});
        }
        return this.getText("no.license", new String[]{this.getText("mailto.confluence.support")});
    }

    @Override
    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    @Override
    public List<PairType> getResultsPerPageOptions() {
        if (this.resultsPerPageOptions == null) {
            this.resultsPerPageOptions = ImmutableList.copyOf(this.buildResultsPerPageOptions());
        }
        return this.resultsPerPageOptions;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    private List<PairType> buildResultsPerPageOptions() {
        List<Integer> levels = Arrays.asList(10, 20, 50, 100);
        return levels.stream().map(level -> new PairType((Serializable)level, (Serializable)((Object)level.toString()))).collect(Collectors.toList());
    }

    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public void setPageResponse(PageResponse<ConfluenceUser> result) {
        this.result = result;
    }

    @Override
    public PageResponse<ConfluenceUser> getPageResponse() {
        return this.result;
    }

    public UserSearchServiceInternal getUserSearchService() {
        return this.userSearchService;
    }

    public void setUserSearchService(UserSearchServiceInternal userSearchService) {
        this.userSearchService = userSearchService;
    }

    @Override
    public boolean isPermitted() {
        return this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), null, this.getAuthenticatedUser());
    }
}

