/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.util.PairType
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.hibernate.DefaultHibernateGroup
 *  com.atlassian.user.impl.osuser.OSUGroup
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  com.atlassian.user.search.query.EntityQueryException
 *  com.atlassian.user.search.query.GroupNameTermQuery
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.admin.criteria.DefaultWritableDirectoryForGroupsExistsCriteria;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.SearchEntitiesManager;
import com.atlassian.confluence.user.actions.AbstractGroupAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.util.PairType;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.hibernate.DefaultHibernateGroup;
import com.atlassian.user.impl.osuser.OSUGroup;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class BrowseGroupsAction
extends AbstractGroupAction {
    private static final Logger log = LoggerFactory.getLogger(BrowseGroupsAction.class);
    protected static final String TERM_DELIM_CHARS = "[\\s,]+";
    private static final List<Integer> OPTIONAL_LEVELS = ImmutableList.of((Object)20, (Object)50, (Object)100);
    private SearchEntitiesManager searchEntitiesManager;
    protected PaginationSupport paginationSupport;
    protected int startIndex;
    private String searchTerm;
    private int resultsPerPage = 10;
    private List<PairType> resultsPerPageOptions;
    private DefaultWritableDirectoryForGroupsExistsCriteria writableDirectoryForGroupsExistsCriteria;

    @Override
    public void validate() {
        super.validate();
        if (this.getName() != null && !this.getName().equals(this.getName().toLowerCase())) {
            this.addFieldError("name", this.getText("group.name.lowercase"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() {
        return this.doSearch();
    }

    public String doAdd() throws Exception {
        if ("confluence-administrators".equals(this.name) && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
            this.addActionError("You do not have permissions to add the " + HtmlUtil.htmlEncode(this.name) + " group.");
            return "error";
        }
        try {
            this.userAccessor.createGroup(this.name);
        }
        catch (InfrastructureException e) {
            this.addActionError("create.group.failed", this.name);
            log.error("Failed to create group: " + this.name, (Throwable)e);
            return "error";
        }
        return "success";
    }

    public String doSearch() {
        List groups;
        if (StringUtils.isBlank((CharSequence)this.searchTerm)) {
            groups = this.userAccessor.getGroupsAsList();
        } else {
            try {
                String[] tokens;
                ArrayList searchTerms = new ArrayList();
                for (String token : tokens = this.searchTerm.trim().split(TERM_DELIM_CHARS)) {
                    String tokenWithWildcards = this.appendWildcard(token);
                    searchTerms.add(this.searchEntitiesManager.getTermQuery(tokenWithWildcards, GroupNameTermQuery.class));
                }
                Query query = this.searchEntitiesManager.createUserQuery(searchTerms, "match any");
                groups = PagerUtils.toList((Pager)this.userAccessor.findGroups(query).pager());
            }
            catch (EntityQueryException e) {
                this.addActionError("Constructing search for groups failed: " + PlainTextToHtmlConverter.encodeHtmlEntities(e.getMessage()));
                log.info("Group search construction failed: " + e.getMessage(), (Throwable)e);
                return "error";
            }
            catch (EntityException e) {
                this.addActionError("Search for groups failed: " + PlainTextToHtmlConverter.encodeHtmlEntities(e.getMessage()));
                log.info("Group search failed: " + e.getMessage(), (Throwable)e);
                return "error";
            }
        }
        this.resultsPerPageOptions = this.buildResultsPerPageOptions(groups.size());
        this.getPaginationSupport().setItems(groups);
        return "success";
    }

    protected String appendWildcard(String s) {
        if (this.settingsManager.getGlobalSettings().isAddWildcardsToUserAndGroupSearches()) {
            if (!((String)s).endsWith("*")) {
                s = (String)s + "*";
            }
            if (!((String)s).startsWith("*")) {
                s = "*" + (String)s;
            }
        }
        return s;
    }

    public PaginationSupport getPaginationSupport() {
        if (this.paginationSupport == null) {
            this.paginationSupport = new PaginationSupport(this.resultsPerPage);
            this.paginationSupport.setItems(Collections.emptyList());
        }
        return this.paginationSupport;
    }

    public boolean canRemove(Group group) {
        return this.isRemovable(group) && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.REMOVE, group);
    }

    public boolean isRemovable(Group group) {
        return group instanceof DefaultHibernateGroup || group instanceof OSUGroup || !this.userAccessor.isReadOnly(group);
    }

    public int getResultsPerPage() {
        return this.resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        if (resultsPerPage >= 0 && resultsPerPage <= 100) {
            this.getPaginationSupport().setPageSize(resultsPerPage);
            this.resultsPerPage = resultsPerPage;
        }
    }

    public List<PairType> getResultsPerPageOptions() {
        if (this.resultsPerPageOptions == null) {
            this.resultsPerPageOptions = this.buildResultsPerPageOptions(0);
        }
        return Collections.unmodifiableList(this.resultsPerPageOptions);
    }

    public boolean canModifyGroups() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) && this.writableDirectoryForGroupsExistsCriteria.isMet();
    }

    private List<PairType> buildResultsPerPageOptions(int size) {
        ArrayList<PairType> options = new ArrayList<PairType>();
        options.add(new PairType((Serializable)Integer.valueOf(10), (Serializable)((Object)Integer.toString(10))));
        int lastLevel = 10;
        for (Integer optionalLevel : OPTIONAL_LEVELS) {
            if (size > lastLevel) {
                options.add(new PairType((Serializable)optionalLevel, (Serializable)((Object)optionalLevel.toString())));
            }
            lastLevel = optionalLevel;
        }
        return options;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.getPaginationSupport().setStartIndex(startIndex);
        this.startIndex = startIndex;
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void setSearchEntitiesManager(SearchEntitiesManager searchEntitiesManager) {
        this.searchEntitiesManager = searchEntitiesManager;
    }

    public void setWritableDirectoryForGroupsExistsCriteria(DefaultWritableDirectoryForGroupsExistsCriteria writableDirectoryForGroupsExistsCriteria) {
        this.writableDirectoryForGroupsExistsCriteria = writableDirectoryForGroupsExistsCriteria;
    }

    public String getUrlEncodeName() {
        return HtmlUtil.urlEncode(HtmlUtil.urlEncode(this.getName()));
    }
}

