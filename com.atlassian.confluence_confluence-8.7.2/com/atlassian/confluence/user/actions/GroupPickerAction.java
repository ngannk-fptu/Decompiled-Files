/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.core.util.filter.Filter
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.search.query.EntityQueryException
 *  com.atlassian.user.search.query.TermQuery
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.SearchEntitiesManager;
import com.atlassian.confluence.user.actions.AbstractEntityPaginationAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.core.util.filter.Filter;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.TermQuery;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupPickerAction
extends AbstractEntityPaginationAction<Group> {
    private static final Logger log = LoggerFactory.getLogger(GroupPickerAction.class);
    private String key;
    private String actionName;
    private String existingGroups;
    private Collection groups;
    private long pageId;
    private List<Group> excludedGroups = new ArrayList<Group>();
    private String onPopupSubmit;
    private String groupnameTerm;
    private SpaceManager spaceManager;
    private SearchEntitiesManager searchEntitiesManager;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doGroupSearch() {
        TermQuery<Group> termQuery = null;
        if (StringUtils.isNotEmpty((CharSequence)this.groupnameTerm)) {
            try {
                termQuery = this.searchEntitiesManager.getGroupNameTermQuery(this.groupnameTerm);
            }
            catch (EntityQueryException e) {
                this.addActionError(HtmlUtil.htmlEncode(e.getMessage()));
            }
        }
        if (termQuery == null) {
            this.addActionError(this.getText("must.specify.search.term"));
            return "error";
        }
        try {
            List<Group> result = this.searchEntitiesManager.findGroupsAsList(termQuery);
            this.paginationSupport.setItems(result);
            List groupList = this.paginationSupport.getPage();
            this.setGroups(groupList != null ? groupList : Collections.EMPTY_LIST);
        }
        catch (EntityException e) {
            log.error("Unable to perform group search : " + e.getMessage(), (Throwable)e);
            this.addActionError(HtmlUtil.htmlEncode(e.getMessage()));
            return "error";
        }
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doGroupSearchFilterExistingGroups() {
        String result = this.doGroupSearch();
        if (this.getActionErrors().isEmpty()) {
            FilterChain filter = new FilterChain();
            filter.addFilter(new ExistingGroupsFilter(this.existingGroups));
            this.setGroups(this.filterContent(this.paginationSupport.getPage(), filter));
        }
        return result;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doFilterAlreadyPermittedGroupSearch() {
        if (!StringUtils.isNotEmpty((CharSequence)this.groupnameTerm)) {
            this.groupnameTerm = "*";
        }
        this.doGroupSearch();
        FilterChain filter = new FilterChain();
        filter.addFilter(new GroupsWithPermissionFilter(this.getKey(), this.spaceManager));
        this.setGroups(this.filterContent(this.paginationSupport.getPage(), filter));
        return "success";
    }

    private List filterContent(Collection<Group> groups, FilterChain filter) {
        if (groups == null) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<Group> filteredList = new ArrayList<Group>();
        for (Group group : groups) {
            if (filter.isIncluded(group)) {
                filteredList.add(group);
                continue;
            }
            this.excludedGroups.add(group);
        }
        return filteredList;
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Collection getGroups() {
        return this.groups;
    }

    public void setGroups(Collection groups) {
        this.groups = groups;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public List getExcludedGroups() {
        return this.excludedGroups;
    }

    public String getCommaSeparatedExcludedGroupNames() {
        ArrayList<String> groupNames = new ArrayList<String>(this.excludedGroups.size());
        for (Group excludedGroup : this.excludedGroups) {
            groupNames.add(excludedGroup.getName());
        }
        return StringUtils.join(groupNames, (String)", ");
    }

    public String getExistingGroups() {
        return this.existingGroups;
    }

    public void setExistingGroups(String existingGroups) {
        this.existingGroups = existingGroups;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public String getOnPopupSubmit() {
        return this.onPopupSubmit;
    }

    public void setOnPopupSubmit(String onPopupSubmit) {
        this.onPopupSubmit = onPopupSubmit;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSearchEntitiesManager(SearchEntitiesManager searchEntitiesManager) {
        this.searchEntitiesManager = searchEntitiesManager;
    }

    public String getGroupnameTerm() {
        return this.groupnameTerm;
    }

    public void setGroupnameTerm(String groupnameTerm) {
        this.groupnameTerm = groupnameTerm;
    }

    private static class ExistingGroupsFilter
    implements Filter {
        private List<String> existingGroupsList;

        public ExistingGroupsFilter(String existingGroups) {
            this.existingGroupsList = LabelUtil.split(existingGroups);
        }

        public boolean isIncluded(Object object) {
            if (object instanceof Group) {
                Group group = (Group)object;
                return !this.existingGroupsList.contains(group.getName());
            }
            return false;
        }
    }

    private class GroupsWithPermissionFilter
    implements Filter {
        private String spaceKey;
        private SpaceManager spaceManager;

        public GroupsWithPermissionFilter(String spaceKey, SpaceManager spaceManager) {
            this.spaceKey = spaceKey;
            this.spaceManager = spaceManager;
        }

        public boolean isIncluded(Object object) {
            if (object instanceof Group) {
                Group group;
                Space space = this.spaceManager.getSpace(this.spaceKey);
                Collection<Group> spaceGroups = GroupPickerAction.this.spacePermissionManager.getGroupsWithPermissions(space);
                return !spaceGroups.contains(group = (Group)object);
            }
            return true;
        }
    }

    private static class FilterChain
    implements Filter {
        List<Filter> filterList = new ArrayList<Filter>();

        private FilterChain() {
        }

        public void addFilter(Filter filter) {
            this.filterList.add(filter);
        }

        public boolean isIncluded(Object o) {
            for (Filter filter : this.filterList) {
                if (filter.isIncluded(o)) continue;
                return false;
            }
            return true;
        }
    }
}

