/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentPermissionsQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 *  com.atlassian.confluence.spaces.actions.ViewSpaceSummaryAction
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  com.google.common.collect.Sets
 *  org.apache.struts2.ServletActionContext
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.ia.actions;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.ViewSpaceSummaryAction;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.XsrfTokenGenerator;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Qualifier;

@RequiresAnyConfluenceAccess
public class PagesCollectorAction
extends ViewSpaceSummaryAction {
    private SearchManager searchManager;
    private boolean showBlankExperience;
    private boolean hasCreatePermission;
    private String createPageLink;
    private final XsrfTokenGenerator simpleXsrfTokenGenerator;

    public PagesCollectorAction(@ComponentImport XsrfTokenGenerator simpleXsrfTokenGenerator) {
        this.simpleXsrfTokenGenerator = simpleXsrfTokenGenerator;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.space != null) {
            this.showBlankExperience = this.determineShowBlankExperience();
            this.createPageLink = "/pages/createpage.action?spaceKey=" + GeneralUtil.urlEncode((String)this.space.getKey()) + this.getFromPageParam() + this.getAtlToken();
            this.hasCreatePermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)this.space, Page.class);
        }
        return super.execute();
    }

    public boolean getShowBlankExperience() {
        return this.showBlankExperience;
    }

    public boolean getHasCreatePermission() {
        return this.hasCreatePermission;
    }

    public String getCreatePageLink() {
        return this.createPageLink;
    }

    public String getRoot() {
        return this.space.getHomePage() != null ? "@home" : "@none";
    }

    private boolean determineShowBlankExperience() {
        if (this.space == null) {
            return false;
        }
        HashSet spaceKeys = Sets.newHashSet((Object[])new String[]{this.space.getKey()});
        InSpaceQuery inSpaceQuery = new InSpaceQuery((Set)spaceKeys);
        ContentTypeQuery contentTypeQuery = new ContentTypeQuery(ContentTypeEnum.PAGE);
        ContentPermissionsQuery contentPermissionsQuery = ContentPermissionsQuery.builder().build();
        SearchQuery searchQuery = BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{inSpaceQuery, contentTypeQuery, contentPermissionsQuery});
        ContentSearch search = new ContentSearch(searchQuery, null, 0, 2);
        try {
            SearchResults results = this.searchManager.search((ISearch)search);
            return results.size() <= 1;
        }
        catch (Exception e) {
            return false;
        }
    }

    private String getFromPageParam() {
        Page homePage = this.space.getHomePage();
        return homePage == null ? "" : "&fromPageId=" + homePage.getIdAsString();
    }

    private String getAtlToken() {
        String token = this.simpleXsrfTokenGenerator.getToken(ServletActionContext.getRequest(), true);
        return "&atl_token=" + token;
    }

    public void setContentEntityManager(@Qualifier(value="ContentEntityManager") ContentEntityManager contentEntityManager) {
        super.setContentEntityManager(contentEntityManager);
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
}

