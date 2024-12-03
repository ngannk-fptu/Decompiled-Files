/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.core.util.PairType
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
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.actions.Tabbed;
import com.atlassian.confluence.user.actions.SearchableUserAction;
import com.atlassian.core.util.PairType;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public final class BrowseUnsyncedUsersAction
extends ConfluenceActionSupport
implements Tabbed,
SearchableUserAction<Person> {
    private String SHOW_ALL_SEARCH_TERM = "*";
    private PageResponse<Person> pageResponse;
    private int startIndex;
    private int resultsPerPage = 10;
    private String searchTerm;
    private List<PairType> resultsPerPageOptions;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doUserSearch() {
        this.setPageResponse((PageResponse<Person>)this.personService.search().forUnsyncedUsers(this.convertSearchTerm(this.searchTerm)).fetchMany(this.getPageRequest()));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String showAllUsers() {
        this.searchTerm = this.SHOW_ALL_SEARCH_TERM;
        return this.doUserSearch();
    }

    @Override
    public void validate() {
        if (StringUtils.isEmpty((CharSequence)this.searchTerm)) {
            this.addFieldError("searchTerm", this.getText("must.specify.search.term"));
        }
        super.validate();
    }

    private String convertSearchTerm(String inputTerm) {
        if (this.SHOW_ALL_SEARCH_TERM.equals(inputTerm)) {
            return "";
        }
        return inputTerm;
    }

    @Override
    public PageRequest getPageRequest() {
        return new SimplePageRequest(this.startIndex, this.resultsPerPage);
    }

    @Override
    public boolean isShowAll() {
        return this.SHOW_ALL_SEARCH_TERM.equals(this.searchTerm);
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

    private List<PairType> buildResultsPerPageOptions() {
        List<Integer> levels = Arrays.asList(10, 20, 50, 100);
        return levels.stream().map(level -> new PairType((Serializable)level, (Serializable)((Object)level.toString()))).collect(Collectors.toList());
    }

    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public PageResponse<Person> getPageResponse() {
        return this.pageResponse;
    }

    @Override
    public void setPageResponse(PageResponse<Person> pageResponse) {
        this.pageResponse = pageResponse;
    }

    @Override
    public String getSelectedTab() {
        return "unsynced";
    }
}

