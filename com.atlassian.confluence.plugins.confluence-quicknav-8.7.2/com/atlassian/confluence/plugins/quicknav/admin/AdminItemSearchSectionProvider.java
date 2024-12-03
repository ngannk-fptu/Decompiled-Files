/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.actions.json.ContentNameMatch
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchContext
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchSection
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider
 *  com.atlassian.confluence.search.contentnames.QueryToken
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.quicknav.admin;

import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSection;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class AdminItemSearchSectionProvider
implements ContentNameSearchSectionsProvider {
    private static final int MAX_RESULTS = 3;
    private static final String LOCATION = "system.admin";
    private static final int WEIGHT = 5;
    private final WebInterfaceManager webInterfaceManager;
    private final PermissionManager permissionManager;

    public AdminItemSearchSectionProvider(WebInterfaceManager webInterfaceManager, PermissionManager permissionManager) {
        this.webInterfaceManager = webInterfaceManager;
        this.permissionManager = permissionManager;
    }

    public Collection<ContentNameSearchSection> getSections(List<QueryToken> queryTokens, ContentNameSearchContext context) {
        boolean confAdmin = this.permissionManager.isConfluenceAdministrator((User)AuthenticatedUserThreadLocal.get());
        boolean typeSpecificSearch = context.getTypes().iterator().hasNext();
        if (!confAdmin || typeSpecificSearch) {
            return null;
        }
        List<ContentNameMatch> contentNameMatches = this.findAdminItems(queryTokens);
        return Collections.singletonList(new ContentNameSearchSection(Integer.valueOf(5), contentNameMatches));
    }

    private Map<String, Object> contextForWebInterfaceManager() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return Collections.singletonMap("user", user);
    }

    private List<ContentNameMatch> findAdminItems(List<QueryToken> queryTokens) {
        Map<String, Object> context = this.contextForWebInterfaceManager();
        HttpServletRequest servletRequest = ServletContextThreadLocal.getRequest();
        return this.webInterfaceManager.getDisplayableSections(LOCATION, context).stream().flatMap(section -> {
            String sectionKey = "system.admin/" + section.getKey();
            return this.webInterfaceManager.getDisplayableItems(sectionKey, context).stream().map(item -> new SectionItem((WebSectionModuleDescriptor)section, (WebItemModuleDescriptor)item));
        }).filter(sectionItem -> {
            String label = sectionItem.item.getWebLabel().getDisplayableLabel(servletRequest, context);
            return queryTokens.stream().allMatch(token -> StringUtils.containsIgnoreCase((CharSequence)label, (CharSequence)token.getText()));
        }).limit(3L).map(sectionItem -> {
            ContentNameMatch match = new ContentNameMatch();
            match.setClassName("admin-item");
            match.setHref(sectionItem.item.getLink().getDisplayableUrl(servletRequest, context));
            match.setName(sectionItem.item.getWebLabel().getDisplayableLabel(servletRequest, context));
            match.setSpaceName(sectionItem.section.getWebLabel().getDisplayableLabel(servletRequest, context));
            return match;
        }).collect(Collectors.toList());
    }

    private static final class SectionItem {
        final WebSectionModuleDescriptor section;
        final WebItemModuleDescriptor item;

        private SectionItem(WebSectionModuleDescriptor section, WebItemModuleDescriptor item) {
            this.section = section;
            this.item = item;
        }
    }
}

